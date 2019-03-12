package edu.campusvirtual.comunica.models.account

import java.io.Serializable

/**
 * Created by jonathan on 2/28/18.
 */
class Account(val id: Int, val name: String, val balance: Double, val person_id: Int, val charges: ArrayList<Charge>) : Serializable