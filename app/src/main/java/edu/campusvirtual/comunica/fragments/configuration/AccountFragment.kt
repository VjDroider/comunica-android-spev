package edu.campusvirtual.comunica.fragments.configuration

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.iid.FirebaseInstanceId
import io.realm.Realm
import io.realm.RealmConfiguration
import edu.campusvirtual.comunica.activities.HomeActivity
import edu.campusvirtual.comunica.library.RealMigrations
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.getDefaultConfig
import edu.campusvirtual.comunica.models.inbox.MessageDB
import edu.campusvirtual.comunica.models.receiver.ReceiverDB

import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.updateDeviceToken


class AccountFragment : Fragment(), View.OnClickListener {


    var sessionManager: SessionManager ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_account, container, false)

        val logoutText = v.findViewById<TextView>(R.id.LogoutId)

        logoutText.setOnClickListener(this)
        sessionManager = SessionManager(context!!)

        return v
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.LogoutId -> {
                Realm.init(context)
                var config = getDefaultConfig("messages.realm")
                var realm = Realm.getInstance(config)

                realm.beginTransaction()
                realm.where(MessageDB::class.java).findAll().deleteAllFromRealm()
                realm.commitTransaction()

                config = getDefaultConfig("receivers.realm")
                realm = Realm.getInstance(config)

                realm.beginTransaction()
                realm.where(ReceiverDB::class.java).findAll().deleteAllFromRealm()
                realm.commitTransaction()


                realm.close()

                val sessionManager = SessionManager(context!!)
                sessionManager.destroySession()

                val intent = Intent(activity, HomeActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
    }

}
