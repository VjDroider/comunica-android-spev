package edu.campusvirtual.comunica.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alphabetik.Alphabetik
import com.kaopiz.kprogresshud.KProgressHUD
import edu.campusvirtual.comunica.activities.ViewMessageActivity
import edu.campusvirtual.comunica.adapters.MessageAdapter
import edu.campusvirtual.comunica.library.*
import edu.campusvirtual.comunica.models.banner.BannerCOM
import edu.campusvirtual.comunica.models.inbox.MessageCOM
import edu.campusvirtual.comunica.models.inbox.MessageDB
import edu.campusvirtual.comunica.models.item.ItemCOM

import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.getMessagesSentCOM
import edu.campusvirtual.comunica.services.markMessageAsDelete
import edu.campusvirtual.comunica.services.markMessageAsReadCOM
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class CustomMessagesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var title: String? = null
    lateinit var items: ArrayList<MessageCOM>
    var itemsSegmented: ArrayList<MessageCOM> = ArrayList()

    private var isSegmented = false
    var recycler: EmptyRecyclerView? = null
    var adapter: MessageAdapter? = null
    var layoutManager: LinearLayoutManager? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var spin: KProgressHUD? = null
    var messagesDB: RealmResults<MessageDB>? = null
    var mailbox:String = ""

    private var mListener: CustomMessageListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_custom_messages, container, false)

        setupRecyclerView(v)

        // setupEmptyView(v)

        setupSwipeRefresh(v)

        return v
    }

    fun getAlphabetik(): Array<String> {
        var sections:ArrayList<String> = arrayListOf()

        for(i in 0..items.size - 1) {
            val msg = items.get(i)
            var firstLetter = ""
            if(!msg.Tema.trim().isEmpty()) {
                firstLetter = msg.Tema.trim().substring(0, 1)
            }

            if(!sections.contains(firstLetter.toUpperCase())) {
                sections.add(firstLetter.toUpperCase())
            }
        }

        var array = arrayOfNulls<String>(sections.size)
        return sections.toArray(array)
    }

    fun getPosition(by: String): Int {
        var position = 0

        for(i in 0..itemsSegmented.size - 1) {
            val msg = itemsSegmented.get(i)

            if(msg.header) {

                var firstLetter = ""
                if(!msg.Tema.trim().isEmpty()) {
                    firstLetter = msg.Tema.trim().substring(0, 1)
                }

                if(firstLetter.toUpperCase() == by.toUpperCase()) {
                    position = i
                    break
                }
            }
        }

        return position
    }

    fun setupRecyclerView(v: View) {
        val orientation = LinearLayout.VERTICAL
        // val emptyView = v.findViewById<ConstraintLayout>(R.id.todo_list_empty_view)

        recycler = v.findViewById<EmptyRecyclerView>(R.id.messagesRecyclerId)
        val dividerDecoration = DividerItemDecoration(context!!, orientation)

        recycler?.addItemDecoration(dividerDecoration)

        val alphabetik = v.findViewById<Alphabetik>(R.id.alphSectionIndex)
        alphabetik.setAlphabet(getAlphabetik())

        alphabetik.onSectionIndexClickListener(object: Alphabetik.SectionIndexClickListener {
            override fun onItemClick(view: View?, position: Int, character: String?) {
                val pos = getPosition(character!!)
                recycler!!.scrollToPosition(pos)
            }

        })

        // recycler.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context!!)
        recycler!!.layoutManager = layoutManager

        if(!isSegmented) {
            alphabetik.visibility = View.GONE
            adapter = MessageAdapter(items, context!!, mailbox!!) {
                if(mailbox == "Recibidos") {
                    items[findMessageIndex(it.id_Mensaje)!!].Leido = true
                    adapter?.notifyDataSetChanged()
                }

                if(mailbox != "Pendiente") {
                    val intent = Intent(context!!, ViewMessageActivity::class.java)
                    intent.putExtra("message", it)
                    intent.putExtra("mailbox", mailbox)
                    startActivity(intent)
                }

            }
        } else {
            alphabetik.visibility = View.VISIBLE
            itemsSegmented = segmentItems()
            adapter = MessageAdapter(itemsSegmented, context!!, mailbox!!) {
                if(mailbox == "Recibidos") {
                    items[findMessageIndex(it.id_Mensaje)!!].Leido = true
                    adapter?.notifyDataSetChanged()
                }

                if(mailbox != "Pendiente") {
                    val intent = Intent(context!!, ViewMessageActivity::class.java)
                    intent.putExtra("message", it)
                    intent.putExtra("mailbox", mailbox)
                    startActivity(intent)
                }

            }
        }



        // recycler!!.setEmptyView(emptyView)

        recycler!!.adapter = adapter

        recycler!!.itemAnimator = DefaultItemAnimator()


        adapter!!.notifyDataSetChanged()
        val swipeHandler = object : SwipeController(context!!) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteMessage(viewHolder.adapterPosition)
                adapter!!.removeAt(viewHolder.adapterPosition)

            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recycler)

    }

    fun segmentItems(): ArrayList<MessageCOM> {
        var msgs = ArrayList<MessageCOM>()
        var temas = getThemes()

        for (tema in temas) {
            msgs.add(MessageCOM(0,"",false,"",false,null,false, tema, null,true))
            msgs.addAll(getMessagesByTheme(tema))
        }

        return msgs
    }

    fun getMessagesByTheme(tema: String): ArrayList<MessageCOM> {
        var newArray = ArrayList<MessageCOM>()

        for(element in items) {
            if(element.Tema.trim().toString() == tema.trim()) {
                newArray.add(element)
            }
        }

        return ArrayList(newArray.sortedWith(compareBy(MessageCOM::id_Mensaje, MessageCOM::id_Mensaje)))
    }

    fun getThemes(): ArrayList<String> {
        val themes = ArrayList<String>()

        for (m in items){
            if(themes.indexOf(m.Tema) == -1) {
                themes.add(m.Tema.trim())
            }
        }

        return ArrayList(themes.sorted())
    }

    fun findMessageIndex(id: Int) : Int? {
        for(i in items.indices) {
            if(items[i].id_Mensaje == id) {
                return i
            }
        }

        return null
    }

    fun findMessage(pos: Int): MessageDB? {
        var msg = items.get(pos)

        if(isSegmented) {
            msg = itemsSegmented.get(pos)
        }

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
        try {
            msg.deleteFromRealm()
        } catch(e: Exception) {

        }
        realm.commitTransaction()

        getMessagesDB()
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
                Service.shared().markMessageAsDelete(context!!, items.get(position).id_Mensaje).enqueue(object: Callback<Any> {
                    override fun onResponse(call: Call<Any>?, response: retrofit2.Response<Any>?) {
                    }

                    override fun onFailure(call: Call<Any>?, t: Throwable?) {
                    }


                })
            }
        }

    }


    fun setupSwipeRefresh(v: View) {
        swipeRefreshLayout = v.findViewById<SwipeRefreshLayout>(R.id.swipeContainerId)
        swipeRefreshLayout?.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        getMessages()
        // Util.stopLoadingPage(spin)
        // swipeRefreshLayout?.isRefreshing = false
        mListener!!.onRefreshMessages()
    }

    fun sync() {
        try {
            Realm.init(context!!)



            val config = getDefaultConfig("messages.realm")
            val realm = Realm.getInstance(config)
            // 1 - eliminar mensajes del mailbox X
            realm.beginTransaction()
            val results = realm.where(MessageDB::class.java).contains("mailbox", mailbox!!).findAll()
            // results.deleteAllFromRealm()
            // 2 - agregó los nuevos elementos a la base de datos

            for(message in items) {
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
        } catch(e: Exception) {
            Toast.makeText(context!!, "Error al sincronizar los mensajes", Toast.LENGTH_SHORT).show()
        }
    }

    fun getMessagesDB() {
        Realm.init(context!!)
        val config = getDefaultConfig("messages.realm")
        val realm = Realm.getInstance(config)

        messagesDB = realm.where(MessageDB::class.java).equalTo("mailbox", mailbox!!).findAll()

        items.clear()

        var msgs = messagesDB!!.sortedByDescending { it.id }

        for(m in msgs) {
            items.add(m.toMessageCOM())
        }

        if(!isSegmented) {
            adapter?.notifyDataSetChanged()
        } else {
            itemsSegmented = segmentItems()
            adapter?.notifyDataSetChanged()
        }

        swipeRefreshLayout?.isRefreshing = false
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

    fun getMessages() {
        // getMessagesDB()
        spin = Util.loadingPage(context!!, null, spin)
        Service.shared().getMessagesSentCOM(context!!, completion = {
            items.clear()
            items.addAll(it)

            // adapter?.notifyDataSetChanged()
            sync()

            stopLoading()
            // swipeRefreshLayout?.isRefreshing = false

        }, failure = {
            stopLoading()
            // swipeRefreshLayout?.isRefreshing = false
        })


    }

    fun stopLoading() {
        Util.stopLoadingPage(spin)
    }

    companion object {

        fun createFragment(section: String, items: ArrayList<MessageCOM>, mailbox: String, segmented: Boolean, msgDB: RealmResults<MessageDB>): CustomMessagesFragment {
            val customFragment = CustomMessagesFragment()
            customFragment.title = section
            customFragment.items = items
            customFragment.mailbox = mailbox
            customFragment.isSegmented = segmented
            customFragment.messagesDB = msgDB
            return customFragment
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CustomMessagesFragment.CustomMessageListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement onRefreshMessages")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface CustomMessageListener {
        fun onRefreshMessages()
    }
}
