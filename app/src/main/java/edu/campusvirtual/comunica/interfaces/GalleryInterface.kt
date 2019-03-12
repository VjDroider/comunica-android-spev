package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.gallery.Response
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by jonathan on 3/2/18.
 */
interface GalleryInterface {

    @GET("/m/gallery")
    fun getGallery(): Call<Response>

}