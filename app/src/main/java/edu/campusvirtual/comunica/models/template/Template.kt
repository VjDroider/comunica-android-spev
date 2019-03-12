package edu.campusvirtual.comunica.models.template

import io.realm.RealmObject
import java.io.Serializable

class Template(val description: String, val title: String, val html: String) : Serializable {


    fun toDb(): TemplateDB {
        val db = TemplateDB()

        db.html = html
        db.description = description
        db.title = title

        return db
    }

}

open class TemplateDB() : RealmObject() {
    var description: String? = null
    var title: String? = null
    var html: String? = null

    fun toModel() : Template {
        val model = Template(description!!, title!!, html!!)

        return model
    }
}
