package edu.campusvirtual.comunica.services

import android.content.Context
import com.google.gson.GsonBuilder
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.ItemInterface
import edu.campusvirtual.comunica.interfaces.MessageInterface
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.inbox.*
import edu.campusvirtual.comunica.models.item.ResponseCOM
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


fun Service.getMessages(context: Context) : Call<Response> {
    var service: MessageInterface
    var sessionManager = SessionManager(context)
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor(logging)
    httpClient.addInterceptor { chain ->
        chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer " + sessionManager.getAceessToken()).build())
    }

    val gson = GsonBuilder().setLenient().create()

    val retrofit = Retrofit.Builder().baseUrl("http://campusvirtualtesting.azurewebsites.net/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()

    service = retrofit.create<MessageInterface>(MessageInterface::class.java)

    val call = service.getMessages()

    return call
}

fun Service.getMessagesSent(context: Context) : Call<Response> {
    var service: MessageInterface
    var sessionManager = SessionManager(context)
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor(logging)
    httpClient.addInterceptor { chain ->
        chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer " + sessionManager.getAceessToken()).build())
    }

    val gson = GsonBuilder().setLenient().create()

    val retrofit = Retrofit.Builder().baseUrl("http://campusvirtualtesting.azurewebsites.net/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()

    service = retrofit.create<MessageInterface>(MessageInterface::class.java)

    val call = service.getMessagesSent()

    return call
}

fun Service.getMessagesSentCOM(context: Context, completion: (ArrayList<MessageCOM>) -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(MessageInterface::class.java)
    var session = SessionManager(context)

    var x = session.getLastGet()

    service.getMessagesCOM(session.getDeviceToken()!!, session.getEmail()!!, session.getId(), session.getLastGet()).enqueue(object: Callback<ArrayList<MessageCOM>> {
        override fun onResponse(call: Call<ArrayList<MessageCOM>>?, response: retrofit2.Response<ArrayList<MessageCOM>>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body)
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

        override fun onFailure(call: Call<ArrayList<MessageCOM>>?, t: Throwable?) {
            failure()
        }

    })
}

fun Service.markMessageAsRead(context: Context, messageId: Int) : Call<Any> {
    var service: MessageInterface
    var sessionManager = SessionManager(context)
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor(logging)
    httpClient.addInterceptor { chain ->
        chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer " + sessionManager.getAceessToken()).build())
    }

    val gson = GsonBuilder().setLenient().create()

    val retrofit = Retrofit.Builder().baseUrl("http://campusvirtualtesting.azurewebsites.net/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()

    service = retrofit.create<MessageInterface>(MessageInterface::class.java)

    val call = service.markMessageAsRead(messageId)

    return call
}

fun Service.markMessageAsReadCOM(context: Context, messageId: Int, completion: () -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(MessageInterface::class.java)
    var session = SessionManager(context)

    service.markMessageAsReadCOM(messageId, session.getId()).enqueue(object: Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>?, response: retrofit2.Response<ResponseBody>?) {
            if(response?.isSuccessful!!) {
                completion()
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

        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            failure()
        }

    })
}

fun Service.markMessageAsDelete(context: Context, messageId: Int) : Call<Any> {
    var service: MessageInterface
    var sessionManager = SessionManager(context)
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor(logging)
    httpClient.addInterceptor { chain ->
        chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer " + sessionManager.getAceessToken()).build())
    }

    val gson = GsonBuilder().setLenient().create()

    val retrofit = Retrofit.Builder().baseUrl("http://campusvirtualtesting.azurewebsites.net/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()

    service = retrofit.create<MessageInterface>(MessageInterface::class.java)

    val call = service.markMessageAsDelete(messageId)

    return call
}

fun Service.getUnreadCountMessages(context: Context, completion: (Int) -> Unit, failure: () -> Unit) {

    val retrofit = Service.prepare(context)
    var service = retrofit.create(MessageInterface::class.java)

    service.getUnreadMessagesCount().enqueue(object: Callback<UnreadResponse> {
        override fun onResponse(call: Call<UnreadResponse>?, response: retrofit2.Response<UnreadResponse>?) {
            if(response?.isSuccessful!!) {
                val body = response?.body()

                completion(body.data!!.value)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al conocer el numero de mensajes sin leer")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<UnreadResponse>?, t: Throwable?) {
            failure()
        }

    })

}

fun Service.getUnreadCountMessagesCOM(context: Context, completion: (Int) -> Unit, failure: () -> Unit) {

    val retrofit = Service.prepare(context)
    var service = retrofit.create(MessageInterface::class.java)
    var session = SessionManager(context)

    service.getUnreadMessagesCountCOM(session.getDeviceToken()!!, session.getEmail()!!, Constants.collegeId, session.getId()).enqueue(object: Callback<UnreadResponseCOM> {
        override fun onResponse(call: Call<UnreadResponseCOM>?, response: retrofit2.Response<UnreadResponseCOM>?) {
            if(response?.isSuccessful!!) {
                val body = response?.body()

                completion(body.numeroMensajesSinLeer)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al conocer el numero de mensajes sin leer")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<UnreadResponseCOM>?, t: Throwable?) {
            failure()
        }

    })

}

fun Service.sentMessage(context: Context, body: Request, completion: () -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(MessageInterface::class.java)

    service.sendMessage(body).enqueue(object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<ResponseBody>?, response: retrofit2.Response<ResponseBody>?) {
            if(response?.isSuccessful!!) {
                completion()
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al enviar el mensaje, vuelve a intentarlo")
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

fun Service.sentMessageCOM(context: Context, body: RequestCOM, completion: () -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(MessageInterface::class.java)

    service.sendMessageCOM(body).enqueue(object: Callback<ResponseSend> {
        override fun onFailure(call: Call<ResponseSend>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<ResponseSend>?, response: retrofit2.Response<ResponseSend>?) {
            if(response?.isSuccessful!!) {
                if(response.body().success == "fail") {
                    failure()
                } else {
                    completion()
                }
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        // Util.showAlert(context, "Error", "Error al enviar el mensaje, vuelve a intentarlo")
                    }
                    Service.serverError -> {
                        // Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

    })
}
