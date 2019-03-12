package edu.campusvirtual.comunica.library

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.iid.InstanceID
import com.google.android.gms.gcm.GoogleCloudMessaging
import edu.campusvirtual.comunica.R


/**
 * Created by jonathan on 2/27/18.
 */
class RegistrationService: IntentService("RegistrationService") {

    override fun onHandleIntent(intent: Intent?) {
        val myID = InstanceID.getInstance(this)

        val registrationToken = myID.getToken(
                "594605988543",
                GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                null
        )

        val sessionManager = SessionManager(applicationContext)
        sessionManager.onRegisterSuccess(registrationToken)
        Log.d("Registration Token", registrationToken);
    }


}

