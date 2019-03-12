package edu.campusvirtual.comunica.models.attachment

import android.net.Uri
import java.io.Serializable

class Attachment(var url: String, var isVideo: Boolean, var preview: String, var isSelected: Boolean = false): Serializable