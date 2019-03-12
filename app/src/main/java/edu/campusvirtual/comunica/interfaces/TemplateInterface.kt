package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.template.Response
import retrofit2.Call
import retrofit2.http.GET

interface TemplateInterface {

    @GET("/m/templates")
    fun getTemplates() : Call<Response>
}