package edu.campusvirtual.comunica.services

import android.content.Context
import android.util.Log
import com.facebook.common.file.FileUtils
import edu.campusvirtual.comunica.interfaces.FileInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.file.FileCOM
import edu.campusvirtual.comunica.models.file.Response
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.StringReader
import java.util.*


fun Service.uploadImage(context: Context, imagePath: String, completion: (String) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(FileInterface::class.java)

    val file = File(imagePath)

    // create RequestBody instance from file
    val date = Date()
    val x = date.toString()
    val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
    val body = MultipartBody.Part.createFormData("uploaded_file", x + '-' + file.name , requestFile)

    service.postImage(body).enqueue(object: Callback<Response> {
        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()
                completion(body.photos!![0].url)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al subir la imagen")
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

fun Service.uploadVideoCOM(context: Context, file: File, completion: (String) -> Unit, failure: (String) -> Unit) {
    val retrofit = Service.prepareString(context)
    val service = retrofit.create(FileInterface::class.java)

    val requestFile = RequestBody.create(MediaType.parse(file.absolutePath), file)
    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
    val description = "Video uploaded by app"
    val descriptionBody = RequestBody.create(okhttp3.MultipartBody.FORM, description)

    val call = service.uploadVideo(descriptionBody, body)

    call.enqueue(object: Callback<String> {
        override fun onFailure(call: Call<String>?, t: Throwable?) {
            failure(t?.message!!)
        }

        override fun onResponse(call: Call<String>?, response: retrofit2.Response<String>?) {
            val body = response?.body()
            var x = body
            var factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            var xpp = factory.newPullParser()
            var value = ""

            if(x == null) {
                failure(response?.message()!!)
            } else {
                xpp.setInput(StringReader(x))
                var eventType = xpp.eventType
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        Log.d("TAG","Start document");
                    } else if(eventType == XmlPullParser.START_TAG) {
                        Log.d("TAG","Start tag "+xpp.getName());
                    } else if(eventType == XmlPullParser.END_TAG) {
                        Log.d("TAG","End tag "+xpp.getName());
                    } else if(eventType == XmlPullParser.TEXT) {
                        value = xpp.text
                    }
                    eventType = xpp.next();
                }
                completion(value)
            }
        }

    })

}

fun Service.uploadImageCOM(context: Context, imagePath: String, completion: (String) -> Unit, failure: (String) -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(FileInterface::class.java)

    val file = File(imagePath)

    // create RequestBody instance from file
    val date = Date()
    val x = date.toString()
    val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
    val body = MultipartBody.Part.createFormData("file", x + '-' + file.name , requestFile)

    service.postImageCOM(body).enqueue(object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            failure(t?.message!!)
        }

        override fun onResponse(call: Call<ResponseBody>?, response: retrofit2.Response<ResponseBody>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()
                // completion(body)
                var x = body.string()
                var factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                var xpp = factory.newPullParser()
                var value = ""

                xpp.setInput(StringReader(x))
                var eventType = xpp.eventType
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        Log.d("TAG","Start document");
                    } else if(eventType == XmlPullParser.START_TAG) {
                        Log.d("TAG","Start tag "+xpp.getName());
                    } else if(eventType == XmlPullParser.END_TAG) {
                        Log.d("TAG","End tag "+xpp.getName());
                    } else if(eventType == XmlPullParser.TEXT) {
                        value = xpp.text
                    }
                    eventType = xpp.next();
                }
                completion(value)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al subir la imagen")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure(response.message())
            }
        }

    })

}
