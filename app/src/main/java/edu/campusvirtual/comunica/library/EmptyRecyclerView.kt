package edu.campusvirtual.comunica.library

import android.R
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by jonathan on 3/8/18.
 */
class EmptyRecyclerView: RecyclerView {

    private var emptyView: View? = null

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

    constructor(context: Context) : super(context) {
    }

    constructor(arg0: Context, arg1: AttributeSet) : super(arg0, arg1) { // System required constructor
    }

    constructor(arg0: Context, arg1: AttributeSet, arg2: Int) : super(arg0, arg1, arg2) {
    }

    fun checkIfEmpty() {
        Log.d("SIII","comeon" )
        if (emptyView != null && adapter != null) {
            Log.d("SIII","yesybesyes " + adapter!!.itemCount )
            val emptyViewVisible = adapter!!.itemCount == 0
            emptyView!!.setVisibility(if (emptyViewVisible) View.VISIBLE else View.GONE)
            visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)

        checkIfEmpty()
    }

    fun setEmptyView(emptyView: View) {
        this.emptyView = emptyView
        checkIfEmpty()
    }
}