package edu.campusvirtual.comunica.library

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.models.configuration.Configuration
import edu.campusvirtual.comunica.models.location.Location
import edu.campusvirtual.comunica.models.location.LocationCOM
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.geofenceNotify
import edu.campusvirtual.comunica.services.geofenceNotifyCOM


class GeofenceRegistrationService: IntentService(TAG) {
    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent")
        // sendNotification("onHandleIntent", "onHandleIntent", 0)
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            // sendNotification("onHandleIntent", "hasError", 1)
            Log.d(TAG, "GeofencingEvent error " + geofencingEvent.errorCode)
        } else {
            val transaction = geofencingEvent.geofenceTransition
            val geofences = geofencingEvent.triggeringGeofences

            // sendNotification("onHandleIntent", "geofences ? " + (geofences != null).toString(), 1)
            if(geofences != null) {
                // sendNotification("onHandleIntent", "geofences count " + geofences.size, 2)
                for(geofence in geofences) {
                    val geo = findGeofence(geofence.requestId)

                    // sendNotification("onHandleIntent", "geo null ? " + (geo != null).toString(), 3)
                    if(geo != null) {
                        // sendNotification("FIND GEO", geo.street!!, 8)
                        if(transaction == Geofence.GEOFENCE_TRANSITION_ENTER)  {
                            // sendNotification("GEOFENCE_TRANSITION_ENTER", "You are entring Stanford University", 4)

                            if(Constants.backend == Constants.COMUNICA) {
                                Service.shared().geofenceNotifyCOM(this, 1, geo.id!!, geo.name!!, completion = {
                                    // sendNotification("GEOFENCE_TRANSITION_ENTER", "success", 5)
                                }, failure = {
                                    // sendNotification("GEOFENCE_TRANSITION_ENTER", "Fail", 5)
                                })
                            } else {
                                Service.shared().geofenceNotify(this, geo.id.toString(), "1", completion = {
                                    // Log.d("HERE", "TRUE")
                                    // sendNotification("GEOFENCE_TRANSITION_ENTER", "success", 5)
                                }, failure = {
                                    // Log.d("HERE", "FALSE")
                                    // sendNotification("GEOFENCE_TRANSITION_ENTER", "Fail", 5)
                                })
                            }


                            // Log.d(TAG, "You are entring Stanford University")
                            // sendNotification("entry", "You are entring Stanford University")
                        } else if(transaction == Geofence.GEOFENCE_TRANSITION_EXIT) {
                            // sendNotification("GEOFENCE_TRANSITION_EXIT", "You are exiting Stanford University", 4)
                            if(Constants.backend == Constants.COMUNICA) {
                                Service.shared().geofenceNotifyCOM(this, 2, geo.id!!, geo.name!!, completion = {
                                    // sendNotification("GEOFENCE_TRANSITION_ENTER", "success", 5)
                                }, failure = {
                                    // sendNotification("GEOFENCE_TRANSITION_ENTER", "Fail", 5)
                                })
                            } else {
                                Service.shared().geofenceNotify(this, geo.id.toString(), "2", completion = {
                                    // Log.d("HERE", "TRUE")
                                    // sendNotification("GEOFENCE_TRANSITION_EXIT", "success", 5)
                                }, failure = {
                                    // Log.d("HERE", "FALSE")
                                    // sendNotification("GEOFENCE_TRANSITION_EXIT", "fail", 5)
                                })
                            }

                            // Log.d(TAG, "You are exiting Stanford University")

                            // sendNotification("exit", "You are exiting Stanford University")
                        } else {
                            // sendNotification("ELSE", "dwell", 5)
                            // estoy dentro
                        }
                    }

                }
            }

        }
    }

    fun sendNotification(title: String, text: String, id: Int) {
        val n = Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.city)
                .setAutoCancel(true).build()


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(id, n)
    }

    fun getGeofences(): ArrayList<LocationCOM> {
        var config = Configuration.getConfiguration(this, Configuration.GEOFENCES)
        var g = config?.value
        var gson = GsonBuilder().setPrettyPrinting().create()
        var locationsArray:ArrayList<LocationCOM> = gson.fromJson(g, object: TypeToken<ArrayList<LocationCOM>>() {}.type)

        return locationsArray
    }

    fun findGeofence(id: String) : LocationCOM? {
        val geofences = getGeofences()

        // sendNotification("findGeofence", "geofences count: " + geofences.size, 3)

        for(geofence in geofences) {
            if(geofence.id.toString() == id) {
                return geofence
            }
        }

        return null
    }

    companion object {
        private val TAG = "GeoIntentService"
    }
}