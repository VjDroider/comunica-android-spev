package edu.campusvirtual.comunica.library

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import edu.campusvirtual.comunica.models.inbox.MessageDB
import edu.campusvirtual.comunica.models.inbox.Request
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.sentMessage

class NetworkStateReceiver: BroadcastReceiver() {

    companion object {
        val TAG = "NETWORKSTATECHANGED"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.getExtras() != null) {
            val connectivityManager : ConnectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni: NetworkInfo? = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnectedOrConnecting()) {
                Log.i(TAG, "Network " + ni.getTypeName() + " connected");
                // mandar mensajes pendientes
                // 1 revisar que no tengan mas de 3 intentos
                Realm.init(context)
                val config = getDefaultConfig("messages.realm")
                val realm = Realm.getInstance(config)

                val messages = realm.where(MessageDB::class.java).contains("mailbox", "Pendiente").findAll()

                for(m in messages) {
                    if(m.trying <= 3) {
                        // lo enviamos
                        val body = Request()

                        body.person_ids = m.person_ids
                        body.distribution_list_ids = m.distribution_list_ids
                        body.subject = m.subject
                        body.message = m.message!!
                        Service.shared().sentMessage(context, body, completion = {
                            realm.beginTransaction()
                            m.deleteFromRealm()
                            realm.commitTransaction()
                        }, failure = {
                            realm.beginTransaction()
                            m.trying += 1
                            realm.commitTransaction()
                        })
                    }
                }

            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                Log.d(TAG, "There's no network connectivity");
            }
        }
    }
}
