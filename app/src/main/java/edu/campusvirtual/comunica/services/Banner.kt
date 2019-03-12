package edu.campusvirtual.comunica.services

import android.content.Context
import android.util.Log
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.BannerInterface
import edu.campusvirtual.comunica.interfaces.ItemInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.banner.Banner
import edu.campusvirtual.comunica.models.banner.BannerCOM
import edu.campusvirtual.comunica.models.banner.Response
import edu.campusvirtual.comunica.models.item.ResponseCOM
import retrofit2.Call
import retrofit2.Callback

fun Service.getBanners(context: Context, completion: (ArrayList<Banner>?) -> Unit, failure: () -> Unit) {

    var retrofit = Service.prepare(context)
    var service = retrofit.create(BannerInterface::class.java)

    service.getBanners(Constants.collegeId).enqueue(object: Callback<Response> {
        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.elements)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudieron obtener los banners")
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

fun Service.getBannersCOM(context: Context, completion: (ArrayList<BannerCOM>?) -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(ItemInterface::class.java)

    service.getItemsCOM(Constants.collegeId).enqueue(object: Callback<ResponseCOM> {
        override fun onResponse(call: Call<ResponseCOM>?, response: retrofit2.Response<ResponseCOM>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.banners)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudieron obtener los banners")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<ResponseCOM>?, t: Throwable?) {
            failure()
        }

    })

}