package com.openclassrooms.realestatemanager.ui.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.ui.detail.EstateDetailActivity
import com.openclassrooms.realestatemanager.ui.detail.EstateDetailFragment
import com.openclassrooms.realestatemanager.ui.viewmodel.AgentViewModel
import com.openclassrooms.realestatemanager.ui.viewmodel.EstateImageViewModel
import com.openclassrooms.realestatemanager.ui.viewmodel.StatusViewModel
import com.openclassrooms.realestatemanager.ui.viewmodel.TypeViewModel
import com.openclassrooms.realestatemanager.utils.Utils
import java.io.Serializable

class EstateListAdapter(private val parentActivity: EstateListActivity,
                        private val values: List<EstateModel>,
                        private val twoPane: Boolean) :
    RecyclerView.Adapter<EstateListAdapter.ViewHolder>() {
        private val onClickListener: View.OnClickListener = View.OnClickListener { v ->
            val item = v.tag as EstateModel

            if (twoPane) {
                val fragment = EstateDetailFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(Utils.EXTRA_ESTATE_MODEL, item as Serializable)
                    }
                }
                parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.estate_detail_container, fragment)
                        .commit()
            } else {
                val intent = Intent(v.context, EstateDetailActivity::class.java).apply {
                    putExtra(Utils.EXTRA_ESTATE_MODEL, item as Serializable)
                }
                v.context.startActivity(intent)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.estate_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        if (item.images.isNotEmpty()) {
            val uri: Uri = Uri.parse(item.images[0].uri)
            holder.imageView.setImageURI(uri)
        }
        holder.nameView.text = item.type.name
        holder.cityView.text = item.estate.city
        holder.priceView.text = String.format("$%.2f", item.estate.price)

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val nameView: TextView = view.findViewById(R.id.item_name)
        val cityView: TextView = view.findViewById(R.id.item_city)
        val priceView: TextView = view.findViewById(R.id.item_price)
    }
}