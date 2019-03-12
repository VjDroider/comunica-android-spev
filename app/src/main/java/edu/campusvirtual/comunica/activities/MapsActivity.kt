package edu.campusvirtual.comunica.activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import edu.campusvirtual.comunica.library.OnInfoWidnowElemTouchListener
import edu.campusvirtual.comunica.models.location.Location
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.getLocations
import com.google.android.gms.maps.model.Marker
import com.kaopiz.kprogresshud.KProgressHUD
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.configuration.Configuration
import edu.campusvirtual.comunica.services.getLocationsCOM

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, DialogInterface.OnClickListener, GoogleMap.OnInfoWindowCloseListener {

    private var mMap: GoogleMap? = null
    private val bottomOffsetPixels: Int = 0
    private val marker: Marker? = null
    private var infoWindow: ViewGroup? = null
    private var buttonEmail: Button? = null
    private var buttonCall: Button? = null
    var locations: ArrayList<Location>? = null
    var infoButtonListener: OnInfoWidnowElemTouchListener? = null
    var locationSelected: Location? = null
    var spin: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
Log.d("->", "<-")
        spin = Util.loadingPage(this, null, spin)

        if(Constants.backend == Constants.COMUNICA) {
            Service.shared().getLocationsCOM(this, completion = { locations ->
                this.locations = locations

                setupMap()
                stopLoading()
            }, failure = {
                // fail
                stopLoading()
            })
        } else {
            Service.shared().getLocations(this, completion = { locations ->
                this.locations = locations

                setupMap()
                stopLoading()
            }, failure = {
                // fail
                stopLoading()
            })
        }



        setTitle("Ubicaciones")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    fun stopLoading() {
        Util.stopLoadingPage(spin)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun setupMap() {
        if(locations != null) {
            val bounds = LatLngBounds.Builder()

            for(location in locations!!) {
                var latLng = LatLng(location.latitude!!, location.longitude!!)

                mMap?.addMarker(MarkerOptions().position(latLng).title(location.street).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                bounds.include(latLng)
            }

            mMap?.setOnMapLoadedCallback { mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 30)) }

            mMap?.setOnInfoWindowClickListener(this)

            mMap?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoWindow(p0: Marker?): View? {
                    return null
                }


                // Defines the contents of the InfoWindow
                override fun getInfoContents(arg0: Marker): View? {
                    var v: View? = null
                    try {
                        v = layoutInflater.inflate(R.layout.custom_infowindow, null)

                        // val addressTxt = v!!.findViewById<View>(R.id.addressTxt) as TextView
                        // addressTxt.text = arg0.title

                        val buttonCall = v.findViewById<View>(R.id.buttonCallId) as Button
                        val place= v.findViewById<TextView>(R.id.mobileTxt)
                        val street = v.findViewById<TextView>(R.id.addressTxt)

                        var x = findLocation(arg0.title)

                        if(x != null ) {
                            var config = Configuration.getConfiguration(applicationContext, Configuration.NAME)
                            place.text = config?.value
                            street.text = x.neighboor
                        }

                        buttonCall.setTypeface(Typeface.createFromAsset(assets, "fontawesome-webfont.ttf"))
                        buttonCall.setText(getString(R.string.fa_icon_phone))

                        val buttonEmail = v.findViewById<View>(R.id.buttonEmailId) as Button

                        buttonEmail.setTypeface(Typeface.createFromAsset(assets, "fontawesome-webfont.ttf"))
                        buttonEmail.setText(getString(R.string.fa_icon_envelope_o))

                        buttonCall.setOnClickListener {
                            Log.d("TAGCLICK", "Listener")
                        }

                    } catch (ev: Exception) {
                        print(ev.message)
                    }

                    return v
                }
            })

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupMap()
    }

    fun findLocation(name: String) : Location? {
        for(location in locations!!) {
            if(location.street == name) {
                return location
            }
        }

        return null
    }

    override fun onInfoWindowClick(marker: Marker?) {
        locationSelected = findLocation(marker!!.title)
        if(locationSelected == null) {
            return
        }

        val adb = AlertDialog.Builder(this)
        val items = arrayOf<CharSequence>("Llamar", "Contactar por correo")

        adb.setItems(items, this)

        adb.setNegativeButton("Cancelar", null)
        adb.setTitle("Contacto")
        adb.show()

    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when(which) {
            0 -> {
                call(locationSelected!!.phone!!)
            }
            1 -> {
                sendEmail(locationSelected!!.email!!)
            }
        }
    }

    private fun call(phone: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.setData(Uri.parse("tel:" + phone))

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 101)
        } else {
            startActivity(callIntent)
        }

    }

    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("mailto:" + email)
        startActivity(Intent.createChooser(intent, "E-mail"))
    }

    override fun onInfoWindowClose(p0: Marker?) {
        locationSelected = null
    }
}
