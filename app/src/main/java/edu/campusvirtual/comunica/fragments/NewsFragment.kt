package edu.campusvirtual.comunica.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.kaopiz.kprogresshud.KProgressHUD
import com.synnapps.carouselview.CarouselView

import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.item.ItemCOM
import edu.campusvirtual.comunica.models.configuration.Configuration
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.getItemsCOM


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NewsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class NewsFragment : Fragment(), ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {
    var rootView: View? = null
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var carousel: CarouselView
    lateinit var subTabAdapter: SubTabAdapter
    lateinit var sections: ArrayList<String>
    lateinit var items: ArrayList<ItemCOM>
    var fragmentList = ArrayList<Fragment>()
    var spin: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DDDD", "NewsFragment onCreateView")
        super.onCreate(savedInstanceState)

        sections = ArrayList<String>()
        // var items = ArrayList<Item>()

        spin = Util.loadingPage(context!!, null, spin)
        Service.shared().getItemsCOM(context!!, completion = { myitems ->
            this.items = myitems?.filter { it.Nombre_TipoItem == "Descripcion" } as ArrayList<ItemCOM>
            for(item in items) {
                if(sections.indexOf(item.seccion.toLowerCase()) == -1) {
                    var section = item.seccion.toLowerCase()
                    if(section != "submenu") {
                        sections.add(section)
                        tabLayout.addTab(tabLayout.newTab().setText(section))
                        fragmentList.add(CustomNewsFragment.createFragment(section, items.filter { i -> i.seccion.toLowerCase() == section }))

                    }
                }
            }
            subTabAdapter.notifyDataSetChanged()
            stopLoading()

            val config = Configuration.getConfiguration(context!!, Configuration.seccionseleccionadaafterlogin)
            if(config != null) {
                var position = getPositionTabSelected(config.value)
                tabLayout.setScrollPosition(position, 0.toFloat(), true)
                viewPager.currentItem = position
            }
        }, failure = {
            // fail
            stopLoading()
        })

    }

    fun getPositionTabSelected(value: String): Int {
        var pos = 0
        for(i in 0..sections.size - 1) {
            val section = sections.get(i)

            if(section == value) {
                pos = i
                break
            }
        }

        return pos
    }

    fun stopLoading() {
        Util.stopLoadingPage(spin)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Log.d("DDDD", "NewsFragment onCreateView")
        // Inflate the layout for this fragment
        if(rootView == null) {
            Log.d("DD", "1")
            rootView = inflater.inflate(R.layout.fragment_news, container, false)
            tabLayout = rootView!!.findViewById(R.id.tabLayoutId)
            fragmentList = ArrayList<Fragment>()
            viewPager = rootView!!.findViewById(R.id.viewPagerNewsId)

            subTabAdapter = SubTabAdapter(fragmentManager!!, context!!, fragmentList)
            viewPager.adapter = subTabAdapter
            viewPager.addOnPageChangeListener(this)
            tabLayout.addOnTabSelectedListener(this)
            tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.white))
        } else {
            Log.d("DD", "2")
        }

        return rootView
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        tabLayout.setScrollPosition(position, 0.toFloat(), true)
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
            viewPager.currentItem = tab.position
        }
    }
}

class SubTabAdapter(manager: FragmentManager, private val context: Context, private val mFragmentList: ArrayList<Fragment>) : FragmentStatePagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return this.mFragmentList[position] as CustomNewsFragment
    }

    override fun getCount(): Int {
        return this.mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return ""
    }

}
