package edu.campusvirtual.comunica.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import com.squareup.picasso.Picasso
import edu.campusvirtual.comunica.R
import android.view.MenuItem
import android.view.View
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import edu.campusvirtual.comunica.library.Util.Companion.showAlert
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.login
import edu.campusvirtual.comunica.services.loginWithProvider
import java.util.*
import android.os.AsyncTask
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*
import com.google.firebase.iid.FirebaseInstanceId
import com.kaopiz.kprogresshud.KProgressHUD
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.library.Util.Companion.downloadAsset
import edu.campusvirtual.comunica.services.loginCOM
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.File
import java.io.IOException


class LoginActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private var callbackManager = CallbackManager.Factory.create()
    var mGoogleSignInClient: GoogleSignInClient? = null
    var mAuth: FirebaseAuth? = null
    var spin: KProgressHUD? = null
    var sessionMaganer: SessionManager? = null

    private val RC_SIGN_IN = 9001

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        setContentView(R.layout.activity_login)

        KeyboardVisibilityEvent.setEventListener(
                this
        ) {
            // some code depending on keyboard visiblity status
            if(it) {
                headerImageViewId.layoutParams.height = 160
            } else {
                headerImageViewId.layoutParams.height = 198
            }

            headerImageViewId.requestLayout()
        }

        sessionMaganer = SessionManager(this)
        setup()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            var result : GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                var account : GoogleSignInAccount = result.getSignInAccount()!!

                var run: Runnable = Runnable(function = {
                    try {
                        var scope = "oauth2:"+Scopes.EMAIL+" "+ Scopes.PROFILE;
                        var accessToken = GoogleAuthUtil.getToken(getApplicationContext(), account.getAccount(), scope,  Bundle());
                        onLogin("google", accessToken)
                    } catch ( e: IOException ) {
                        e.printStackTrace();
                    } catch ( e : GoogleAuthException ) {
                        e.printStackTrace();
                    }
                })

                AsyncTask.execute(run);

            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                        Log.d("error", exception.message)
                    }
                })
    }

    fun setupGoogle() {
        val gso:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()
    }

    fun onLogin(provider: String, accessToken: String) {
        var token = FirebaseInstanceId.getInstance().token

        if(token == null) {
            val deviceToken = sessionMaganer?.getDeviceToken()

            if(deviceToken != null) {
                token = deviceToken
            } else {
                token = "devicetokendontregister2"
            }
        }

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

    private fun setup() {
        setupGoogle()
        setupFacebook()
        setTitle("Login")
        val headerImageViewId = findViewById<ImageView>(R.id.headerImageViewId)

        downloadAsset(this, Util.COVERPAGE, Util.COVERPAGE, Util.COVERPAGE, completion = {
            val file = File(it)
            Picasso.with(applicationContext).load(file).fit().into(headerImageViewId)
        }, failure = {
            Picasso.with(applicationContext).load(Util.DEFAULT_COVERPAGE).fit().into(headerImageViewId)
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        setupLoginButton()
        setupLoginWithFacebook()
        setupLoginWithGoogle()
    }

    private fun setupLoginButton() {
        val buttonLoginId = findViewById<Button>(R.id.buttonLoginId)
        buttonLoginId.setOnClickListener(this)

    }

    private fun setupLoginWithFacebook() {
        val buttonLoginFacebookId = findViewById<Button>(R.id.buttonLoginFacebookId)
        buttonLoginFacebookId.setOnClickListener(this)

        if(Constants.backend == Constants.COMUNICA) {
            buttonLoginFacebookId.visibility = View.INVISIBLE
        }
    }

    private fun setupLoginWithGoogle() {
        val buttonLoginGoogleId = findViewById<Button>(R.id.buttonLoginGoogleId)
        buttonLoginGoogleId.setOnClickListener(this)

        if(Constants.backend == Constants.COMUNICA) {
            buttonLoginGoogleId.visibility = View.INVISIBLE
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
            showAlert(this, "Error", "El correo electronico es obligatorio")
            Util.stopLoadingPage(spin)
            return
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            showAlert(this, "Error", "No es un correo electronico valido")
            Util.stopLoadingPage(spin)
            return
        }

        if(password.isEmpty()) {
            showAlert(this, "Error", "La contraseña es obligatoria")
            Util.stopLoadingPage(spin)
            return
        }

        if(Constants.backend == Constants.COMUNICA) {
            Service.shared().loginCOM(this, username.toString(), password.toString(), token, completion = {
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


    override fun onClick(item: View?) {

        spin = Util.loadingPage(this, null, spin)

        when(item?.id) {
            R.id.buttonLoginId -> {
                validateCredentials()
            }
            R.id.buttonLoginGoogleId -> {
                val signInIntent = mGoogleSignInClient?.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            R.id.buttonLoginFacebookId -> {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


}
