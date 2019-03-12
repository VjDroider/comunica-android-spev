package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.inbox.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface MessageInterface {
    @GET("/m/messages")
    fun getMessages() : Call<Response>

    @GET("/Modulos/Comunica/get_mensajes.aspx")
    fun getMessagesCOM(
            @Query("token") token: String,
            @Query("email") email: String,
            @Query("id_Persona") idPerson: Int,
            @Query("modifiedSince") modifiedSince: String? = "2010-01-01T00:00:00"
    ) : Call<ArrayList<MessageCOM>>

    @GET("/m/messages/sent")
    fun getMessagesSent() : Call<Response>

    @GET("/api/ReporteMensajes")
    fun getReports(
        @Query("fromid") fromid: Int,
        @Query("fecha_inicio") start: String,
        @Query("fecha_final") end: String
    ) : Call<ResponseReportCOM>

    @GET("/api/ReporteMensajes/{id}")
    fun getReportDetails(@Path("id") id: Int) : Call<ResponseDetailReport>

    @PUT("/m/messages/read/{id}")
    fun markMessageAsRead(@Path("id") id: Int) : Call<Any>

    @GET("/Modulos/Comunica/Mensaje_Leido.aspx")
    fun markMessageAsReadCOM(@Query("id_mensaje") id: Int, @Query("id_Persona") id_Persona: Int, @Query("fecha") fecha: String = "") : Call<ResponseBody>

    @DELETE("/m/messages/{id}")
    fun markMessageAsDelete(@Path("id") id: Int) : Call<Any>

    @GET("/modulos/comunica/getInfopersonal.aspx")
    fun getUnreadMessagesCount() : Call<UnreadResponse>

    @GET("/modulos/comunica/getInfopersonal.aspx")
    fun getUnreadMessagesCountCOM(
            @Query("token") token: String,
            @Query("email") email: String,
            @Query("id_Colegio") id_Colegio: Int,
            @Query("id_Persona") id_Persona: Int
    ) : Call<UnreadResponseCOM>

    @POST("/m/messages/send")
    fun sendMessage(@Body body: Request) : Call<ResponseBody>

    @POST("/api/mensajes")
    fun sendMessageCOM(@Body body: RequestCOM) : Call<ResponseSend>
}
