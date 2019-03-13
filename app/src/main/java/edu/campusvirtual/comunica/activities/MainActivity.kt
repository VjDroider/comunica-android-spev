package edu.campusvirtual.comunica.activities

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.crashlytics.android.Crashlytics
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.location.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.fragments.*
import edu.campusvirtual.comunica.fragments.configuration.ConfigurationFragment
import edu.campusvirtual.comunica.library.GeofenceRegistrationService
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.models.configuration.Configuration
import edu.campusvirtual.comunica.models.inbox.Request
import edu.campusvirtual.comunica.models.inbox.RequestCOM
import edu.campusvirtual.comunica.models.location.LocationCOM
import edu.campusvirtual.comunica.services.*
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import me.leolin.shortcutbadger.ShortcutBadger

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener, CustomMessagesFragment.CustomMessageListener,
    View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener, ResultCallback<Status> {

    private val geoFencePendingIntent: PendingIntent? = null
    private val GEOFENCE_REQ_CODE = 0

    private var GEO_DURATION: Long = 60 * 60 * 1000;
    private var GEOFENCE_REQ_ID = "My Geofence";
    private var GEOFENCE_RADIUS = 100.0f; // in meters

    val REQ_PERMISSION = 200

    var fab1:FloatingActionButton? = null
    var fab2:FloatingActionButton? = null
    var fab3:FloatingActionButton? = null

    private var googleApiClient: GoogleApiClient? = null
    private var lastLocation: Location? = null
    private var locationRequest: LocationRequest? = null;

    var sessionManager: SessionManager? = null
    private val UPDATE_INTERVAL: Long = 1000
    private val FASTEST_INTERVAL: Long = 900
    var geofences = ArrayList<LocationCOM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        sessionManager = SessionManager(this)

        val headerView = nav_view.getHeaderView(0)
        val nameId = headerView.findViewById<TextView>(R.id.nameId)
        val emailId = headerView.findViewById<TextView>(R.id.emailId)

        var x = sessionManager?.getFullname()
        nameId.text = sessionManager?.getFullname()
        emailId.text = sessionManager?.getEmail()

        updateDeviceToken()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        toolbar.setNavigationIcon(R.drawable.ic_menu)

        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.mainId, NewsFragment())
        fragment.commit()

        fab1 = findViewById(R.id.menu_item)
        fab2 = findViewById(R.id.menu_item2)
        fab3 = findViewById(R.id.menu_item3)

        fab1!!.setOnClickListener(this)
        fab2!!.setOnClickListener(this)
        fab3!!.setOnClickListener(this)

        setTitle("Inicio")
        fab.setClosedOnTouchOutside(true)


        logUser(sessionManager!!)
        createGoogleAPI()
        getGeofences()
    }

    override fun onRefreshMessages() {

    }

    fun createGoogleAPI() {
        Log.d("TAG", "createGoogleAPI()")

        if(googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
    }

    override fun onStart() {
        super.onStart()

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient?.connect()
    }

    override fun onStop() {
        super.onStop()

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient?.disconnect()
    }

    fun logUser(sm: SessionManager) {
        // TODO: Use the current user's information
        Log.d("FABRIC", "setup");
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(sm.getId().toString());
        Crashlytics.setUserEmail(sm.getEmail());
        Crashlytics.setUserName(sm.getFullname());
    }

    fun getGeofences() {
        var config = Configuration.getConfiguration(this, Configuration.GEOFENCES)
        var g = config?.value
        var gson = GsonBuilder().setPrettyPrinting().create()
        var locationsArray:ArrayList<LocationCOM> = gson.fromJson(g, object: TypeToken<ArrayList<LocationCOM>>() {}.type)

        this.geofences = locationsArray
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                setTitle("Inicio")
                // Handle the camera action
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.mainId, NewsFragment())
                fragment.commit()
            }
            R.id.nav_messages -> {
                setTitle("Mensajes")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.mainId, MessagesFragment())
                fragment.commit()
            }
            R.id.nav_reports -> {
                setTitle("Reportes")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.mainId, ReportFragment())
                fragment.commit()
            }
            R.id.nav_calendar -> {
                setTitle("Calendario")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.mainId, CalendarFragment())
                fragment.commit()
            }
            R.id.nav_location -> {
                setTitle("Ubicación")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.mainId, LocationFragment())
                fragment.commit()
            }
            R.id.nav_configuration -> {
                setTitle("Configuración")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.mainId, ConfigurationFragment())
                fragment.commit()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onClick(p0: View?) {
        val outside = "Ya llegué"
        val inComing = "Llego en 10 minutos"
        val late = "Se me hizo tarde"
        var fullname = sessionManager?.getFullname()
        var subject = "Geonotificación: " + fullname
        var message = ""

        when(p0?.id) {
            R.id.menu_item -> {
                message = outside
                sendMessage(subject, message)
                fab.close(true)
            }
            R.id.menu_item2 -> {
                message = inComing
                sendMessage(subject, message)
                fab.close(true)
            }
            R.id.menu_item3 -> {
                message = late
                sendMessage(subject, message)
                fab.close(true)
            }
        }
    }

    override fun onResult(status: Status) {
        Log.i("TAG", "onResult: " + status);
        if ( status.isSuccess() ) {
            // saveGeofence();
            // drawGeofence();
        } else {
            // inform about fail
        }
    }

    override fun onRestart() {
        super.onRestart()

        updateDeviceToken()
    }

    fun updateDeviceToken() {
        var session = SessionManager(this)
        var token = FirebaseInstanceId.getInstance().token

        if(token == null) {
            val deviceToken = session.getDeviceToken()

            if(deviceToken != null) {
                token = deviceToken
            } else {
                token = "devicetokendontregister"
            }
        }

        Service.shared().loginCOM(this, session.getEmail()!!, session.getpassword()!!, token, completion = {
            var session = SessionManager(this)
            session.onRegisterSuccess(token)


        }, failure = {
            // failure
        })

        Service.shared().getUnreadCountMessagesCOM(this, completion = { count ->
            //
            // setCountInInbox(count)
            ShortcutBadger.applyCount(this, count)
        }, failure = {
            // setCountInInbox()
            Log.d("FAIL", "SIIIIII")
        })

    }

    override fun onLocationChanged(location: Location?) {
        // Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
        // writeActualLocation(location);
    }

    override fun onConnected(p0: Bundle?) {
        Log.i("TAG", "onConnected()");
        startGeofence()
        getLastKnownLocation();
        recoverGeofenceMarker();
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.w("TAG", "onConnectionSuspended()");
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.w("TAG", "onConnectionFailed()");
    }

    // Recovering last Geofence marker
    private fun recoverGeofenceMarker() {
        Log.d("TAG", "recoverGeofenceMarker");

        // var lat = 20.677898
        // var lon = -103.381846
        // var latLng = LatLng( lat, lon );
        // markerForGeofence(latLng);
        // drawGeofence();

    }

    // Check for permission to access Location
    private fun checkPermission(): Boolean {
        Log.d("TAG", "checkPermission()")
        // Ask for permission if it wasn't granted yet
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Start location Updates
    private fun startLocationUpdates(){
        Log.i("TAG", "startLocationUpdates()");
        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    // Get last known location
    private fun getLastKnownLocation() {
        Log.d("TAG", "getLastKnownLocation()")
        if (checkPermission()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            if (lastLocation != null) {
                Log.i("TAG", "LasKnown location. " +
                        "Long: " + lastLocation!!.getLongitude() +
                        " | Lat: " + lastLocation!!.getLatitude())

                startLocationUpdates()
            } else {
                Log.w("TAG", "No location retrieved yet")
                startLocationUpdates()
            }
        } else
            askPermission()
    }

    // Start Geofence creation process
    private fun startGeofence() {
        var geofences: ArrayList<LocationCOM> = sessionManager?.getGeofences()!!

        if(geofences.size == 0) {
            getGeofences()
            geofences = this.geofences
        }

        var list: ArrayList<Geofence> = ArrayList()

        for(geofence in geofences) {
            // var g = geofence

            var lat = geofence.latitude!!
            var lon = geofence.longitude!!
            var latLng = LatLng( lat, lon )
            var transition = Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT

            if(geofence.transitionType == 1) {
                transition = Geofence.GEOFENCE_TRANSITION_ENTER
            } else if(geofence.transitionType == 2) {
                transition = Geofence.GEOFENCE_TRANSITION_EXIT
            }

            var mygeofence = createGeofence( latLng, geofence.radius?.toFloat()!!, geofence.id!!, transition)

            list.add(mygeofence)
        }

        if(list.size > 0 ) {
            addGeofence( createGeofenceRequest( list ) )
        }

    }

    private fun createGeofencePendingIntent(): PendingIntent {
        Log.d("TAG", "createGeofencePendingIntent")
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent

        val intent = Intent(baseContext, GeofenceRegistrationService::class.java)
        return PendingIntent.getService(
            this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private fun addGeofence( request: GeofencingRequest) {
        Log.d("TAG", "addGeofence");

        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                createGeofencePendingIntent()
            ).setResultCallback(this);
    }

    // Create a Geofence Request
    private fun createGeofenceRequest(list : List<Geofence>): GeofencingRequest {
        Log.d("TAG", "createGeofenceRequest");
        return GeofencingRequest.Builder()
            .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_DWELL )
            .addGeofences(list)
            .build();
    }

    // Create a Geofence
    private fun createGeofence(  latLng: LatLng, radius: Float, id: Int, transitionType: Int ): Geofence {
        Log.d("TAG", "createGeofence");
        return Geofence.Builder()
            .setRequestId(id.toString())
            .setCircularRegion( latLng.latitude, latLng.longitude, radius)
            .setExpirationDuration( GEO_DURATION )
            .setTransitionTypes( transitionType )
            .build();
    }

    fun sendMessage(subject: String, message: String) {
        var config: Configuration?

        config = Configuration.getConfiguration(applicationContext!!, "id_Lista_GeoNotificaciones")



        if(Constants.backend == Constants.COMUNICA) {
            var body = RequestCOM()

            body.mensaje = message
            body.tema = subject
            body.ids_lista_distribucion = config?.value!!
            body.id_Colegio = Constants.collegeId

            Service.shared().sentMessageCOM(applicationContext, body, completion = {
                Util.showAlert(this, "Bien", "Tu mensaje ya fue enviado a las misses")
            }, failure = {
                Util.showAlert(this, "Alerta", "No pudimos entregar el mensaje, intenta de nuevo")
            })
        } else {
            var body = Request()

            body.distribution_list_ids = config?.value!!
            body.person_ids = ""
            body.message = message
            body.subject = subject

            Service.shared().sentMessage(applicationContext, body, completion = {
                Util.showAlert(this, "Bien", "Tu mensaje ya fue enviado a las misses")
            }, failure = {
                Util.showAlert(this, "Alerta", "No pudimos entregar el mensaje, intenta de nuevo")
            })
        }


    }

    // Asks for permission
    private fun askPermission() {
        Log.d("TAG", "askPermission()");
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQ_PERMISSION
        );
    }
}
