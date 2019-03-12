package edu.campusvirtual.comunica.library

import android.app.Activity
import android.content.Context
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import com.kaopiz.kprogresshud.KProgressHUD
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.models.configuration.Configuration
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import android.os.Build



/**
 * Created by jonathan on 2/13/18.
 */
class Util {
    companion object {

        var COVERPAGE = "logoImg"
       var DEFAULT_COVERPAGE = R.drawable.logo_bn

        fun showAlert(context: Context, title: String, message: String, okButton: String = "OK", callback: (() -> Unit)? = null) {
            if(!(context as Activity).isFinishing())
            {
                val alertDialog = AlertDialog.Builder(context).create()
                alertDialog.setTitle(title)
                alertDialog.setMessage(message)
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, okButton) { dialog, which ->
                    if(callback != null) {
                        callback()
                    }
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        }

        fun loadingPage(context: Context, label: String? = null, kprogress: KProgressHUD? = null) : KProgressHUD {

            if(kprogress == null || (kprogress != null && !kprogress?.isShowing!!)) {
                val spin = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setAnimationSpeed(2)

                if(label != null) {
                    spin.setLabel(label)
                }

                return spin.show()
            }

            return kprogress

        }

        fun stopLoadingPage(spin: KProgressHUD?) {
            if(spin != null) spin.dismiss()
        }


        fun downloadAsset(context: Context, key: String, coreDataKey: String, assetName: String, completion: (String) -> Unit, failure: () -> Unit) {
            val sessionManager = SessionManager(context)
            val config = Configuration.getConfiguration(context, key)

            if(config == null) {
                failure()
            } else {
                val urlString = config?.value
                val mime = urlString?.substring(urlString.lastIndexOf(".") + 1, urlString.length);
                val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                val path = downloadsPath + File.separator + assetName + "." + mime
                val file = File(path)

                if(file.exists() && urlString == sessionManager.onGet(key)) {
                    completion(path)
                } else {
                    sessionManager.onRegister(key, urlString!!)
                    download(urlString, downloadsPath, assetName, completion = {
                        completion(it)
                    }, failure = {
                        // Toast.makeText(context, "Algo salio mal al descargar el asset", Toast.LENGTH_LONG).show()
                        failure()
                    })
                }
            }

        }

        fun downloadAssetByName(context: Context, urlString: String, name: String, completion: (String) -> Unit, failure: () -> Unit) {
            val mime = urlString?.substring(urlString.lastIndexOf(".") + 1, urlString.length);
            val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            val path = downloadsPath + File.separator + name + "." + mime
            val file = File(path)

            if(file.exists()) {
                completion(path)
            } else {
                download(urlString, downloadsPath, name, completion = {
                    completion(it)
                }, failure = {
                    // Toast.makeText(context, "Algo salio mal al descargar el asset", Toast.LENGTH_LONG).show()
                    failure()
                })
            }

        }

        fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val product = Build.PRODUCT
            val brand = Build.BRAND

            return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
                capitalize(model) + " - " + product + " - " + brand
            } else {
                capitalize(manufacturer) + " - " + model+ " - " + product + " - " + brand
            }
        }


        private fun capitalize(s: String?): String {
            if (s == null || s.length == 0) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                Character.toUpperCase(first) + s.substring(1)
            }
        }

    }

}


fun download(fileURL: String, saveDir: String, name: String, completion: (String) -> Unit, failure: () -> Unit)  {
    try {
        var BUFFER_SIZE = 4096;
        var url = URL(fileURL);
        val httpConn: HttpURLConnection = url.openConnection() as HttpURLConnection
        val responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            var fileName = "";
            var disposition = httpConn.getHeaderField("Content-Disposition");
            var contentType = httpConn.getContentType();
            var contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                var index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                        disposition.length - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                    fileURL.length);
            }

            var mime = fileURL.substring(fileURL.lastIndexOf(".") + 1,
                fileURL.length);
            fileName = name + "." + mime
            // opens input stream from the HTTP connection
            var inputStream = httpConn.getInputStream();
            var saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            var outputStream = FileOutputStream(saveFilePath);

            var bytesRead = -1;
            var buffer = ByteArray(BUFFER_SIZE)
            bytesRead = inputStream.read(buffer)
            while (bytesRead != -1) {
                outputStream.write(buffer, 0, bytesRead);
                bytesRead = inputStream.read(buffer)
            }

            outputStream.close();
            inputStream.close();

            // Toast.makeText(this, "Archivo descargado con exito", Toast.LENGTH_LONG).show()
            val file = File(saveFilePath)

            /* val target = Intent(Intent.ACTION_VIEW)
             target.setDataAndType(Uri.fromFile(file), mime)
             target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

             startActivity(target)*/
            // Util.stopLoadingPage(spin)
            // startActivity(Intent(this, VideoActivity::class.java))
            completion(saveFilePath)
        } else {
            failure()
            // Toast.makeText(this, "No file to download. Server replied HTTP code: " + responseCode, Toast.LENGTH_LONG).show()
            // Util.stopLoadingPage(spin)
        }
        httpConn.disconnect();
    } catch (e: Exception) {
        failure()
    }
}