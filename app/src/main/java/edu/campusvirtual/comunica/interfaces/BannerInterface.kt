package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.banner.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by jonathan on 2/16/18.
 */
interface BannerInterface {

    @GET("/m/banners/{id}")
    fun getBanners(@Path("id") collegeId: Int) : Call<Response>

}