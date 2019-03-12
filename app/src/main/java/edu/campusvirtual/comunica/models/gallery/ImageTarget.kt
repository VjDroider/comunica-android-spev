package edu.campusvirtual.comunica.models.gallery

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.FileNotFoundException
import java.io.IOException
import android.content.Intent
import android.R.attr.bitmap
import android.content.Context
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStorageDirectory
import java.io.File
import java.io.FileOutputStream


/**
 * Created by jonathan on 3/5/18.
 */
class ImageTarget(val uri: Uri, val context: Context): Target {

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "image/*"
        Log.d("LOCAL", "" + getLocalBitmapUri(bitmap))
        i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap))
        context.startActivity(Intent.createChooser(i, "Share Image"))
    }

    override fun onBitmapFailed(errorDrawable: Drawable) {

    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable) {

    }

    fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(getExternalStorageDirectory(), "share_image_" + System.currentTimeMillis() + ".png")
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bmpUri
    }
}