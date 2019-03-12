package edu.campusvirtual.comunica.fragments.configuration

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import edu.campusvirtual.comunica.R
import android.content.pm.PackageManager
import android.R.attr.versionName
import android.content.pm.PackageInfo
import android.widget.TextView
import androidx.fragment.app.Fragment


class AboutFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_about, container, false)

        var versionText = v.findViewById<TextView>(R.id.titleVersion)
        try {
            val pInfo = activity?.packageManager?.getPackageInfo(activity!!.packageName, 0)
            val version = pInfo?.versionName

            versionText.text = "versión " + version
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


        return v
    }

}