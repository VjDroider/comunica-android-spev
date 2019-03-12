package edu.campusvirtual.comunica.models.kardex

/**
 * Created by jonathan on 2/28/18.
 */
class Kardex(var id: Int, var study_plan: String, var student: String, var average: Double, val grades: ArrayList<Grade>)