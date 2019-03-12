package edu.campusvirtual.comunica.models.location

import io.realm.RealmObject
import edu.campusvirtual.comunica.models.inbox.Message

/**
 * Created by jonathan on 2/28/18.
 */
open class Location {

    var id: Int? = null
    var street: String? = null
    var neighboor: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var phone: String? = null
    var email: String? = null

    fun toDB(): GeofenceDB {
        var db = GeofenceDB()

        db.id = id
        db.street = street
        db.neighboor = neighboor
        db.latitude = latitude
        db.longitude = longitude
        db.phone = phone
        db.email = email

        return db
    }
}

class LocationCOM: Location() {
    var name: String? = null
    var radius: Int? = null
    var transitionType: Int? = null
}

class SingleLocationCOM {
    var latitude: Double? = null
    var longitude: Double? = null
}

open class GeofenceDB(): RealmObject() {
    var id: Int? = null
    var street: String? = null
    var name: String? = null
    var neighboor: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var phone: String? = null
    var email: String? = null

    fun toModel(): Location {
        var model = Location()

        model.id = id
        model.street = street
        model.neighboor = neighboor
        model.latitude = latitude
        model.longitude = longitude
        model.phone = phone
        model.email = email

        return model
    }

    fun toModelCOM(): LocationCOM {
        var model = LocationCOM()

        model.id = id
        model.street = street
        model.name = name
        model.neighboor = neighboor
        model.latitude = latitude
        model.longitude = longitude
        model.phone = phone
        model.email = email

        return model
    }

}