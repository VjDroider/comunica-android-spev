package edu.campusvirtual.comunica.activities

import android.os.Bundle
import edu.campusvirtual.comunica.R
import kotlinx.android.synthetic.main.activity_image_viewer.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import java.net.URL


class ImageViewerActivity : AppCompatActivity() {

    var path: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        path = intent.getStringExtra("path")

        val url = URL(path)
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        imageView.setImageBitmap(bmp)
    }
}
