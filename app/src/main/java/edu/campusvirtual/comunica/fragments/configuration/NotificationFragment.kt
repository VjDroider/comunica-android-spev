package edu.campusvirtual.comunica.fragments.configuration

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import edu.campusvirtual.comunica.models.configuration.PersonalConfiguration
import edu.campusvirtual.comunica.models.configuration.PersonalRequest

import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.getPersonalConfig
import edu.campusvirtual.comunica.services.updatePersonalConfig


class NotificationFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    var configurations = ArrayList<PersonalConfiguration>()
    var notificationsId = 0
    var emailId = 0
    var smsId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_notification, container, false)

        var switchNotifications = v.findViewById<Switch>(R.id.switchNotificaciones)

        var switchEmail = v.findViewById<Switch>(R.id.switchEmails)
        var switchSms = v.findViewById<Switch>(R.id.switchSms)

        Service.shared().getPersonalConfig(context!!, completion = { configs ->
            this.configurations.clear()
            this.configurations.addAll(configs!!)

            for(config in this.configurations) {
                if(config.name == "Notificaciones") {
                    switchNotifications.isChecked = config.active
                    notificationsId = config.id
                } else if(config.name == "Emails") {
                    switchEmail.isChecked = config.active
                    emailId = config.id
                } else if(config.name == "SMS") {
                    smsId = config.id
                    switchSms.isChecked = config.active
                }
            }
        }, failure = {
            Log.d("ocnfigs", "JAJAJA")
        })

        switchNotifications.setOnCheckedChangeListener(this)
        switchEmail.setOnCheckedChangeListener(this)
        switchSms.setOnCheckedChangeListener(this)

        return v
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        Log.d("HEREE", "group "+ buttonView?.id + "and checked" + isChecked)
        var id = 0
        when(buttonView?.id) {
            R.id.switchNotificaciones -> {
                id = notificationsId
            }
            R.id.switchEmails -> {
                id = emailId
            }
            R.id.switchSms -> {
                id = smsId
            }
        }

        if(id != 0) {
            update(id, isChecked)
        }

    }

    fun update(id: Int, active: Boolean) {
        val body = PersonalRequest()
        body.active = active
        body.id = id
        Service.shared().updatePersonalConfig(context!!, body, completion = { config ->
            for(c in this.configurations) {
                if(config?.id == c.id) {
                    c.active = config.active
                }
            }
        }, failure = {

        })

    }


}