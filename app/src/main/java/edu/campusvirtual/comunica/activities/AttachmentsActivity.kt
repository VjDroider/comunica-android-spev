package edu.campusvirtual.comunica.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TabHost
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.kaopiz.kprogresshud.KProgressHUD
import com.stfalcon.frescoimageviewer.ImageViewer
import edu.campusvirtual.comunica.adapters.AlbumsAdapter
import edu.campusvirtual.comunica.adapters.AttachmentsAdapter
import edu.campusvirtual.comunica.adapters.PhotosAdapter
import edu.campusvirtual.comunica.library.EmptyRecyclerView
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.attachment.Attachment
import edu.campusvirtual.comunica.models.gallery.Album
import edu.campusvirtual.comunica.models.gallery.Photo
import edu.campusvirtual.comunica.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

class AttachmentsActivity : AppCompatActivity() {

    var adapter: AttachmentsAdapter? = null
    var recycler: RecyclerView? = null
    var layoutManager: GridLayoutManager? = null
    var attachments = ArrayList<Attachment>()

    private var globalMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachments)

        attachments = intent.getSerializableExtra("attachments") as ArrayList<Attachment>

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        Fresco.initialize(this)

        recycler = findViewById(R.id.recycler)
        layoutManager = GridLayoutManager(applicationContext, 3)
        adapter = AttachmentsAdapter(this, attachments) { photo, position, longClick ->
            if(longClick) {
                if(attachments.get(position).isSelected) {
                    attachments.get(position).isSelected = false
                } else {
                    attachments.get(position).isSelected = true
                }
                adapter?.notifyDataSetChanged()

                //
                if(attachments.any() { it.isSelected }) {
                    globalMenu?.findItem(R.id.share)?.setVisible(true)
                } else {
                    globalMenu?.findItem(R.id.share)?.setVisible(false)
                }
            } else {
                if(attachments.get(position).isVideo) {
                    val intent = Intent(this, VideoViewerActivity::class.java)
                    intent.putExtra("path", attachments.get(position).url)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, ImageViewerActivity::class.java)
                    intent.putExtra("path", attachments.get(position).url)
                    startActivity(intent)
                }
            }
        }

        recycler!!.adapter = adapter
        recycler!!.layoutManager = layoutManager

        adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater

        inflater.inflate(R.menu.share_menu, menu)
        globalMenu = menu
        menu?.findItem(R.id.share)?.setVisible(false)

        return true
    }

    fun share(files: ArrayList<File>) {
        val uris:ArrayList<Uri> = ArrayList<Uri>();
        for(file in files){
            uris.add(Uri.fromFile(file));
        }

        val intent = Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("*/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivity(Intent.createChooser(intent, "Titulo"));
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
            R.id.share -> {
                // compartir
                val files: ArrayList<File> = arrayListOf()
                val attachmentsSelected = attachments.filter { s -> s.isSelected }
                for(attach in attachmentsSelected) {
                    val file = Uri.parse(attach.url)
                    val name = file.lastPathSegment.substring(0, file.lastPathSegment.lastIndexOf("."));
                    Util.downloadAssetByName(this, attach.url, name, completion = {
                        files.add(File(it))

                        if(attachmentsSelected.size == files.size) {
                            share(files)
                        }
                    }, failure = {
                        Toast.makeText(this, "Error al obtener un archivo", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
