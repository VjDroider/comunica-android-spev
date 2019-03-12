package edu.campusvirtual.comunica.activities

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.R
import kotlinx.android.synthetic.main.activity_video.*
import java.io.File

class VideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_video)

        // TODO change video here
        // videoViewId.setVideoPath("andoid.resource://" + packageName + "/" + R.raw.video)

        var path = "android.resources://" + packageName + "/raw/video.mp4"
        val video = Uri.parse(path)
        val file = File(path)

        if(file.exists()) {
            val videoView = findViewById(R.id.videoViewId) as VideoView
            val mc = object: MediaController(this) {
                override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
                    if(event?.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                        finish()
                    }
                    return super.dispatchKeyEvent(event)
                }
            }

            mc.setAnchorView(videoView)
            mc.setMediaPlayer(videoView)
            videoView.setMediaController(mc)
            videoView.setVideoURI(video)
            videoView.setZOrderOnTop(true)

            var dialogBuilder = AlertDialog.Builder(this);
            var inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE ) as LayoutInflater
            var dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            var b = dialogBuilder.create();
            b.show();

            videoView.setOnPreparedListener {
                videoView.start()
                b.dismiss()
            }
        } else {
            // no existe
            Util.showAlert(this, "Error", "No existe video", "ok") {
                onBackPressed()
            }
        }

    }

    fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
