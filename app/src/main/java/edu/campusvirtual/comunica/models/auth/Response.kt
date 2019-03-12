package edu.campusvirtual.comunica.models.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 2/13/18.
 */

class AuthResponse {

    @SerializedName("id_Persona")
    @Expose
    var id: Int ? = null

    @SerializedName("nombre_apellidopaterno_apellido_materno")
    @Expose
    open var fullname: String? = null

    @SerializedName("Email_Personal")
    @Expose
    open var email: String? = null

    @SerializedName("Telefono_Celular")
    @Expose
    open var phone: String? = null
}

class AuthResponseCOM(
        var id_persona: Int?,
        var email: String?,
        var fullname: String?
)