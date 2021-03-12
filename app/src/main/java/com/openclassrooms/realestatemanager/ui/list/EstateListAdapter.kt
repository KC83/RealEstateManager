package com.openclassrooms.realestatemanager.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.dummy.DummyContent
import com.openclassrooms.realestatemanager.ui.detail.EstateDetailActivity
import com.openclassrooms.realestatemanager.ui.detail.EstateDetailFragment
import com.openclassrooms.realestatemanager.ui.viewmodel.TypeViewModel
import kotlin.math.roundToInt

class EstateListAdapter(private val parentActivity: EstateListActivity, private val values: List<Estate>, private val twoPane: Boolean, private val typeViewModel: TypeViewModel) :
    RecyclerView.Adapter<EstateListAdapter.ViewHolder>() {
        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as Estate
                if (twoPane) {
                    val fragment = EstateDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(EstateDetailFragment.ARG_ITEM_ID, item.id.toString())
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.estate_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, EstateDetailActivity::class.java).apply {
                        putExtra(EstateDetailFragment.ARG_ITEM_ID, item.id.toString())
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.estate_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]

            holder.nameView.text = item.type
            holder.cityView.text = item.city
            holder.priceView.text = String.format("$%.2f", item.price)

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount(): Int {
            return values.size
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameView: TextView = view.findViewById(R.id.item_name)
            val cityView: TextView = view.findViewById(R.id.item_city)
            val priceView: TextView = view.findViewById(R.id.item_price)
        }
}