package com.openclassrooms.realestatemanager.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.dummy.DummyContent
import com.openclassrooms.realestatemanager.utils.Utils


class EstateDetailFragment : Fragment() {
    private var item: Estate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(Utils.EXTRA_ESTATE)) {
                item = it.getSerializable(Utils.EXTRA_ESTATE) as Estate
                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = item?.description
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.estate_detail, container, false)
        item?.let {
            rootView.findViewById<TextView>(R.id.card_description_text).text = it.location
        }

        return rootView
    }
}