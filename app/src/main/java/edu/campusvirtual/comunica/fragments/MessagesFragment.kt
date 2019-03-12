package edu.campusvirtual.comunica.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.kaopiz.kprogresshud.KProgressHUD
import edu.campusvirtual.comunica.adapters.MessageAdapter
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.library.*
import edu.campusvirtual.comunica.models.inbox.Message
import edu.campusvirtual.comunica.models.inbox.MessageCOM
import edu.campusvirtual.comunica.models.inbox.MessageDB
import edu.campusvirtual.comunica.models.inbox.Response
import edu.campusvirtual.comunica.models.item.ItemCOM
import edu.campusvirtual.comunica.models.template.Template

import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.activities.SendMessageActivity
import edu.campusvirtual.comunica.activities.ViewMessageActivity
import edu.campusvirtual.comunica.services.*
import io.realm.Realm
import io.realm.RealmResults
import me.leolin.shortcutbadger.ShortcutBadger
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MessagesFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MessagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MessagesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
        ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {

    var messages = ArrayList<MessageCOM>()
    // var messagesSegmented:Map<String, ArrayList<MessageCOM>> = HashMap()
    var recycler: EmptyRecyclerView? = null
    var adapter: MessageAdapter? = null
    var layoutManager: LinearLayoutManager? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var mailbox: String? = null
    var spin: KProgressHUD? = null
    var messagesDB: RealmResults<MessageDB>? = null
    var subTabAdapter: SubTabAdapterMsg? = null
    val sections = ArrayList<String>()

    var rootView: View? = null
    var tabLayout:TabLayout? = null
    var viewPager:CustomViewPager? = null
    var fragmentList: ArrayList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mailbox = "Recibidos"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_messages, container, false)
            mailbox = "Recibidos"
            tabLayout = rootView!!.findViewById<TabLayout>(R.id.tabLayoutId)
            viewPager = rootView!!.findViewById<CustomViewPager>(R.id.viewPagerNewsId)
            fragmentList = ArrayList<Fragment>()
            // setupRecyclerView(v)

            // setupEmptyView(v)

            // setupSwipeRefresh(v)

            setHasOptionsMenu(true)

            subTabAdapter = SubTabAdapterMsg(fragmentManager!!, context!!, fragmentList)

            viewPager!!.adapter = subTabAdapter
            // viewPager!!.addOnPageChangeListener(this)
            tabLayout!!.addOnTabSelectedListener(this)
            tabLayout!!.setSelectedTabIndicatorColor(resources.getColor(R.color.white))

            // getMessages()

        }

        return rootView
    }


    fun setupSwipeRefresh(v: View) {
        swipeRefreshLayout = v.findViewById<SwipeRefreshLayout>(R.id.swipeContainerId)
        swipeRefreshLayout?.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        getMessages()
    }

    fun stopLoading() {
        Util.stopLoadingPage(spin)
    }

    fun getMessages() {
        // getMessagesDB()
        countMessages()
        spin = Util.loadingPage(context!!, null, spin)
        Service.shared().getMessagesSentCOM(context!!, completion = {
            messages.clear()
            messages.addAll(it)

            // adapter?.notifyDataSetChanged()
            sync()

            stopLoading()
            // swipeRefreshLayout?.isRefreshing = false

        }, failure = {
            stopLoading()
            // swipeRefreshLayout?.isRefreshing = false
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        val inflater = inflater

        inflater?.inflate(R.menu.messages_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {

            R.id.addMessageMenuId -> {
                val intent = Intent(context!!, SendMessageActivity::class.java)
                // intent.putExtra("Template", Template("", "", ""))
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun getMessagesDB() {
        Realm.init(context!!)
        val config = getDefaultConfig("messages.realm")
        val realm = Realm.getInstance(config)

        messagesDB = realm.where(MessageDB::class.java).equalTo("mailbox", mailbox!!).findAll()

        messages.clear()

        var msgs = messagesDB!!.sortedByDescending { it.id }

        for(m in msgs) {
            messages.add(m.toMessageCOM())
        }


        if(tabLayout!!.tabCount == 0) {
            sections.clear()
            var section = "Todos"
            sections.add(section)
            tabLayout?.addTab(tabLayout!!.newTab().setText(section))
            fragmentList.add(CustomMessagesFragment.createFragment(section, messages, mailbox!!, false, messagesDB!!))
            section = "Por tema"
            sections.add(section)
            tabLayout?.addTab(tabLayout!!.newTab().setText(section))
            fragmentList.add(CustomMessagesFragment.createFragment(section, messages, mailbox!!, true, messagesDB!!))


            subTabAdapter!!.notifyDataSetChanged()
        }

        // adapter?.notifyDataSetChanged()
    }

    fun getDateString(date: Date?) : String? {
        val locale = Locale("es", "mx")
        val format = SimpleDateFormat("yyyy-MM-dd'T00:00:00'")

        try {
            var dateTime = format.format(date)

            return dateTime
        } catch(e: Throwable) {
            Log.d("ERROR", "message: " + e.message)
            return null
        }
    }

    fun sync() {
        Realm.init(context!!)



        val config = getDefaultConfig("messages.realm")
        val realm = Realm.getInstance(config)
        // 1 - eliminar mensajes del mailbox X
        realm.beginTransaction()
        val results = realm.where(MessageDB::class.java).contains("mailbox", mailbox!!).findAll()
        // results.deleteAllFromRealm()
        // 2 - agregó los nuevos elementos a la base de datos

        for(message in messages) {
            var exists = false
            for(m in results!!) {
                if(m.id == message.id_Mensaje) {
                    exists = true
                }
            }

            if(!exists) {
                realm.copyToRealm(message.messageToDb(mailbox!!))
            }
        }

        var session = SessionManager(context!!)
        var date = Calendar.getInstance().time
        var x = getDateString((date))!!
        session.saveLastGet(x)

        realm.commitTransaction()

        getMessagesDB()
    }

    fun countMessages() {

        Service.shared()

        Service.shared().getUnreadCountMessagesCOM(context!!, completion = { count ->
            //
            // setCountInInbox(count)
            ShortcutBadger.applyCount(context!!, count)
            adapter?.notifyDataSetChanged()
        }, failure = {
            // setCountInInbox()
            Log.d("FAIL", "SIIIIII")
        })
    }

    fun findMessageIndex(id: Int) : Int? {
        for(i in messages.indices) {
            if(messages[i].id_Mensaje == id) {
                return i
            }
        }

        return null
    }

    fun findMessage(pos: Int): MessageDB? {
        var msg = messages.get(pos)
        var finded = messagesDB?.find { it.id == msg.id_Mensaje }

        if(finded == null) {
            return null
        }

        return finded
    }

    fun markAsDelete(msg: MessageDB) {
        val config = getDefaultConfig("messages.realm")
        val realm = Realm.getInstance(config)

        realm.beginTransaction()
        msg.deleteFromRealm()
        realm.commitTransaction()
    }

    fun deleteMessage(position: Int) {
        if(mailbox == "Pendiente") {
            val config = getDefaultConfig("messages.realm")
            val realm = Realm.getInstance(config)

            realm.beginTransaction()
            messagesDB?.get(position)?.deleteFromRealm()
            realm.commitTransaction()
        } else {

            if(Constants.backend == Constants.COMUNICA) {
                var msg = findMessage(position)

                if(msg != null) {
                    Service.shared().markMessageAsReadCOM(context!!, msg.id, completion = {
                        markAsDelete(msg)
                    }, failure = {
                        markAsDelete(msg)
                    })
                }

            } else {
                Service.shared().markMessageAsDelete(context!!, messages.get(position).id_Mensaje).enqueue(object: Callback<Any> {
                    override fun onResponse(call: Call<Any>?, response: retrofit2.Response<Any>?) {
                    }

                    override fun onFailure(call: Call<Any>?, t: Throwable?) {
                    }


                })
            }
        }

    }

    override fun onResume() {
        super.onResume()

        getMessages()
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        tabLayout?.setScrollPosition(position, 0.toFloat(), true)
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
            viewPager?.currentItem = tab.position
        }
    }

}

class SubTabAdapterMsg(manager: FragmentManager, private val context: Context, private val mFragmentList: ArrayList<Fragment>) : FragmentStatePagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return this.mFragmentList[position] as CustomMessagesFragment
    }

    override fun getCount(): Int {
        return this.mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return ""
    }

}