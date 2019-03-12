package edu.campusvirtual.comunica.models.inbox

/**
 * Created by jonathan on 2/26/18.
 */
class Request {

    var subject: String = ""

    var message: String = ""

    var distribution_list_ids: String = ""

    var person_ids: String = ""

}

class RequestCOM {
    var id_Colegio: Int = 0
    var tema: String = ""
    var mensaje: String = ""
    var urlAttachment: String = ""
    var id_Persona: Int = -1
    var ids_lista_distribucion: String = ""
    var id_Personas: String = ""
    var id_Canal_Comunicacion: Int = 3
    var id_Tipo_Mensaje: Int = 1

}