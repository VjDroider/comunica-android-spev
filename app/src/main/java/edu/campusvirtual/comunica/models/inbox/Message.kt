package edu.campusvirtual.comunica.models.inbox

import io.realm.RealmObject
import java.io.Serializable

class Message(
        var id: Int,
        var message: String,
        var read: Boolean,
        var registration_date: String,
        var received: Boolean,
        var receiver_name: String?,
        var sent: Boolean,
        var subject: String,
        var transmitter_name: String?
) : Serializable {

    fun messageToDb(mailbox: String): MessageDB {
        val m = MessageDB()

        m.id = id
        m.message = message
        m.read = read
        m.registration_date = registration_date
        m.received = received
        m.receiver_name = receiver_name
        m.sent = sent
        m.subject = subject
        m.transmitter_name = transmitter_name
        m.mailbox = mailbox

        return m
    }
}

open class MessageDB(): RealmObject() {
    var id: Int = 0
    var message: String? = null
    var read: Boolean = false
    var registration_date: String = ""
    var received: Boolean = false
    var receiver_name: String? = ""
    var sent: Boolean = false
    var subject: String = ""
    var transmitter_name: String? = ""
    var mailbox: String = ""

    var distribution_list_ids: String = ""
    var person_ids: String = ""
    var trying: Int = 0

    fun toMessage(): Message {
        val m = Message(
                id,
                message!!,
                read,
                registration_date,
                received,
                receiver_name,
                sent,
                subject,
                transmitter_name
        )

        return m
    }

    fun toMessageCOM(): MessageCOM {
        val m = MessageCOM(
                id,
                message!!,
                read,
                registration_date,
                received,
                receiver_name,
                sent,
                subject,
                transmitter_name
        )

        return m
    }
}

class MessageCOM(
        var id_Mensaje: Int,
        var Mensaje: String,
        var Leido: Boolean,
        var Fecha_Registro: String,
        var acusado: Boolean,
        var receiver_name: String?,
        var Enviado: Boolean,
        var Tema: String,
        var transmitter_name: String?,
        var header: Boolean = false
) : Serializable {

    fun messageToDb(mailbox: String): MessageDB {
        val m = MessageDB()

        m.id = id_Mensaje
        m.message = Mensaje
        m.read = Leido
        m.registration_date = Fecha_Registro
        m.received = acusado
        m.receiver_name = receiver_name
        m.sent = Enviado
        m.subject = Tema
        m.transmitter_name = transmitter_name
        m.mailbox = mailbox

        return m
    }
}