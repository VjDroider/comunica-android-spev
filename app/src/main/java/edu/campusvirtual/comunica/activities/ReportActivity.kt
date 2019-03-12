package edu.campusvirtual.comunica.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import com.kaopiz.kprogresshud.KProgressHUD
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.adapters.MessageAdapter
import edu.campusvirtual.comunica.adapters.ReportDetailAdapter
import edu.campusvirtual.comunica.fragments.SubTabAdapterMsg
import edu.campusvirtual.comunica.library.CustomViewPager
import edu.campusvirtual.comunica.library.EmptyRecyclerView
import edu.campusvirtual.comunica.models.inbox.MessageCOM
import edu.campusvirtual.comunica.models.inbox.MessageDB
import edu.campusvirtual.comunica.models.inbox.Report
import edu.campusvirtual.comunica.models.inbox.SingleReport
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.getReportDetailsCOM
import io.realm.RealmResults

class ReportActivity : AppCompatActivity() {

    var reports = ArrayList<SingleReport>()
    var recycler: RecyclerView? = null
    var adapter: ReportDetailAdapter? = null
    var layoutManager: LinearLayoutManager? = null
    var spin: KProgressHUD? = null
    var report: Report? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        report = intent.extras.getSerializable("report") as Report
        setup()
    }

    fun setup() {
        val recyclerViewOrientation = LinearLayout.VERTICAL
        val dividerDecoration = DividerItemDecoration(this, recyclerViewOrientation)
        val layoutManager = LinearLayoutManager(this)

        recycler = findViewById(R.id.recyclerViewId)
        recycler!!.layoutManager = layoutManager
        adapter = ReportDetailAdapter(reports)
        recycler!!.adapter = adapter
        recycler!!.addItemDecoration(dividerDecoration)

        makeRequests()
    }

    fun makeRequests() {
        Service.shared().getReportDetailsCOM(this, report?._id_Reporte_Envio!!, completion = {
            reports.clear()
            reports.addAll(it)

            adapter!!.notifyDataSetChanged()
        }, failure = {

        })
    }
}
