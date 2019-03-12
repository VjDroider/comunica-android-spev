package edu.campusvirtual.comunica.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaopiz.kprogresshud.KProgressHUD
import com.squareup.picasso.Picasso
import com.synnapps.carouselview.CarouselView

import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.activities.ItemActivity
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.banner.BannerCOM
import edu.campusvirtual.comunica.adapters.NewsAdapter
import edu.campusvirtual.comunica.models.item.ItemCOM
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.getBannersCOM

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CustomNewsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CustomNewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CustomNewsFragment : Fragment() {
    lateinit var carouselView: CarouselView

    private var title: String? = null
    var banners: List<BannerCOM> = ArrayList()

    lateinit var items: List<ItemCOM>
    var spin: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val x =  inflater.inflate(R.layout.fragment_custom_news, container, false)

        val recycler: RecyclerView = x.findViewById<RecyclerView>(R.id.itemsRecyclerViewId)
        val orientation = LinearLayout.VERTICAL

        val adapter = NewsAdapter(items) {
            val intent = Intent(context, ItemActivity::class.java)
            intent.putExtra("Item", it)
            intent.putExtra("Banner", banners as ArrayList)
            startActivity(intent)
        }
        carouselView = x.findViewById(R.id.carouselViewId)
        carouselView.pageCount = banners.size

        carouselView.setImageListener({ i: Int, imageView: ImageView ->
            Picasso.with(context).load(banners.get(i).imgsrc).fit().into(imageView)
        })

        recycler.layoutManager = LinearLayoutManager(context, orientation, false)

        recycler.adapter = adapter
        val dividerDecoration = DividerItemDecoration(recycler.context, orientation)

        recycler.addItemDecoration(dividerDecoration)

        // spin = Util.loadingPage(context!!, null, spin)
        Service.shared().getBannersCOM(context!!, completion = { banners ->
            this.banners = banners!!.filter { b -> b.seccion.toLowerCase() == this.title?.toLowerCase() }
            carouselView.pageCount = this.banners.size
            stopLoading()
        }, failure = {
            // fail
            stopLoading()
        })

        return x
    }

    fun stopLoading() {
        Util.stopLoadingPage(spin)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



    companion object {

        fun createFragment(section: String, items: List<ItemCOM>): CustomNewsFragment {
            val customFragment = CustomNewsFragment()
            customFragment.title = section
            customFragment.items = items
            return customFragment
        }

    }
}
