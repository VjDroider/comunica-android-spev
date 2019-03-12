package edu.campusvirtual.comunica.activities

import android.app.ProgressDialog
import android.content.Context
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import android.widget.Toast
import edu.campusvirtual.comunica.R
import kotlinx.android.synthetic.main.activity_video_viewer.*
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import edu.campusvirtual.comunica.library.Util
import java.io.File


class VideoViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen()

        setContentView(R.layout.activity_video_viewer)
        val path = intent.getStringExtra("path")
        val file = Uri.parse(path)
        val name = file.lastPathSegment.substring(0, file.lastPathSegment.lastIndexOf("."));
        Util.downloadAssetByName(this, path, name, completion = {

            val videoView = findViewById(R.id.videoView) as VideoView
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
            val video = Uri.parse(it)
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

        }, failure = {
            Toast.makeText(this, "Error al obtener el video", Toast.LENGTH_SHORT).show()
        })



    }

    fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
