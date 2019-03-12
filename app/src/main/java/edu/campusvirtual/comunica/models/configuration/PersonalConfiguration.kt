package edu.campusvirtual.comunica.models.configuration

import android.content.Context
import edu.campusvirtual.comunica.library.SessionManager

/**
 * Created by jonathan on 3/6/18.
 */
class PersonalConfiguration(val id: Int, val name: String, var active: Boolean) {

    val PREFERENCE_NAME = "SHARED_PREFERENCE_PERSONAL_CONFIG"


    fun saveIn(context: Context) {
        val pref = context.getSharedPreferences(PREFERENCE_NAME, 0)
        val editor = pref?.edit()

        editor?.putBoolean(name, active)
        editor?.commit()
    }

    fun get(context: Context, name: String) : Boolean {
        val pref = context.getSharedPreferences(PREFERENCE_NAME, 0)

        return pref.getBoolean(name, false)
    }


}