package edu.campusvirtual.comunica.models.inbox

import java.io.Serializable

class Report(
    var _id_Reporte_Envio: Int,
    var _Fromid: Int,
    var _Tema: String,
    var _Fecha: String,
    var _iError: Int,
    var _iContador_Email: Int,
    var _iContador_Email_Delivered: Int,
    var _iContador_Sms: Int,
    var _iContador_Sms_Delivered: Int,
    var _iContador_Mobile: Int,
    var _iContador_Mobile_Delivered: Int
): Serializable

class SingleReport(
    var ToId_Name:String,
    var id_Mensaje:Int,
    var id_Canal_Comunicacion:Int,
    var Enviado:Boolean,
    var Fecha_Enviado:String,
    var Leido:Boolean,
    var Fecha_Leido:String,
    var Acusado:Boolean,
    var id_Tipo_Mensaje:Int,
    var sError:String
)