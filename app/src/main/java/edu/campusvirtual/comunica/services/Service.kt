package edu.campusvirtual.comunica.services

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.library.SessionManager
import io.realm.log.LogLevel
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.SimpleXmlConverterFactory
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.Type


/**
 * Created by jonathan on 2/13/18.
 */
class Service {
    companion object {
        val notFound = 404
        val serverError = 500
        val unauthorized = 401
        val badRequest = 400
        val noInternedConnection = 0

        private var service: Service? = null
        fun shared() : Service {
            if(service == null) {
                return Service()
            }
            return service!!
        }

        fun prepare(context: Context) : Retrofit {
            val sessionManager = SessionManager(context)
            val logging = HttpLoggingInterceptor()
            val httpClient = OkHttpClient.Builder()
            httpClient.readTimeout(60*1000, TimeUnit.MILLISECONDS)

            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
            if(sessionManager.getAceessToken() != null) {
                httpClient.addInterceptor { chain ->
                    Log.d("Auth", sessionManager.getAceessToken())
                    chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer " + sessionManager.getAceessToken()).build())
                }
            }


            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder().baseUrl(Constants.baseUrl)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build()

            return retrofit
        }

        fun prepareString(context: Context) : Retrofit {
            val sessionManager = SessionManager(context)
            val logging = HttpLoggingInterceptor()
            val httpClient = OkHttpClient.Builder()
            httpClient.readTimeout(60*1000, TimeUnit.MILLISECONDS)

            // logging.level = HttpLoggingInterceptor.Level.BODY
            logging.level = HttpLoggingInterceptor.Level.NONE
            httpClient.addInterceptor(logging)
            if(sessionManager.getAceessToken() != null) {
                httpClient.addInterceptor { chain ->
                    Log.d("Auth", sessionManager.getAceessToken())
                    chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer " + sessionManager.getAceessToken()).build())
                }
            }


            val retrofit = Retrofit.Builder().baseUrl(Constants.baseUrl)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(ToStringConverterFactory())
                    .client(httpClient.build())
                    .build()

            return retrofit
        }

        fun prepareXML(context: Context) : Retrofit {
            val sessionManager = SessionManager(context)
            val logging = HttpLoggingInterceptor()
            val httpClient = OkHttpClient.Builder()
            httpClient.readTimeout(60*1000, TimeUnit.MILLISECONDS)

            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
            if(sessionManager.getAceessToken() != null) {
                httpClient.addInterceptor { chain ->
                    Log.d("Auth", sessionManager.getAceessToken())
                    chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer " + sessionManager.getAceessToken()).build())
                }
            }


            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder().baseUrl(Constants.baseUrl)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .client(httpClient.build())
                    .build()

            return retrofit
        }
    }
}

class ToStringConverterFactory : Converter.Factory() {


    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        return if (String::class.java == type) {
            object : Converter<ResponseBody, String> {
                @Throws(IOException::class)
                override fun convert(value: ResponseBody): String {
                    return value.string()
                }
            }
        } else null
    }

    fun requestBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {

        return if (String::class.java == type) {
            object : Converter<String, RequestBody> {
                @Throws(IOException::class)
                override fun convert(value: String): RequestBody {
                    return RequestBody.create(MEDIA_TYPE, value)
                }
            }
        } else null
    }

    companion object {
        private val MEDIA_TYPE = MediaType.parse("text/plain")
    }
}