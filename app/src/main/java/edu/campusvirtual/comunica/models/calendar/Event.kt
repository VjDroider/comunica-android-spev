package edu.campusvirtual.comunica.models.calendar

import java.io.Serializable

/**
 * Created by jonathan on 3/1/18.
 */
class Event(
        val id: Int,
        val type: String,
        val title: String,
        val place: String,
        val description: String,
        val url: String,
        val notes: String,
        var section: String,
        val initial_date: String,
        val initial_time: String,
        val final_date: String,
        val final_time: String
) : Serializable

class EventCOM(
        val id_evento: Int,
        val type: String,
        val title: String,
        val description: String,
        val url: String,
        val notes: String,
        var seccion: String,
        val startdate: String,
        val enddate: String
) : Serializable
