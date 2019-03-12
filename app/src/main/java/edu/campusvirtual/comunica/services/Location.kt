package edu.campusvirtual.comunica.services

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.LocationsInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.configuration.Configuration
import edu.campusvirtual.comunica.models.location.Location
import edu.campusvirtual.comunica.models.location.LocationCOM
import edu.campusvirtual.comunica.models.location.Response
import edu.campusvirtual.comunica.models.location.SingleLocationCOM
import retrofit2.Call
import retrofit2.Callback

fun Service.getLocations(context: Context, completion: (ArrayList<Location>?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(LocationsInterface::class.java)

    service.getLocations(Constants.collegeId).enqueue(object: Callback<Response> {
        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.locations)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener las ubicaciones")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            failure()
        }

    })
}

fun Service.getLocationsCOM(context: Context, completion: (ArrayList<Location>?) -> Unit, failure: () -> Unit) {
    var config = Configuration.getConfiguration(context, Configuration.PortadaUbicacion)
    var configEmail = Configuration.getConfiguration(context, Configuration.EMAIL)
    var configPhone = Configuration.getConfiguration(context, Configuration.PHONE)
    var locations:ArrayList<Location> = ArrayList()
    if(config == null) {
        var location = Location()
        location.email = configEmail?.value
        location.id = 1
        location.latitude = Constants.defaultLatitude
        location.longitude = Constants.defaultLongitude
        location.phone = configPhone?.value
        location.street = "1"

        locations.add(location)

        completion(locations)
    } else {
        var gson = GsonBuilder().setPrettyPrinting().create()
        var geofences = config.value
        var locationsArray:Location = gson.fromJson(geofences, object: TypeToken<Location>() {}.type)

        if(locationsArray == null) {
            completion(ArrayList())
        } else {
            /*for(i in locationsArray) {
                locations.add(i as Location)
            }*/
            locationsArray.email = configEmail?.value
            locationsArray.phone = configPhone?.value
            locationsArray.id = 1
            locationsArray.street = "1"

            locations.add(locationsArray)

            completion(locations)
        }

    }
}

fun Service.getGeofences(context: Context, completion: (ArrayList<Location>?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(LocationsInterface::class.java)

    service.getGeofences(Constants.collegeId).enqueue(object: Callback<Response> {
        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.locations)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener las ubicaciones")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            failure()
        }

    })
}
