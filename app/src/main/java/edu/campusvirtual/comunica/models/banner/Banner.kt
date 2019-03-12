package edu.campusvirtual.comunica.models.banner

import java.io.Serializable

class Banner(var id: Int, var title: String, var url: String, var section: String) : Serializable

class BannerCOM(var id_banner: Int, var titulo: String, var imgsrc: String, var seccion: String) : Serializable