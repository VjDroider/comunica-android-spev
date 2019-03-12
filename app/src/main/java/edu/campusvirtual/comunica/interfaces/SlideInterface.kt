package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.slides.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by jonathan on 2/15/18.
 */
interface SlideInterface {

    @GET("/m/public/slides/{college}")
    fun getSlides(@Path("college") college: Int) : Call<Response>
}