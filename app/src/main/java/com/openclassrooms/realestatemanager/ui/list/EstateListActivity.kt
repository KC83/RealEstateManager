package com.openclassrooms.realestatemanager.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstateRoomDatabase
import com.openclassrooms.realestatemanager.dummy.DummyContent
import com.openclassrooms.realestatemanager.ui.form.EstateFormActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EstateListActivity : AppCompatActivity() {
    private var twoPane: Boolean = false
    private val FORM_ACTIVITY_REQUEST = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estate_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        toolbar.title = title

        if (findViewById<NestedScrollView>(R.id.estate_detail_container) != null) {
            twoPane = true
        }

        setupRecyclerView(findViewById(R.id.estate_list))

        GlobalScope.launch {
            //RealEstateRoomDatabase.getDatabase(this@EstateListActivity, GlobalScope).agentDao().getAgents().observeForever {  }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem)= when (item.itemId) {
        R.id.action_add_estate -> {
            val intent = Intent(this, EstateFormActivity::class.java)
            startActivityForResult(intent, FORM_ACTIVITY_REQUEST)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FORM_ACTIVITY_REQUEST && resultCode == Activity.RESULT_OK) {
            val estate = intent.getSerializableExtra(EstateFormActivity.EXTRA_REPLY)
            /*data?.getStringExtra(EstateFormActivity.EXTRA_REPLY)?.let {

            }*/
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = EstateListAdapter(this, DummyContent.ITEMS, twoPane)
    }
}