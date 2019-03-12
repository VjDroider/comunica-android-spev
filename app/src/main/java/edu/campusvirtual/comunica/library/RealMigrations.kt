package edu.campusvirtual.comunica.library

import io.realm.DynamicRealm
import io.realm.RealmMigration
import io.realm.RealmObjectSchema
import io.realm.RealmSchema



/**
 * Created by jonathan on 3/15/18.
 */
class RealMigrations: RealmMigration {
    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        // val schema = realm!!.getSchema()

        /*if (oldVersion == 1) {
            val userSchema = schema.get("UserData")
            userSchema!!.addField("age", Int::class.javaPrimitiveType)
        }*/
    }

    override fun hashCode(): Int {
        return 37
    }

    override fun equals(other: Any?): Boolean {
        return other is RealMigrations
    }
}