package edu.campusvirtual.comunica.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.campusvirtual.comunica.fragments.CustomNewsFragment
import edu.campusvirtual.comunica.fragments.ItemContentFragment
import edu.campusvirtual.comunica.models.banner.Banner
import edu.campusvirtual.comunica.models.banner.BannerCOM
import edu.campusvirtual.comunica.models.item.Item
import edu.campusvirtual.comunica.models.item.ItemCOM
import edu.campusvirtual.comunica.R
import java.io.Serializable

class ItemActivity : AppCompatActivity() {

    var item: ItemCOM? = null
    var banners: List<BannerCOM>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        item = intent.getSerializableExtra("Item") as ItemCOM
        banners = intent.getSerializableExtra("Banner") as List<BannerCOM>

        val fm = supportFragmentManager.beginTransaction()

        val itemContentFragment = ItemContentFragment()
        itemContentFragment.item = item!!
        fm.replace(R.id.contentFragmentId, itemContentFragment)

        fm.commit()

    }
}
