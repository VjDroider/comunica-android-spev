package edu.campusvirtual.comunica.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.RemoteMessage
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.loginCOM
import edu.campusvirtual.comunica.services.updateDeviceToken

/**
 * Created by jonathan on 3/9/18.
 */
class MyFirebaseInstanceIDService: FirebaseInstanceIdService() {

    private var TAG = "MyFirebaseInstanceIDService"

    override fun onTokenRefresh() {
        //Get updated token
        var refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG,"New Token : "+refreshedToken)
        val sessionManager = SessionManager(applicationContext)
        sessionManager.onRegisterSuccess(refreshedToken!!)

        // sessionManager.onRegisterSuccess(refreshedToken)
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionName

            if(sessionManager.isLoguedIn()) {
                Service.shared().loginCOM(this, sessionManager.getEmail()!!, sessionManager.getpassword()!!, refreshedToken, completion = {
                    // sessionManager.onRegisterSuccess(refreshedToken)
                }, failure = {
                    // failure
                })
            }


        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }
}