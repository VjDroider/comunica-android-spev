package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.file.FileCOM
import edu.campusvirtual.comunica.models.file.Response
import retrofit2.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Created by jonathan on 2/26/18.
 */
interface FileInterface {

    @Multipart
    @POST("/api/photo")
    fun postImage(@Part image: MultipartBody.Part) : Call<Response> ///Modulos/Comunica/upload.asmx/UploadFile

    @Multipart
    @POST("/Modulos/Comunica/upload.asmx/UploadFile")
    fun postImageCOM(@Part image: MultipartBody.Part) : Call<ResponseBody>

    @Multipart
    @POST("/Modulos/Comunica/upload.asmx/UploadFile")
    fun uploadVideo(@Part("file") description: RequestBody, @Part file: MultipartBody.Part): Call<String>

}