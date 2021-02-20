package com.openclassrooms.realestatemanager.ui.form

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.dummy.DropdownItem

class DropdownAdapter(private val adapterContext: Context, private val values: List<DropdownItem>)
    : ArrayAdapter<DropdownItem>(adapterContext, 0, values) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var dropdownItemView = convertView

        if (dropdownItemView == null) {
            dropdownItemView = LayoutInflater.from(adapterContext).inflate(R.layout.dropdown_item, parent,false)
        }

        val value = values[position]

        val textView: TextView = dropdownItemView!!.findViewById(R.id.dropdown_item_name)
        textView.text = value.name

        return dropdownItemView
    }
}