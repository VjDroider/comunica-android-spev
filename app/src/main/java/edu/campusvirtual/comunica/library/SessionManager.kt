package edu.campusvirtual.comunica.library


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import edu.campusvirtual.comunica.models.location.Location
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import edu.campusvirtual.comunica.models.Constants
import io.realm.Realm
import io.realm.RealmConfiguration
import edu.campusvirtual.comunica.models.location.GeofenceDB
import edu.campusvirtual.comunica.models.location.LocationCOM


/**
 * Created by jonathan on 2/13/18.
 */
class SessionManager {

    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var context: Context
    var PRIVATE_MODE: Int = 0

    companion object {
        val SESSION_NAME_PREFERENCES: String = "COMUNICA_DEMO"
        val SESSION_FULLNAME_KEY: String = "SESSION_FULLNAME_KEY"
        val SESSION_EMAIL_KEY: String = "SESSION_EMAIL_KEY"
        val SESSION_PHONE_KEY: String = "SESSION_PHONE_KEY"
        val SESSION_TOKEN_KEY: String = "SESSION_TOKEN_KEY"
        val SESSION_GEOFENCES: String = "SESSION_GEOFENCES"
        val SESSION_DEVICE_TOKEN_KEY = "SESSION_DEVICE_TOKEN_KEY"
        val SESSION_ID_KEY = "SESSION_ID_KEY"
        val SESSION_LAST_GET= "SESSION_LAST_GET"
        var SESSION_PASS_KEY = "SESSION_PASS_KEY"
        var SESSION_DEVICE_VIDEO= "SESSION_DEVICE_VIDEO"


        var geofences: ArrayList<LocationCOM> = ArrayList<LocationCOM>()
    }

    constructor(context: Context) {
        this.context = context
        pref = context.getSharedPreferences(SESSION_NAME_PREFERENCES, PRIVATE_MODE)
        editor = pref.edit()

        geofences = getGeofences()
    }

    fun createSession(id: Int, fullname: String, email: String, phone: String, accessToken: String) {
        editor.putString(SESSION_FULLNAME_KEY, fullname)
        editor.putString(SESSION_EMAIL_KEY, email)
        editor.putString(SESSION_PHONE_KEY, phone)
        editor.putString(SESSION_TOKEN_KEY, accessToken)
        editor.putInt(SESSION_ID_KEY, id)
        editor.commit()
    }

    fun saveLastGet(last: String) {
        editor.putString(SESSION_LAST_GET, last)
        editor.commit()
    }

    fun savePass(last: String) {
        editor.putString(SESSION_PASS_KEY, last)
        editor.commit()
    }

    fun destroySession() {
        editor.clear().apply()
    }

    fun getFullname() : String? {
        return pref.getString(SESSION_FULLNAME_KEY, null)
    }

    fun getId() : Int {
        return pref.getInt(SESSION_ID_KEY, -1)
    }

    fun getEmail() : String? {
        return pref.getString(SESSION_EMAIL_KEY, null)
    }

    fun getpassword() : String? {
        return pref.getString(SESSION_PASS_KEY, null)
    }

    fun getPhone() : String? {
        return pref.getString(SESSION_PHONE_KEY, null)
    }

    fun getLastGet() : String? {
        return pref.getString(SESSION_LAST_GET, "2010-01-01T00:00:00")
    }

    fun getAceessToken() : String? {
        return pref.getString(SESSION_TOKEN_KEY, null)
    }

    fun isLoguedIn() : Boolean {
        if(Constants.backend == Constants.COMUNICA) {
            var id: Int? = pref.getInt(SESSION_ID_KEY, -1)

            if(id == null || id == -1) {
                return false
            }

            return true

        } else {
            val accessToken: String? = pref.getString(SESSION_TOKEN_KEY, null)

            if(accessToken != null && !accessToken.isEmpty()) {
                return true
            }

            return false
        }

    }

    fun updateAccessToken(token: String) {
        editor.putString(SESSION_TOKEN_KEY, token)
        editor.commit()
    }

    fun updatePhone(phone: String) {
        editor.putString(SESSION_PHONE_KEY, phone)
        editor.commit()
    }

    fun updateEmail(email: String) {
        editor.putString(SESSION_EMAIL_KEY, email)
        editor.commit()
    }

    fun onRegisterSuccess(token: String) {
        editor.putString(SESSION_DEVICE_TOKEN_KEY, token)
        editor.commit()
    }

    fun getDeviceToken() : String? {
        return pref.getString(SESSION_DEVICE_TOKEN_KEY, "")
    }

    fun getGeofences() : ArrayList<LocationCOM> {
        Realm.init(context)
        var array = ArrayList<LocationCOM>()
        val config = getDefaultConfig("geofence.realm")
        val realm = Realm.getInstance(config)
        // 1 - eliminar mensajes del mailbox X

        val results = realm.where(GeofenceDB::class.java).findAll()

        for(g in results) {
            array.add(g.toModelCOM())
        }

        return array
    }

    fun onRegisterVideo(token: String) {
        editor.putString(SESSION_DEVICE_VIDEO, token)
        editor.commit()
    }


    fun getVideo() : String? {
        return pref.getString(SESSION_DEVICE_VIDEO, "")
    }

    fun onRegister(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun onGet(key: String) : String? {
        return pref.getString(key, null)
    }

}

fun getDefaultConfig(name: String): RealmConfiguration {
    return RealmConfiguration.Builder().name(name).schemaVersion(1).deleteRealmIfMigrationNeeded().migration(RealMigrations()).build()
}
