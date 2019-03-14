package edu.campusvirtual.comunica.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.kaopiz.kprogresshud.KProgressHUD
import com.rengwuxian.materialedittext.MaterialEditText
import edu.campusvirtual.comunica.adapters.ImageAdapter
import edu.campusvirtual.comunica.adapters.MoreAdapter
import edu.campusvirtual.comunica.models.Constants
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_view_message.*
import edu.campusvirtual.comunica.library.*
import edu.campusvirtual.comunica.models.inbox.MessageDB
import edu.campusvirtual.comunica.models.inbox.Request
import edu.campusvirtual.comunica.models.inbox.RequestCOM
import edu.campusvirtual.comunica.models.MoreItem
import edu.campusvirtual.comunica.models.receiver.Receiver
import edu.campusvirtual.comunica.models.receiver.ReceiverCOM
import edu.campusvirtual.comunica.models.receiver.ReceiverDB
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.services.*
import kotlinx.android.synthetic.main.activity_send_message.*
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidbuts.multispinnerfilter.KeyPairBoolData
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch
import com.androidbuts.multispinnerfilter.SpinnerListener
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.esafirm.imagepicker.features.ReturnMode
import com.iceteck.silicompressorr.SiliCompressor
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class SendMessageActivity : AppCompatActivity() {

    var items = ArrayList<String>()
    var multiSpinner: MultiSpinnerSearch? = null
    var message: String? = null
    var tema: MaterialEditText? = null
    var spin: KProgressHUD? = null
    var receivers = ArrayList<ReceiverCOM>()
    var images:ArrayList<String> = ArrayList()
    var isVideo:ArrayList<String?> = ArrayList()
    var isLoading: ArrayList<Boolean> = ArrayList()
    var isCompriming: ArrayList<Boolean> = ArrayList()
    var theme:String? = null
    var loadingView: LinearLayout? = null
    var loadingView2: LinearLayout? = null
    var queueImages:ArrayList<Image> = arrayListOf()
    var queueVideos:ArrayList<String> = arrayListOf()
    var toast:Toast? = null

    var session:SessionManager? = null
    lateinit var recycler: RecyclerView
    lateinit var adapter: ImageAdapter
    var listArray: ArrayList<KeyPairBoolData> = arrayListOf()
    val requestCode = 301

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        toast = Toast(this)
        val recyclerViewOrientation = LinearLayout.VERTICAL
        val dividerDecoration = DividerItemDecoration(this, recyclerViewOrientation)
        val layoutManager = LinearLayoutManager(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1002)
        }

        loadingView = findViewById(R.id.layout_loading)
        loadingView2 = findViewById(R.id.layout_loading2)
        recycler = findViewById(R.id.recyclerView)
        recycler.layoutManager = layoutManager
        session = SessionManager(this)
        adapter = ImageAdapter(this, images) { item ->

            var builder = AlertDialog.Builder(this)
            var options:Array<String> = arrayOf("Eliminar")

            builder.setItems(options, DialogInterface.OnClickListener { dialogInterface, which ->
                Log.d("ELIMINALA", "YES")
                adapter.deleteItem(item)
                isVideo.removeAt(item)
            }).show()
        }

        recycler.adapter = adapter
        recycler.addItemDecoration(dividerDecoration)
        adapter.notifyDataSetChanged()

        receivers.clear()
        message = intent.getStringExtra("html")
        tema = findViewById(R.id.temaId)
        setTitle("")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        multiSpinner = findViewById(R.id.viewMultiSpinner)
        // multiSpinner!!.setItems(items, items, "default", "title")
        listArray = arrayListOf()
        multiSpinner!!.setItems(listArray, -1,object: SpinnerListener {
            override fun onItemsSelected(p0: MutableList<KeyPairBoolData>?) {

            }

        })

        spin = Util.loadingPage(this, null, spin)
        getReceiversDB()

        button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                ImagePicker.create(this)
                        .folderMode(false)
                        .showCamera(true)
                        .multi()
                        .enableLog(true)
                        .start(requestCode)
            }
        }

        button2.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                openVideoChooser()
            }

        }

        var intent = getIntent()
        theme = intent.getStringExtra("TEMA")

        if(theme != null) {
            tema?.text = SpannableStringBuilder(theme)
            tema?.inputType = InputType.TYPE_NULL
        }

        if(Constants.backend == Constants.COMUNICA) {
            Service.shared().getReceiversCOM(applicationContext, completion = { receivers ->
                this.receivers.addAll(receivers!!)
                for (i in receivers) {
                    val h = KeyPairBoolData()
                    h.id = i.id_Lista_Distribucion.toLong()
                    h.name = i.Nombre
                    h.isSelected = false
                    listArray.add(h)
                }
                multiSpinner!!.setItems(listArray, -1,object: SpinnerListener {
                    override fun onItemsSelected(p0: MutableList<KeyPairBoolData>?) {

                    }

                })
                // multiSpinner!!.setItems(getNames(receivers), getIds(receivers), "Destinatarios", "Seletcciona varios remitentes")
                stopLoading()
                sync()
            }, failure = {
                stopLoading()
            })
        } else {
            /*Service.shared().getReceivers(applicationContext, completion = { receivers ->
                this.receivers.addAll(receivers?.filter { true }!!)
                multiSpinner!!.setItems(getNames(receivers), getIds(receivers), "Selecciona", "Seletcciona varios remitentes")
                stopLoading()
                sync()
            }, failure = {
                // multiSpinner!!.setItems(ArrayList(), ArrayList(), "Error al cargar destinatarios", "Seletcciona varios remitentes")
                stopLoading()
            })*/
        }


    }

    fun setupLoading() {
        var anyInQueue = queueImages.size + queueVideos.size
        if(isLoading.size > 0 || anyInQueue > 0) {
            labelPersonId4.text = "Cargando " + isLoading.size + " archivo(s) y "+ anyInQueue +" en espera ..."
            loadingView?.visibility = View.VISIBLE
        } else {
            loadingView?.visibility = View.GONE
        }
    }

    fun setupCompriming() {
        if(isCompriming.size > 0) {
            labelPersonId5.text = "Comprimiendo " + isCompriming.size + " video(s) y "+ queueVideos.size +" en espera ..."
            loadingView2?.visibility = View.VISIBLE
        } else {
            loadingView2?.visibility = View.GONE
        }
    }

    fun openVideoChooser() {
        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "video/mp4"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select videos"), 99)
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "video/mp4"
            startActivityForResult(intent, 99)
        }
    }

    fun getSelectedVideos(requestCode: Int, data: Intent): List<String>  {

        var result:ArrayList<String> = ArrayList<String>();

        var clipData = data.getClipData();
        if(clipData != null) {
            for(i in 0..clipData.getItemCount() - 1) {
                var videoItem = clipData.getItemAt(i);
                var videoURI = videoItem.getUri();
                var filePath = getPath(this, videoURI);
                result.add(filePath!!);
            }
        }
        else {
            var videoURI = data.getData();
            var filePath = getPath(this, videoURI);
            result.add(filePath!!)
        }

        return result.toList();
    }

    fun getPath(context: Context, uri: Uri): String?  {

        var isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                var docId = DocumentsContract.getDocumentId(uri);
                var split = docId.split(":");
                var type = split[0];

                if ("primary".equals(type, true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                var id = DocumentsContract.getDocumentId(uri);
                var contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), id.toLong());

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                var docId = DocumentsContract.getDocumentId(uri);
                var split = docId.split(":");
                var type = split[0];

                var contentUri:Uri? = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                var selection = "_id=?";
                var selectionArgs: Array<String> = arrayOf(split[1])

                return getDataColumn(context, contentUri!!, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equals(uri.getScheme(), true)) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equals(uri.getScheme(), true)) {
            return uri.getPath();
        }

        return null;
    }

    fun showErrorAtUploadFile() {
        Toast.makeText(this, "Error al subir el archivo", Toast.LENGTH_SHORT).show()
    }

    fun getDataColumn(context: Context, uri: Uri, selection: String?,
                      selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor?.moveToFirst()) {
                val index = cursor?.getColumnIndexOrThrow(column)
                return cursor?.getString(index)
            }
        } finally {
            if (cursor != null)
                cursor!!.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun uploadImagesSynchronous(images: List<Image>) {
        queueImages.addAll(images)
        uploadImagesQueue()
    }

    fun uploadVideosSynchronous(selectVideos: List<String>) {
        queueVideos.addAll(selectVideos)
        uploadVideosQueue()
    }

    fun uploadQueue() {
        if(queueImages.size > 0) {
            uploadImagesQueue()
        } else {
            uploadVideosQueue()
        }
    }

    fun uploadVideosQueue() {
        if(isLoading.size == 0 && queueVideos.size > 0) {
            val v = queueVideos.get(0)

            uploadVideoWithRetrofit(v)
        }
        setupCompriming()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == this.requestCode && data != null) {
            val images = ImagePicker.getImages(data)

            uploadImagesSynchronous(images)

        } else if(requestCode == 99 && data != null) {
            var selectVideos = getSelectedVideos(requestCode, data!!)

            uploadVideosSynchronous(selectVideos)

        }
    }

    fun uploadImagesQueue() {
        if(isLoading.size == 0 && queueImages.size > 0 && isCompriming.size == 0) {
            val i = queueImages.get(0)

            uploadImage(i)
        }
    }

    fun uploadImage(image: Image) {
        val path= Compress(this).compressImage(image.path)
        isLoading.add(true)
        setupLoading()
        Service.shared().saveLog(this, "TRY_UPLOAD_IMAGE", completion = {

        }, failure = {

        })
        Service.shared().uploadImageCOM(this, path, completion = {
            images.add(it)
            isVideo.add(null)
            isLoading.removeAt(0)

            Log.d("LOGGG", "entro1")
            adapter.notifyDataSetChanged()
            deleteImage(path)
            queueImages.removeAt(0)
            setupLoading()
            uploadQueue()
            Answers.getInstance().logCustom(
                CustomEvent("UPLOAD_IMAGE")
                    .putCustomAttribute("count", "1")
                    .putCustomAttribute("who", session!!.getFullname())
                    .putCustomAttribute("msg", it)
            )
            Service.shared().saveLog(this, "SUCCESS_UPLOAD_IMAGE", completion = {

            }, failure = {

            })
        }, failure = {
            Service.shared().saveLog(this, "TRY2_UPLOAD_IMAGE", completion = {

            }, failure = {

            })
            Answers.getInstance().logCustom(
                CustomEvent("FAIL_UPLOAD_IMAGE")
                    .putCustomAttribute("count", "1")
                    .putCustomAttribute("who", session!!.getFullname())
                    .putCustomAttribute("msg", it)
            )
            Service.shared().uploadImageCOM(this, path, completion = {
                images.add(it)
                isVideo.add(null)
                isLoading.removeAt(0)
                Log.d("LOGGG", "entro2")
                adapter.notifyDataSetChanged()
                deleteImage(path)
                queueImages.removeAt(0)
                setupLoading()
                uploadQueue()
                Answers.getInstance().logCustom(
                    CustomEvent("UPLOAD_IMAGE")
                        .putCustomAttribute("count", "2")
                        .putCustomAttribute("who", session!!.getFullname())
                        .putCustomAttribute("msg", it)
                )
                Service.shared().saveLog(this, "SUCCESS2_UPLOAD_IMAGE", completion = {

                }, failure = {

                })
            }, failure = {
                showErrorAtUploadFile()
                isLoading.removeAt(0)
                deleteImage(path)
                queueImages.removeAt(0)
                setupLoading()
                uploadQueue()
                Answers.getInstance().logCustom(
                    CustomEvent("FAIL_UPLOAD_IMAGE")
                        .putCustomAttribute("count", "2")
                        .putCustomAttribute("who", session!!.getFullname())
                        .putCustomAttribute("msg", it)
                )
                Service.shared().saveLog(this, "FAILURE_UPLOAD_IMAGE", completion = {

                }, failure = {

                })
            })
        })
    }

    fun deleteImage(path: String) {
        var file = File(path)
        if(file.exists()) {
            if(file.delete()) {
                Log.d("LOG", "image deleted");
            }
        }
    }


    fun uploadVideoWithRetrofit(path: String) {
        if(isCompriming.size > 0 || isLoading.size > 0) {
            return
        }

        val file = File(path)
        var uri = Uri.parse(path)
        var dest = File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/src")
        dest.mkdir()


        queueVideos.removeAt(0)
        isCompriming.add(true)
        setupCompriming()
        setupLoading()
        Thread({
            try {
                val _path = SiliCompressor.with(this).compressVideo(path, dest.path)
                runOnUiThread {
                    var file2 = File(_path)
                    isCompriming.removeAt(0)
                    isLoading.add(true)
                    setupLoading()
                    setupCompriming()
                    Service.shared().saveLog(this, "TRY_UPLOAD_VIDEO", completion = {

                    }, failure = {

                    })
                    Service.shared().uploadVideoCOM(this, file = file2, completion = {
                        isLoading.removeAt(0)
                        isVideo.add(it)
                        setupLoading()
                        setupCompriming()
                        uploadQueue()
                        images.add(path)

                        adapter.notifyDataSetChanged()
                        deleteImage(_path)
                        Service.shared().saveLog(this, "SUCCESS_UPLOAD_VIDEO", completion = {

                        }, failure = {

                        })
                    }, failure = {
                        Service.shared().saveLog(this, "TRY2_UPLOAD_VIDEO", completion = {

                        }, failure = {

                        })
                        Answers.getInstance().logCustom(
                            CustomEvent("FAIL_UPLOAD_VIDEO")
                                .putCustomAttribute("count", "1")
                                .putCustomAttribute("who", session!!.getFullname())
                                .putCustomAttribute("msg", it)
                        )
                        Service.shared().uploadVideoCOM(this, file = file2, completion = {
                            isLoading.removeAt(0)
                            isVideo.add(it)
                            setupLoading()
                            setupCompriming()
                            uploadQueue()
                            images.add(path)
                            deleteImage(_path)
                            adapter.notifyDataSetChanged()
                            Service.shared().saveLog(this, "SUCCESS2_UPLOAD_VIDEO", completion = {

                            }, failure = {

                            })
                        }, failure = {
                            showErrorAtUploadFile()
                            isLoading.removeAt(0)
                            setupLoading()
                            setupCompriming()
                            uploadQueue()
                            deleteImage(_path)
                            Answers.getInstance().logCustom(
                                CustomEvent("FAIL_UPLOAD_VIDEO")
                                    .putCustomAttribute("count", "2")
                                    .putCustomAttribute("who", session!!.getFullname())
                                    .putCustomAttribute("msg", it)
                            )
                            Service.shared().saveLog(this, "FAILURE_UPLOAD_VIDEO", completion = {

                            }, failure = {

                            })
                        })
                    })
                }
            } catch(e: Exception) {
                isCompriming.removeAt(0)
                setupCompriming()
                setupLoading()
                uploadQueue()
            }

        }).start()
        Toast.makeText(this, "Espere a que su video termine de comprimirse, el tiempo puede variar dependiendo de el video seleccionado", Toast.LENGTH_LONG).show()
    }

    fun getReceiversDB() {
        Realm.init(this)
        val config = getDefaultConfig("receivers.realm")
        val realm = Realm.getInstance(config)

        val _receivers = realm.where(ReceiverDB::class.java).findAll()
        receivers.clear()
        for(m in _receivers) {
            receivers.add(m.toModelCOM())
        }

        // multiSpinner!!.setItems(getNames(receivers), getIds(receivers), "Selecciona", "Seletcciona varios remitentes")
    }

    fun sync() {
        Realm.init(this)

        val config = getDefaultConfig("receivers.realm")
        val realm = Realm.getInstance(config)
        // 1 - eliminar mensajes del mailbox X
        realm.beginTransaction()
        val results = realm.where(ReceiverDB::class.java).findAll()
        results.deleteAllFromRealm()

        // 2 - agregó los nuevos elementos a la base de datos

        for(message in receivers) {
            realm.copyToRealm(message.toDB())
        }
        realm.commitTransaction()
    }

    fun stopLoading() {
        Util.stopLoadingPage(spin)
    }

    fun getNames(items: ArrayList<ReceiverCOM>) : List<String> {
        return items.map { it.Nombre }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater

        inflater.inflate(R.menu.send_message_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
            R.id.sendId -> {
                sendMessage()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun sendMessage() {
        var session = SessionManager(this)
        var signature = session.onGet("signature")
        if(tema?.text?.isEmpty()!!) {
            Util.showAlert(this, "Error", "El tema no puede estar vacio")
            return
        }

        val people = getPeople()
        val groups = getGroups()
        var subject = tema!!.text

        if(people.isEmpty() && groups.isEmpty()) {
            Util.showAlert(this, "Error", "No seleccionaste ningun destinatario")
            return
        }

        if(messageId?.text?.isEmpty()!!) {
            Util.showAlert(this, "Error", "El mensaje no puede estar vacio")
            return
        }

        var msg = messageId.text.toString()

        for(i in 0..images.size - 1) {
            if(isVideo.get(i) != null) {
                var video = images.get(i)
                var mime =  MimeTypeMap.getFileExtensionFromUrl(isVideo.get(i))
                val tag = "<video src='" + isVideo.get(i) + "' class='vi' preload='none' controls='controls'><source src='" + isVideo.get(i) + "'><source src='" + isVideo.get(i) + "' type='"+ mime +"'>Your browser does not support the video tag.</video><br/><br/>"
                msg = msg + tag
            } else {
                var image = images.get(i)
                val tag = "<img class='ri' src='" + image + "' /><br/><br/>"
                msg = msg + tag
            }

        }


        if(signature != null && !signature.isEmpty()) {
            var sign = "\n\n\n<h3>" + signature + "</h3>"
            msg = msg + sign
        }

        if(isLoading.size > 0) {
            var alert = AlertDialog.Builder(this)
            alert.setTitle("Alerta")
            alert.setMessage("Tienes archivos aun cargandose, espera a que termine de cargar todos.")
            alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, i -> dialog.dismiss() })
            alert.setNegativeButton("Enviar", DialogInterface.OnClickListener { dialog, i ->  sentInfo(groups, subject.toString(), msg, session, people)})
            alert.show()
            return
        }

        if(isCompriming.size > 0) {
            var alert = AlertDialog.Builder(this)
            alert.setTitle("Alerta")
            alert.setMessage("Tienes videos comprimiendose, espera a que termine de cargar todos.")
            alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, i -> dialog.dismiss() })
            alert.setNegativeButton("Enviar", DialogInterface.OnClickListener { dialog, i ->  sentInfo(groups, subject.toString(), msg, session, people)})
            alert.show()
            return
        }

        sentInfo(groups, subject.toString(), msg, session, people)


    }

    fun sentInfo(groups: String, subject: String, msg: String, session: SessionManager, people: String) {
        if(Constants.backend == Constants.COMUNICA) {
            var body = RequestCOM()

            body.id_Colegio = Constants.collegeId
            body.ids_lista_distribucion = groups
            body.tema = subject.toString()
            body.mensaje = msg
            body.id_Persona = session.getId()

            spin = Util.loadingPage(this, null, spin)
            Service.shared().saveLog(this, "TRY_SEND_MESSAE", completion = {

            }, failure = {

            })
            Service.shared().sentMessageCOM(applicationContext, body, completion = {
                Answers.getInstance().logCustom(CustomEvent("SendMessage").putCustomAttribute("to", body.ids_lista_distribucion).putCustomAttribute("from", session.getFullname()).putCustomAttribute("subject", body.tema))
                Util.showAlert(this, "Exito", "Mensaje enviado correctamente")
                stopLoading()
                Service.shared().saveLog(this, "SUCCESS_SEND_MESSAE", completion = {

                }, failure = {

                })
            }, failure = { msg ->
                Util.showAlert(this, "Error", "Error al enviar el mensaje")
                Answers.getInstance().logCustom(CustomEvent("FailSendMessage").putCustomAttribute("to", body.ids_lista_distribucion).putCustomAttribute("from", session.getFullname()).putCustomAttribute("subject", body.tema).putCustomAttribute("msg", msg))
                stopLoading()
                Service.shared().saveLog(this, "FAILURE_SEND_MESSAE", completion = {

                }, failure = {

                })
            })

        } else {
            val body = Request()

            body.person_ids = people
            body.distribution_list_ids = groups
            body.subject = subject.toString()
            body.message = message!!

            spin = Util.loadingPage(this, null, spin)
            Service.shared().sentMessage(applicationContext, body, completion = {
                Util.showAlert(this, "Exito", "Mensaje enviado correctamente", callback = {
                    onBackPressed()
                })
                stopLoading()
            }, failure = {
                Util.showAlert(this, "Error", "El mensaje se guardará en mensajes pendientes y automaticamente se intentará enviar más tarde")
                saveMessagePending(message!!, subject.toString(), groups, people)
                stopLoading()
            })
        }
    }

    fun saveMessagePending(message: String, subject: String, distribution_list_ids: String, person_ids: String) {
        val mailbox = "Pendiente"
        val sessionManager = SessionManager(this)

        val messageDB = MessageDB()
        messageDB.mailbox = mailbox
        messageDB.message = message
        messageDB.subject = subject
        messageDB.distribution_list_ids = distribution_list_ids
        messageDB.person_ids = person_ids
        messageDB.transmitter_name = sessionManager.getFullname()!!

        val config = getDefaultConfig("messages.realm")
        val realm = Realm.getInstance(config)

        realm.beginTransaction()
        realm.copyToRealm(messageDB)
        realm.commitTransaction()
    }

    fun getPeople(): String {

        var myPeople = ""
        val list:List<String> = arrayListOf()// multiSpinner?.selectedIdsAsString?.split(",")!!

        for (i in list.indices) {
            var data = list[i].split("-")
            if(data[0] == "person") {
                myPeople += data[1]
            }

            if(i < list.size -1) {
                myPeople += ","
            }
        }

        return myPeople

    }

    fun getGroups(): String {

        var myGroups = ""
        val list:List<String> = arrayListOf()//multiSpinner?.selectedIdsAsString?.split(",")!!

        for(i in listArray.filter { it.isSelected }.indices) {
            var ele = listArray.filter { it.isSelected }.get(i)
            myGroups += ele.id

            if(i < listArray.filter { it.isSelected }.size -1) {
                myGroups += ","
            }
        }

        return myGroups
    }

    fun getIds(items: ArrayList<ReceiverCOM>) : List<String> {
        return items.map {
            "list-" + it.id_Lista_Distribucion
        }
    }

}
