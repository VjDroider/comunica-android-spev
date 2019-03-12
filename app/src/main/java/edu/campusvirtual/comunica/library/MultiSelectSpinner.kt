package edu.campusvirtual.comunica.library

import android.content.Context
import android.view.LayoutInflater
import android.content.DialogInterface
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import edu.campusvirtual.comunica.models.receiver.Receiver
import edu.campusvirtual.comunica.R
import java.util.*
import kotlin.collections.ArrayList


class MultiSelectSpinner : Spinner, DialogInterface.OnMultiChoiceClickListener {

    private var receivers = ArrayList<Receiver>()

    private val simpleAdapter: ArrayAdapter<Any>
    private var items: List<String>? = null // Stores original values
    private var itemsAtStart: String? = null // Stores original values as string
    private var ids: List<String>? = null // Stores original value ids
    private var defaultText: String? = null // Default text like "Select - One"
    private var titleText: String? = null // Dialogbox title text
    private var selectedValues: BooleanArray? = null // Stores selected values
    private var selectionAtStart: BooleanArray? = null // Original selected values
    private var c: Context? = null

    // stops appending "," before string.
    val selectedItemsAsString: String
        get() {
            val sb = StringBuilder()
            var isFirst = false
            for (i in items!!.indices) {
                if (selectedValues!![i]) {
                    if (isFirst) {
                        sb.append(", ")
                    }
                    isFirst = true
                    sb.append(items!![i])
                }
            }
            if (sb.length == 0) {
                sb.append(defaultText)
            }
            return sb.toString()
        }

    // stops appending "," before string.
    val selectedIdsAsString: String
        get() {
            val sb = StringBuilder()
            var isFirst = false
            for (i in ids!!.indices) {
                if (selectedValues!![i]) {
                    if (isFirst) {
                        sb.append(",")
                    }
                    isFirst = true
                    sb.append(ids!![i])
                }
            }
            if (sb.length == 0) {
                sb.append(defaultText)
            }
            return sb.toString()
        }

    constructor(context: Context) : super(context) {
        this.c = context
        simpleAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item)
        super.setAdapter(simpleAdapter)
    }

    constructor(arg0: Context, arg1: AttributeSet) : super(arg0, arg1) { // System required constructor
        this.c = arg0
        simpleAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item)
        super.setAdapter(simpleAdapter)
    }

    constructor(arg0: Context, arg1: AttributeSet, arg2: Int) : super(arg0, arg1, arg2) {
        this.c = arg0
        simpleAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item)
        super.setAdapter(simpleAdapter)
    }

    override fun onClick(dialogInterface: DialogInterface, which: Int, isSelected: Boolean) {
        if (selectedValues != null && selectedValues!!.size > 0) {
            selectedValues!![which] = isSelected
            simpleAdapter.clear()
            simpleAdapter.add(selectedItemsAsString)
        } else {
            throw IllegalStateException("Clicked on null object.")
        }
    }

    override fun performClick(): Boolean {
        val builder = AlertDialog.Builder(c!!)
        // val ItemsAsArray = arrayOfNulls<String>(items!!.size)
        builder.setMultiChoiceItems(items!!.toTypedArray(), selectedValues, this)
        itemsAtStart = selectedItemsAsString
        val inflater = getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog, null)
        builder.setTitle(titleText)
        builder.setView(view)
        val alertDialog = builder.show()
        init(view, alertDialog)
        return true
    }

    private fun init(view: View, alertDialog: AlertDialog) {
        val ok = view.findViewById<Button>(R.id.buttonOk)
        val cancel = view.findViewById<Button>(R.id.buttonCancel)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                for (i in items!!.indices) {
                    alertDialog.getListView().setItemChecked(i, true)
                    selectedValues!![i] = true
                    simpleAdapter.clear()
                    simpleAdapter.add(selectedItemsAsString)
                }
                if (selectedValues != null)
                    System.arraycopy(selectedValues!!, 0, selectionAtStart!!, 0, selectedValues!!.size)
            } else {
                for (i in items!!.indices) {
                    alertDialog.getListView().setItemChecked(i, false)
                    selectedValues!![i] = false
                }
                simpleAdapter.clear()
                simpleAdapter.add(selectedItemsAsString)
            }
        }

        ok.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (selectedValues != null)
                    System.arraycopy(selectedValues!!, 0, selectionAtStart!!, 0, selectedValues!!.size)
                alertDialog.dismiss()
            }
        })

        cancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                simpleAdapter.clear()
                if (items!!.size <= 1) {
                    simpleAdapter.add(defaultText)
                }
                simpleAdapter.add(itemsAtStart)
                if (selectedValues != null)
                    System.arraycopy(selectionAtStart!!, 0, selectedValues!!, 0, selectionAtStart!!.size)
                alertDialog.dismiss()
            }
        })
    }

    override fun setAdapter(adapter: SpinnerAdapter) {
        super.setAdapter(adapter)
        throw RuntimeException("setAdapter is not allowed." + " Instead call 'setItems()'.")
    }

    fun setReceivers(receivers: ArrayList<Receiver>) {
        this.receivers = receivers
    }

    fun setItems(items: List<String>, ids: List<String>, defaultText: String, titleText: String) {
        this.receivers.clear()
        this.items = items
        this.ids = ids
        this.defaultText = defaultText
        this.titleText = titleText
        selectedValues = BooleanArray(items.size)
        selectionAtStart = BooleanArray(items.size)
        simpleAdapter.clear()
        simpleAdapter.add(defaultText)
        Arrays.fill(selectedValues, false)
    }
}