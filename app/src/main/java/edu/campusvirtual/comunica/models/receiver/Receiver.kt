package edu.campusvirtual.comunica.models.receiver

import io.realm.RealmObject

/**
 * Created by jonathan on 2/27/18.
 */
class Receiver(var name: String, var distribution_list_id: Int, var person_id: Int) {
    fun toDB(): ReceiverDB {
        val db = ReceiverDB()

        db.name = name
        db.distribution_list_id = distribution_list_id
        db.person_id = person_id

        return db
    }
}

class ReceiverCOM(var id_Lista_Distribucion: Int, var Nombre: String) {
    fun toDB(): ReceiverDB {
        val db = ReceiverDB()

        db.name = Nombre
        db.distribution_list_id = id_Lista_Distribucion
        db.person_id = null

        return db
    }
}

open class ReceiverDB: RealmObject() {
    var name: String? = null
    var distribution_list_id: Int? = null
    var person_id: Int? = null

    fun toModel() : Receiver {
        val model = Receiver(name!!, distribution_list_id!!, person_id!!)

        return model
    }

    fun toModelCOM() : ReceiverCOM {
        val model = ReceiverCOM(distribution_list_id!!, name!!)

        return model
    }
}