package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.ConfigurationInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.configuration.*
import retrofit2.Call
import retrofit2.Callback


fun Service.updatePersonalConfig(context: Context, body: PersonalRequest, completion: (PersonalConfiguration?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(ConfigurationInterface::class.java)

    service.updatePersonalConfig(body).enqueue(object: Callback<PersonalConfiguration> {
        override fun onFailure(call: Call<PersonalConfiguration>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<PersonalConfiguration>?, response: retrofit2.Response<PersonalConfiguration>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                body.saveIn(context)

                completion(body)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo actualizar la configuración personal")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }
    })
}

fun Service.getPersonalConfig(context: Context, completion: (ArrayList<PersonalConfiguration>?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(ConfigurationInterface::class.java)

    service.getPersonalConfigs().enqueue(object: Callback<PersonalResponse> {
        override fun onResponse(call: Call<PersonalResponse>?, response: retrofit2.Response<PersonalResponse>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                for(config in body.data!!) {
                    config.saveIn(context)
                }

                completion(body.data)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener la configuración")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<PersonalResponse>?, t: Throwable?) {
            failure()
        }

    })
}

fun Service.getConfig(context: Context, name: String, completion: (Configuration?) -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(ConfigurationInterface::class.java)

    service.getConfig(Constants.collegeId, name).enqueue(object: Callback<SingleResponse> {
        override fun onResponse(call: Call<SingleResponse>?, response: retrofit2.Response<SingleResponse>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.data)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener el valor de " + name)
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<SingleResponse>?, t: Throwable?) {
            failure()
        }

    })
}


fun Service.getConfigs(context: Context, completion: (ArrayList<Configuration>?) -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(ConfigurationInterface::class.java)

    var keys = "caracteristica_home,caracteristica_historial,caracteristica_calificaciones,caracteristica_calendario,caracteristica_estado_cuenta,caracteristica_galeria"
    service.getConfigs(Constants.collegeId, keys).enqueue(object: Callback<Response> {
        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                for(config in body.config!!) run {
                    config.saveConfiguration(context)
                }

                completion(body.config)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudieron opbtener los valores buscados")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

    })
}

fun Service.getAllConfiguration(context: Context, completion: () -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(ConfigurationInterface::class.java)

    service.getAllConfigs(Constants.collegeId).enqueue(object: Callback<SingleResponseCOM> {
        override fun onFailure(call: Call<SingleResponseCOM>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<SingleResponseCOM>?, response: retrofit2.Response<SingleResponseCOM>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                for (i in body.items!!) {
                    i.saveConfiguration(context)
                }

                completion()
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudieron opbtener los valores buscados")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

    })
}
