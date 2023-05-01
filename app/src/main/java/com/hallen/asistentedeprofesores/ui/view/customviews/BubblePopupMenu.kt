package com.hallen.asistentedeprofesores.ui.view.customviews

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.hallen.asistentedeprofesores.R

class BubblePopupMenu(
    context: Context,
    private val anchorView: View,
    private val onClickListener: PopupMenuCustomOnClickListener
) {
    private val popupWindow: PopupWindow
    private val popupView: View

    fun show(){
        popupWindow.showAsDropDown(anchorView)
    }

    interface PopupMenuCustomOnClickListener {
        fun onClick(index: Int, view: View)
    }

    init {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.bubble_menu_design,null)
        val container: LinearLayout = popupView.findViewById(R.id.bubble_container)
        popupView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow = PopupWindow(popupView, popupView.measuredWidth, popupView.measuredHeight, true)
        popupWindow.elevation = 10f
        for (i in 0 until container.childCount) {
            val v: View = container.getChildAt(i)
            v.setOnClickListener { v1 ->
                onClickListener.onClick(i, v1)
                popupWindow.dismiss()
            }
        }
    }

}