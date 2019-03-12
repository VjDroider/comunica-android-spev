package edu.campusvirtual.comunica.fragments.configuration


import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import edu.campusvirtual.comunica.library.SessionManager

import edu.campusvirtual.comunica.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SignatureFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_signature, container, false)

        var session = SessionManager(context!!)
        var editText = v.findViewById<EditText>(R.id.editTextId)
        var button = v.findViewById<Button>(R.id.buttonSave)

        var currentSignature = session.onGet("signature")

        if(currentSignature == null) {
            session.onRegister("signature", session.getFullname()!!)
            editText.text = SpannableStringBuilder(session.getFullname())
        } else {
            editText.text = SpannableStringBuilder(currentSignature)
        }

        button.setOnClickListener {
            var signature = editText.text

            session.onRegister("signature", signature.toString())

        }

        return v
    }


}
