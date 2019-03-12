package edu.campusvirtual.comunica.library

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import edu.campusvirtual.comunica.R
import android.app.PendingIntent
import android.content.*
import android.util.Log
import com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices
import android.provider.Settings.Secure.LOCATION_MODE_OFF
import android.os.Build
import android.location.LocationManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.provider.Settings.Secure
import android.provider.Settings.Secure.LOCATION_MODE
import android.content.Intent
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.common.GooglePlayServicesUtil


/**
 * Created by jonathan on 4/13/18.
 */
class TransitionReceviver: BroadcastReceiver(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mGeofenceList: ArrayList<Geofence>? = null
    private var mGeofencePendingIntent: PendingIntent? = null
    private var TAG = "BootReceiver"
    var contextBootReceiver: Context? = null

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        Log.i(TAG, "Connected to GoogleApiClient")
        val sharedPrefs = contextBootReceiver?.getSharedPreferences("GEO_PREFS", Context.MODE_PRIVATE)
        val geofencesExist = sharedPrefs?.getString("Geofences added", null)

        if (geofencesExist == null) {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent(contextBootReceiver!!)
            ).setResultCallback(ResultCallback<Status> { status ->
                if (status.isSuccess) {
                    val sharedPrefs = contextBootReceiver!!.getSharedPreferences("GEO_PREFS", Context.MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putString("Geofences added", "1")
                    editor.commit()
                }
            })

        }
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

       if (connectionResult.hasResolution()) {
        try {
            // connectionResult.startResolutionForResult(TabbarActivity(),0);
        } catch ( e: IntentSender.SendIntentException) {
            Log.i(TAG, "Exception while resolving connection error.", e);
        }
    } else {
        var errorCode = connectionResult.getErrorCode();
        Log.i(TAG, "Connection to Google Play services failed with error code " + errorCode);
    }
    }

    override fun onResult(p0: Status) {
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        contextBootReceiver = context;

        var sharedPrefs: SharedPreferences?
        var editor: SharedPreferences.Editor?

        if ((intent?.getAction().equals("android.location.MODE_CHANGED") && isLocationModeAvailable(contextBootReceiver!!)) || (intent?.getAction().equals("android.location.PROVIDERS_CHANGED") && isLocationServciesAvailable(contextBootReceiver!!))) {
        // isLocationModeAvailable for API >=19, isLocationServciesAvailable for API <19
        sharedPrefs = context?.getSharedPreferences("GEO_PREFS", Context.MODE_PRIVATE);
        editor = sharedPrefs?.edit();
        editor?.remove("Geofences added");
        editor?.commit();
        if (!isGooglePlayServicesAvailable()) {
            Log.i(TAG, "Google Play services unavailable.");
            return;
        }

        mGeofencePendingIntent = null;
        mGeofenceList = ArrayList<Geofence>();

        mGeofenceList!!.add(Geofence.Builder()
                .setRequestId("1")

                .setCircularRegion(
                        20.678047,
                        -103.381722,
                        100.0.toFloat()
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL or
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(30000)
                .build());


        mGoogleApiClient = GoogleApiClient.Builder(contextBootReceiver!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mGoogleApiClient!!.connect();
    }

        val n = Notification.Builder(context)
                .setContentTitle("onreceive")
                .setContentText("enter")
                .setSmallIcon(R.drawable.city)
                .setAutoCancel(true).build()



        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(100, n)
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(contextBootReceiver)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, contextBootReceiver as android.app.Activity,
                        0).show()
            } else {
                Log.i(TAG, "This device is not supported.")
            }
            return false
        }
        return true
    }

    private fun isLocationModeAvailable(context: Context): Boolean {

        return if (Build.VERSION.SDK_INT >= 19 && getLocationMode(context) !== Settings.Secure.LOCATION_MODE_OFF) {
            true
        } else
            false
    }

    fun isLocationServciesAvailable(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 19) {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        } else
            return false
    }

    fun getLocationMode(context: Context): Int {
        try {
            return Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }

        return 0
    }

    fun getGeofencingRequest(): GeofencingRequest {
        val builder = GeofencingRequest.Builder()
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
        builder.addGeofences(mGeofenceList)
        return builder.build()
    }


    fun getGeofencePendingIntent(context: Context): PendingIntent {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent!!
        }
        val intent = Intent(context, GeofenceRegistrationService::class.java)
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}

