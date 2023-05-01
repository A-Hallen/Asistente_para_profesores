package com.hallen.asistentedeprofesores.ui.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import com.hallen.asistentedeprofesores.R


class GraficSpinnerAdapter(mContext: Context, textViewResourceId: Int) :
    ArrayAdapter<Pair<Int, String>>(mContext, textViewResourceId, arrayListOf()) {
    val  values: ArrayList<Pair<Int, String>> = arrayListOf()

    override fun getCount(): Int = values.size

    override fun add(student: Pair<Int, String>?) {
        values.add(student!!)
    }

    override fun clear() {
        values.clear()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: CheckedTextView
        if (convertView == null) {
            view = LayoutInflater.from(parent.context).inflate(
                R.layout.spinner_item, parent, false) as CheckedTextView
            view.tag = view
        } else {
            view = convertView as CheckedTextView
        }
        view.text = values[position].second
        return view
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView as CheckedTextView?
        if (view == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false) as CheckedTextView
        }
        view.text = values[position].second
        return view
    }

}