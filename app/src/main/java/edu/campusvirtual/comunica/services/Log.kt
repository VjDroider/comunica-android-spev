package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.interfaces.LogInterface
import edu.campusvirtual.comunica.interfaces.MessageInterface
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.account.BodyLog
import edu.campusvirtual.comunica.models.inbox.MessageCOM
import retrofit2.Call
import retrofit2.Callback
import android.content.pm.PackageManager
import android.R.attr.versionName
import com.google.android.gms.common.util.ClientLibraryUtils.getPackageInfo
import android.content.pm.PackageInfo
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import edu.campusvirtual.comunica.BuildConfig
import okhttp3.ResponseBody
import retrofit2.Response


fun Service.saveLog(context: Context, action: String, completion: () -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(LogInterface::class.java)
    var session = SessionManager(context)

    var body = BodyLog()
    body.action = action
    // nombre - os - version app -
    var versionApp = BuildConfig.VERSION_NAME

    var description = "" + session.getFullname()
    description = description + "-Android-" + versionApp + "-" + Util.getDeviceName()
    body.description = description

    service.saveLog(body).enqueue(object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
            completion()
        }

    })

}