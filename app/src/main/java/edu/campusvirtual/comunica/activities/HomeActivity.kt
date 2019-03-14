package edu.campusvirtual.comunica.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.Scopes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.kaopiz.kprogresshud.KProgressHUD
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.library.RegistrationService
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.configuration.Configuration
import edu.campusvirtual.comunica.models.contact.Contact
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.library.SupportDialogFragment
import edu.campusvirtual.comunica.services.*
import io.fabric.sdk.android.Fabric
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class HomeActivity : AppCompatActivity(), View.OnClickListener, SupportDialogFragment.BottomSheetListener  {


    private var callbackManager = CallbackManager.Factory.create()
    var mGoogleSignInClient: GoogleSignInClient? = null
    var mAuth: FirebaseAuth? = null
    var contact: Contact? = null
    var sessionMaganer: SessionManager? = null
    var spin: KProgressHUD? = null
    var config: Configuration? = null

    var dialog: SupportDialogFragment? = null

    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_home)
        // gif = findViewById(R.id.gifImageViewId)
        setup()
        // var gifImageViewId = findViewById<GifImageView>(R.id.gifImageViewId)


        dialog = SupportDialogFragment.newInstance()
        dialog!!.setListener(this)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val vmPolicy = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(vmPolicy.build())
    }

    fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("aqui", "aquiii")

        if (requestCode == RC_SIGN_IN) {
            var result : GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("aqui", "aqui" + result.status)
            if (result.isSuccess()) {
                var account : GoogleSignInAccount = result.getSignInAccount()!!

                var run: Runnable = Runnable(function = {
                    try {
                        var scope = "oauth2:"+ Scopes.EMAIL+" "+ Scopes.PROFILE;
                        var accessToken = GoogleAuthUtil.getToken(getApplicationContext(), account.getAccount(), scope,  Bundle());
                        onLogin("google", accessToken)
                    } catch ( e: IOException) {
                        e.printStackTrace();
                    } catch ( e : GoogleAuthException) {
                        e.printStackTrace();
                    }
                })

                AsyncTask.execute(run);

            } else {
                Util.stopLoadingPage(spin)
            }
        } else if(requestCode == 1001) {
            setupButtonVideo()
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
            Util.stopLoadingPage(spin)
        }
    }

    fun setupFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        onLogin("facebook", loginResult.accessToken.token)
                    }

                    override fun onCancel() {
                        // App code
                        Log.d("failure", "cancel")
                        Util.stopLoadingPage(spin)
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                        Log.d("error", exception.message)
                        Util.stopLoadingPage(spin)
                    }
                })
    }

    fun setupGoogle() {
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()
    }

    fun verifyAccess() {
        if(sessionMaganer?.isLoguedIn()!!) {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    fun setup() {
        Service.shared().getAllConfiguration(this, completion = {
            config = Configuration.getConfiguration(this, Configuration.VIDEO)
            setupButtonVideo()
        }, failure = {
        })
        val i = Intent(this, RegistrationService::class.java)
        startService(i)

        sessionMaganer = SessionManager(applicationContext)
        verifyAccess()
        setTitle("Barragan Moreno")
        setupButtons()
        setupFacebook()
        setupGoogle()
        setupSlider()
        setupTitle()
        setupContact()
        setupListGroups()


    }

    fun setupContact() {
        Service.shared().getContactInfo(this, completion = { contact ->
            this.contact = contact!!
        }, failure = {
            // fail
        })
    }

    fun setupTitle() {
        // val titleApp = findViewById<TextView>(R.id.titleApp)
/*
        if(Constants.backend == Constants.COMUNICA) {
            var configuration = Configuration.getConfiguration(this, Configuration.NAME)

            if (configuration != null) {
                titleApp.setText(configuration?.value)
            }
        } else {
            Service.shared().getConfig(this, "NombreInstitucion", completion = { config ->
                titleApp.setText(config?.value)
            }, failure = {

            })
        }
*/


    }

    fun setupListGroups() {
        var key = "id_Lista_GeoNotificaciones"
        Service.shared().getConfig(this, key, completion = { config ->
            // titleApp.setText(config?.value)
            config?.key = key
            config?.saveConfiguration(this)
        }, failure = {
            var x = "si"
        })

    }

    fun setupSlider() {
        // val fragmentNotifications = supportFragmentManager.beginTransaction()

        // fragmentNotifications.replace(R.id.sliderFragmentId, SliderFragment())

        // fragmentNotifications.commit()
    }

    fun onLogin(provider: String, accessToken: String) {
        var token = FirebaseInstanceId.getInstance().token

        if(token == null) {
            val deviceToken = sessionMaganer?.getDeviceToken()

            if(deviceToken != null) {
                token = deviceToken
            } else {
                token = "devicetokendontregister"
            }
        }

        Log.d("TOKEN", token)
        Service.shared()
                .loginWithProvider(this, provider, accessToken, token, completion = {
                    Util.stopLoadingPage(spin)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }, failure = {
                    // fail
                    Util.stopLoadingPage(spin)
                })
    }

    fun setupButtons() {
        setupButtonMap()
        setupButtonPlayVideo()
        setupButtonEmail()
        setupButtonLogin()
        setupButtonGoogle()
        setupButtonFacebook()
    }

    fun setupButtonMap() {
        var mapButtonId = findViewById<Button>(R.id.mapButtonId)
        mapButtonId.setBackgroundResource(R.drawable.ic_vector_marker)
        //mapButtonId.setTypeface(Typeface.createFromAsset(assets, "fontawesome-webfont.ttf"))
        //mapButtonId.setText(getString(R.string.fa_icon_map_marker))
        mapButtonId.textSize = 28.toFloat()
        mapButtonId.setOnClickListener(this)
    }

    fun setupButtonPlayVideo() {
        val callButtonId = findViewById<Button>(R.id.playButtonId)
        callButtonId.setBackgroundResource(R.drawable.ic_vector_play_button)
        //callButtonId.setTypeface(Typeface.createFromAsset(assets, "fontawesome-webfont.ttf"))
        //callButtonId.setText(getString(R.string.fa_icon_phone))
        callButtonId.textSize = 28.toFloat()
        callButtonId.setOnClickListener(this)
    }

    fun setupButtonEmail() {
        val emailButtonId = findViewById<Button>(R.id.emailButtonId)
        emailButtonId.setBackgroundResource(R.drawable.ic_vector_support)
        // emailButtonId.setTypeface(Typeface.createFromAsset(assets, "fontawesome-webfont.ttf"))
        // emailButtonId.setText(getString(R.string.fa_icon_envelope_o))
        emailButtonId.textSize = 28.toFloat()
        emailButtonId.setOnClickListener(this)
    }

    fun setupButtonGoogle() {
        val googleButtonId = findViewById<Button>(R.id.googleButtonId)
        googleButtonId.setBackgroundResource(R.drawable.rounded_button_google)
        googleButtonId.setTypeface(Typeface.createFromAsset(assets, "fontawesome-webfont.ttf"))
        googleButtonId.setText(getString(R.string.fa_icon_google))
        googleButtonId.textSize = 28.toFloat()
        googleButtonId.setOnClickListener(this)

        if(Constants.backend == Constants.COMUNICA) {
            googleButtonId.visibility = View.INVISIBLE
        }
    }

    fun setupButtonFacebook() {
        val facebookButtonId = findViewById<Button>(R.id.facebookButtonId)
        facebookButtonId.setBackgroundResource(R.drawable.rounded_button_facebook)
        facebookButtonId.setTypeface(Typeface.createFromAsset(assets, "fontawesome-webfont.ttf"))
        facebookButtonId.setText(getString(R.string.fa_icon_facebook))
        facebookButtonId.textSize = 28.toFloat()
        facebookButtonId.setOnClickListener(this)

        if(Constants.backend == Constants.COMUNICA) {
            facebookButtonId.visibility = View.INVISIBLE
        }
    }

    fun setupButtonVideo() {
        // playButtonId.setOnClickListener(this)

/*
        if(config != null) {
            if(config?.value == "") {
                playButtonId.visibility = View.INVISIBLE
                return
            }
            var video = config?.value!!
            var mime = video.substring(video.lastIndexOf(".") + 1,
                    video.length);
            var downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            var path = downloadsPath + File.separator + "video." + mime
            var file = File(path)

            if(file.exists() && video == sessionMaganer?.getVideo()) {
                playButtonId.visibility = View.VISIBLE
            } else {
                Log.d("AQUI1", "downloading")
                playButtonId.visibility = View.INVISIBLE
                sessionMaganer?.onRegisterVideo(video)
                // download(video, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())
                // downloadInBackground(video, "video." + mime)
            }
        } else {
            playButtonId.visibility = View.INVISIBLE
        }*/
    }

    @SuppressLint("ResourceType")
    fun setupButtonLogin() {
        val loginButton = findViewById<Button>(R.id.buttonLoginId)

        loginButton.setOnClickListener(this)
    }

    private fun call() {
        val callIntent = Intent(Intent.ACTION_CALL)

        if(Constants.backend == Constants.COMUNICA) {
            var config = Configuration.getConfiguration(this, Configuration.PHONE)
            if(config == null) {
                Util.showAlert(this, "Alerta", "No encontramos el teléfono en la base de datos")
                return
            } else {
                callIntent.setData(Uri.parse("tel:" + config?.value))
            }

        } else {
            callIntent.setData(Uri.parse("tel:" + contact?.phone))
        }



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 101)
        } else {
            startActivity(callIntent)
        }

    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_VIEW)

        if(Constants.backend == Constants.COMUNICA) {
            val config = Configuration.getConfiguration(this, Configuration.EMAIL)
            intent.data = Uri.parse("mailto:" + config?.value)
            startActivity(Intent.createChooser(intent, "E-mail"))
        } else {
            intent.data = Uri.parse("mailto:" + contact?.email)
            startActivity(Intent.createChooser(intent, "E-mail"))
        }
    }

    private fun validateCredentials() {
        val usernameTextEditId = findViewById<EditText>(R.id.usernameTextEditId)
        val passwordTextEditId = findViewById<EditText>(R.id.passwordTextEditId)
        val username = usernameTextEditId.text
        val password = passwordTextEditId.text

        var token = FirebaseInstanceId.getInstance().token

        if(token == null) {
            val deviceToken = sessionMaganer?.getDeviceToken()

            if(deviceToken != null) {
                token = deviceToken
            } else {
                token = "devicetokendontregister"
            }
        }


        if(username.isEmpty()) {
            Util.showAlert(this, "Error", "El correo electronico es obligatorio")
            Util.stopLoadingPage(spin)
            return
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            Util.showAlert(this, "Error", "No es un correo electronico valido")
            Util.stopLoadingPage(spin)
            return
        }

        if(password.isEmpty()) {
            Util.showAlert(this, "Error", "La contraseña es obligatoria")
            Util.stopLoadingPage(spin)
            return
        }

        if(Constants.backend == Constants.COMUNICA) {
            spin = Util.loadingPage(this, null, spin)
            Service.shared().loginCOM(this, username.toString(), password.toString(), token, completion = {
                Answers.getInstance().logLogin(LoginEvent().putMethod("credentials").putSuccess(true).putCustomAttribute("username", username.toString()).putCustomAttribute("password", password.toString()))
                var session = SessionManager(this)
                session.onRegisterSuccess(token)
                session.savePass(password.toString())
                Util.stopLoadingPage(spin)
                val intent = Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

            }, failure = {
                // failure
                Util.stopLoadingPage(spin)
            })
        } else {
            Service.shared().login(this, username.toString(), password.toString(), token, completion = {
                Util.stopLoadingPage(spin)
                val intent = Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

            }, failure = {
                // failure
                Util.stopLoadingPage(spin)
            })
        }

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.buttonLoginId -> {
                validateCredentials()
            }
            R.id.mapButtonId -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
            R.id.emailButtonId -> {
                dialog?.show(supportFragmentManager, "support")

                // sendEmail()
            }
            R.id.googleButtonId -> {
                spin = Util.loadingPage(this, null, spin)
                val signInIntent = mGoogleSignInClient?.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            R.id.facebookButtonId -> {
                spin = Util.loadingPage(this, null, spin)
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            }
            R.id.playButtonId -> {
                startActivity(Intent(this, VideoActivity::class.java))
            }
        }
    }

    fun tryDownload(urlToDownload: String, filename: String) {
        var request = DownloadManager.Request(Uri.parse(urlToDownload))

        request.setTitle("Descargando...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
        //request.setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), filename)


        var manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

    fun downloadInBackground(urlToDownload: String, filename: String) {
        var permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
        } else {
            //TODO
            tryDownload(urlToDownload, filename)
        }

    }

    fun download(fileURL: String, saveDir: String)  {
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
            fileName = "video." + mime
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

            Toast.makeText(this, "Archivo descargado con exito", Toast.LENGTH_LONG).show()
            val file = File(saveFilePath)

           /* val target = Intent(Intent.ACTION_VIEW)
            target.setDataAndType(Uri.fromFile(file), mime)
            target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

            startActivity(target)*/
            Util.stopLoadingPage(spin)
            startActivity(Intent(this, VideoActivity::class.java))
        } else {
            Toast.makeText(this, "No file to download. Server replied HTTP code: " + responseCode, Toast.LENGTH_LONG).show()
            Util.stopLoadingPage(spin)
        }
        httpConn.disconnect();
    }

    override fun onSelect(id: Int) {
        when(id) {
            R.id.callId -> call()
            R.id.msgId -> sendEmail()
        }
        dialog?.dismiss()
    }

}
