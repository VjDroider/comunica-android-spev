package edu.campusvirtual.comunica.models.configuration

import android.annotation.SuppressLint
import android.content.Context
import edu.campusvirtual.comunica.library.SessionManager

/**
 * Created by jonathan on 2/16/18.
 */
class Configuration(var key: String, var value: String) {

    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    fun saveConfiguration(context: Context) {
        val pref = context.getSharedPreferences(SessionManager.SESSION_NAME_PREFERENCES, 0)
        val editor = pref.edit()

        editor.putString(key, value)
        editor.commit()
    }

    companion object {
        val EMAIL = "ReplyTo_Address"
        var PHONE = "PortadaTelefono"
        var NAME = "TITULOENCABEZADO"
        var PATH_VIDEO = "video_path"
        var GEOFENCES = "geofences"
        var VIDEO = "VIDEO_PATH_ANDROID_SECCION_ALUMNOS"
        var PortadaUbicacion = "PortadaUbicacion"
        var ImagenPrincipal = "ImagenPrincipal"
        var ImagenEventos = "ImagenEventos"
        var seccionseleccionadaafterlogin = "seccionseleccionadaafterlogin"
        var caracteristica_avisomillegada = "caracteristica_avisomillegada"

        fun getConfiguration(context: Context, key: String) : Configuration? {
            val pref = context.getSharedPreferences(SessionManager.SESSION_NAME_PREFERENCES, 0)

            val value = pref.getString(key, null)

            if(value == null) return null

            return Configuration(key, value)
        }
    }

}
