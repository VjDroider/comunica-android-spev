package edu.campusvirtual.comunica.models.kardex

import java.io.Serializable

/**
 * Created by jonathan on 2/28/18.
 */
class Grade(var id: Int, var subject: String, var value: Double, var minimum_toapprove: Double, var status: String) : Serializable