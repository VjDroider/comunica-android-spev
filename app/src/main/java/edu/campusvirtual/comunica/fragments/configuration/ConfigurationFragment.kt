package edu.campusvirtual.comunica.fragments.configuration


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import edu.campusvirtual.comunica.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ConfigurationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_configuration, container, false)

        val fragmentNotifications = fragmentManager?.beginTransaction()

        fragmentNotifications?.replace(R.id.fragmentNotificationId2, AboutFragment())
        fragmentNotifications?.replace(R.id.fragmentNotificationId3, AccountFragment())
        fragmentNotifications?.replace(R.id.fragmentNotificationId4, SignatureFragment())

        fragmentNotifications?.commit()


        return v
    }


}
