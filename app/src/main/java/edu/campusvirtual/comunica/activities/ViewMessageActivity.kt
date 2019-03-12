package edu.campusvirtual.comunica.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import com.kaopiz.kprogresshud.KProgressHUD
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.models.inbox.Message
import edu.campusvirtual.comunica.models.inbox.MessageCOM
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.markMessageAsDelete
import edu.campusvirtual.comunica.services.markMessageAsRead
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.library.getDefaultConfig
import edu.campusvirtual.comunica.models.attachment.Attachment
import edu.campusvirtual.comunica.models.inbox.MessageDB
import edu.campusvirtual.comunica.services.markMessageAsReadCOM
import io.realm.Realm
import org.jsoup.Jsoup
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.collections.ArrayList


class ViewMessageActivity : AppCompatActivity() {

    var message: MessageCOM? = null

    var transmitterName: TextView? = null
    var receiverName: TextView? = null
    var initialsButton: Button? = null

    var subjectText: TextView? = null
    var dateText: TextView? = null
    var webView: WebView? = null

    var mailbox: String? = null
    var spin: KProgressHUD? = null
    var attachments: ArrayList<Attachment> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_message)

        message = intent.getSerializableExtra("message") as MessageCOM
        mailbox = intent.getStringExtra("mailbox")

        setTitle("")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        setupView()

        markMessageAsRead()
        getAttachments()

    }

    fun getAttachments() {
        val doc = Jsoup.parse(message?.Mensaje)
        val images = doc.select("img[src]")
        val videos = doc.select("video[src]")

        for(image in images) {
            val src = image.attr("src")

            attachments.add(Attachment(src, false, ""))
        }

        for(video in videos) {
            val src = video.attr("src")

            val file = Uri.parse(src)
            var name = file.lastPathSegment.substring(0, file.lastPathSegment.lastIndexOf("."));
            Thread({
                Util.downloadAssetByName(this, src, name, completion = {
                    var file = File(it)
                    val thumb = ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)

                    attachments.add(Attachment(src, true, preview = getImageUri(this, thumb)))

                }, failure = {
                    var x = ""
                })
            }).start()



        }


        if(attachments.size > 0) {
            //
        } else {
            // hide icon tab
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return path
    }

    fun markAsRead(id: Int) {
        Realm.init(this)

        val config = getDefaultConfig("messages.realm")
        val realm = Realm.getInstance(config)
        // 1 - eliminar mensajes del mailbox X
        realm.beginTransaction()
        val results = realm.where(MessageDB::class.java).contains("mailbox", mailbox!!).equalTo("id", id).findFirst()

        if(results != null) {
            results.read = true
        }

        realm.commitTransaction()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater

        inflater.inflate(R.menu.reply_menu, menu)

        return true
    }

    fun markMessageAsRead() {
        if(mailbox == "Recibidos") {
            if(Constants.backend == Constants.COMUNICA) {
                Service.shared().markMessageAsReadCOM(this, message?.id_Mensaje!!, completion = {
                    markAsRead(message?.id_Mensaje!!)
                }, failure = {
                    markAsRead(message?.id_Mensaje!!)
                })
            } else {
                Service.shared().markMessageAsRead(this, message?.id_Mensaje!!).enqueue(object: Callback<Any> {
                    override fun onResponse(call: Call<Any>?, response: Response<Any>?) {
                        if(response?.code() == 200) {
                            Log.d("TAGMESSAGE", "mensaje " + message?.id_Mensaje + " marcado como leido")
                        } else {
                            Log.d("TAGMESSAGE", "mensaje " + message?.id_Mensaje + " no marcado como leido")
                        }
                    }

                    override fun onFailure(call: Call<Any>?, t: Throwable?) {
                    }
                })
            }

        }
    }

    fun setupView() {
        val session = SessionManager(this)
        transmitterName = findViewById<TextView>(R.id.transmitterNameId)
        receiverName = findViewById<TextView>(R.id.receiverNameId)
        initialsButton = findViewById<Button>(R.id.initialsTransmitterId)

        subjectText = findViewById<TextView>(R.id.subjectId)
        dateText = findViewById<TextView>(R.id.dateTextId)
        webView = findViewById<WebView>(R.id.webViewId)

        val settings = webView!!.getSettings()
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = false

        var transmitter = message?.transmitter_name

        if(transmitter == null) {
            transmitter = Constants.defaultTransmitter
        }

        transmitterName!!.text = transmitter
        var to = message?.receiver_name

        if(to == null) {
            to = session.getEmail()
        }

        receiverName!!.text = "Para: " + to
        initialsButton!!.text = transmitter.substring(0, 2)

        subjectText!!.text = message?.Tema
        dateText!!.text = getDateString(getDate(message?.Fecha_Registro!!))

        webView!!.setBackgroundColor(Color.TRANSPARENT)
        webView!!.getSettings().setBuiltInZoomControls(true)
        webView!!.getSettings().setDisplayZoomControls(false)
        webView!!.getSettings().setSupportZoom(true)
        webView!!.getSettings().setLoadWithOverviewMode(true)
        webView!!.getSettings().setUseWideViewPort(true)
        webView!!.getSettings().setBuiltInZoomControls(true)
        webView!!.setInitialScale(1)

        var msg:String = message?.Mensaje!!

        var doc = Jsoup.parse(msg)
        var body = doc.getElementsByTag("body")
        var head = doc.getElementsByTag("head")

        var myHead = head.toString()

        if(myHead == "<head></head>") {
            myHead = "<head>" +
                         "<style>" +
                           "img { width: 100%; left: 50%;position: relative;max-width: 100%;-webkit-transform: translateX(-50%);-moz-transform: translateX(-50%);-ms-transform: translateX(-50%);-o-transform: translateX(-50%);transform: translateX(-50%);}" +
                         "</style>" +
                         "<meta name=\"viewport\" content=\"initial-scale=1, width=device-width, height=device-height, viewport-fit=cover\">" +
                     "</head>\n"
        }

        var html = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                        myHead.toString() +
                        body.toString() +
                    "</html>"

        webView!!.settings.loadWithOverviewMode = true
        webView!!.settings.useWideViewPort = true
        webView!!.settings.javaScriptEnabled = true
        webView!!.settings.defaultTextEncodingName = "utf-8"
        // contentTextView.loadData(Html.fromHtml(html), "text/html", "utf-8")
        webView!!.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)

        // webView!!.loadData(html, "text/html", "UTF-8")

    }

    fun deleteMessage() {
        Service.shared().markMessageAsDelete(this, message?.id_Mensaje!!).enqueue(object: Callback<Any> {
            override fun onResponse(call: Call<Any>?, response: Response<Any>?) {
            }

            override fun onFailure(call: Call<Any>?, t: Throwable?) {
            }
        })
    }

    fun getDate(date: String) : Date? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

        try {
            return format.parse(date)
        } catch(e: ParseException) {
            Log.d("ERROR", "error: " + e.message)
            return null
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateString(date: Date?) : String? {
        val locale = Locale("es", "mx")
        val format = SimpleDateFormat("dd 'de' MMMM 'del' yyyy 'a las' HH:mm", locale)

        try {
            val dateTime = format.format(date)

            return dateTime
        } catch(e: Throwable) {
            Log.d("ERROR", "message: " + e.message)
            return null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
            R.id.replyId -> {
                val intent = Intent(this, SendMessageActivity::class.java)
                intent.putExtra("TEMA", message?.Tema)

                startActivity(intent)
            }
            R.id.attachmentId -> {
                val intent = Intent(this, AttachmentsActivity::class.java)
                intent.putExtra("attachments", attachments)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
