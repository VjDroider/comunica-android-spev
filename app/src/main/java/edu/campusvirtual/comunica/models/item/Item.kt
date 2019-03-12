package edu.campusvirtual.comunica.models.item

import java.io.Serializable

class Item(val id: Int, var index: Int, var section: String, var type: String, var text: String, var content: String, var parent: Int, var children: ArrayList<Item>) : Serializable

class ItemCOM(val id_Pagina: Int, var indice: Int, var seccion: String, var Nombre_TipoItem: String, var menuItemTexto: String, var contenido: String, var Parent: Int) : Serializable
