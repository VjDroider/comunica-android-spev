package edu.campusvirtual.comunica.library

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by jonathan on 3/15/18.
 */
class RealmManager {

    var context: Context

    constructor(context: Context) {
        this.context = context
        Realm.init(context)
    }

    fun getInstance(name: String) : Realm {
        val config = RealmConfiguration.Builder()
                .name(name)
                .schemaVersion(1)
                .build()

        return Realm.getInstance(config)
    }

}