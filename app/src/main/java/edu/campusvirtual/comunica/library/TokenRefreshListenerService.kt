package edu.campusvirtual.comunica.library

import android.content.Intent
import com.google.android.gms.iid.InstanceIDListenerService

/**
 * Created by jonathan on 2/27/18.
 */
class TokenRefreshListenerService: InstanceIDListenerService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        var intent = Intent(this, RegistrationService::class.java)
        startService(intent)
    }

}