package edu.campusvirtual.comunica.models.file

import org.simpleframework.xml.*

/**
 * Created by jonathan on 2/26/18.
 */
class File(val name: String, val size: Int, val url: String)

class FileCOM {

    @set:Element(name = "string", required = false) @get:Element(name = "string", required = false)
    var value: String = ""

}