package com.openclassrooms.realestatemanager.ui.list

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.domain.repository.RealEstateApplication
import com.openclassrooms.realestatemanager.ui.form.EstateFormActivity
import com.openclassrooms.realestatemanager.ui.viewmodel.*
import com.openclassrooms.realestatemanager.utils.Utils
import com.squareup.picasso.Picasso
import okhttp3.internal.Util

class EstateListActivity : AppCompatActivity() {
    private var twoPane: Boolean = false

    private val estateViewModel: EstateViewModel by viewModels {
        val app = application as RealEstateApplication
        EstateViewModelFactory(app.estateRepository)
    }
    private val estateImageViewModel: EstateImageViewModel by viewModels {
        EstateImageViewModelFactory((application as RealEstateApplication).estateImageRepository)
    }
    private val estatePlaceViewModel: EstatePlaceViewModel by viewModels {
        EstatePlaceViewModelFactory((application as RealEstateApplication).estatePlaceRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estate_list)

        if (findViewById<NestedScrollView>(R.id.estate_detail_container) != null) {
            twoPane = true
        }

        estateViewModel.allEstates.observe(this, { estatesModel ->
            setupRecyclerView(findViewById(R.id.estate_list), estatesModel)
        })

        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottom_app_bar)
        setSupportActionBar(bottomAppBar)

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.itemIconTintList = null
        val bottomSheetBehavior = BottomSheetBehavior.from(navigationView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomAppBar.setNavigationOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.btn_convert_euros -> {
                    convertAlert()
                }
                R.id.btn_today_date -> {
                    Toast.makeText(this,"Aujourd'hui, nous sommes le "+Utils.getTodayDate(),Toast.LENGTH_LONG).show()
                }
                R.id.btn_internet -> {
                    if (Utils.isInternetAvailable(this)) {
                        Toast.makeText(this,"Vous êtes connecté à internet !",Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this,"Vous n'êtes pas connecté à internet !",Toast.LENGTH_LONG).show()
                    }
                }
            }

            true
        }

        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            menuItem.isChecked = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            when(menuItem.itemId) {
                R.id.button_maps -> {
                    if (Utils.isInternetAvailable(this)) {
                        val intent = Intent(this,MapActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this,"Vous n'êtes pas connecté à internet !",Toast.LENGTH_LONG).show()
                    }
                    true
                }
                else -> false
            }
        }

        val btnAddEstate = findViewById<FloatingActionButton>(R.id.button_add_estate)
        btnAddEstate.setOnClickListener {
            val intent = Intent(this, EstateFormActivity::class.java)
            startActivityForResult(intent, Utils.FORM_ACTIVITY_REQUEST)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bottom_app_bar,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Utils.FORM_ACTIVITY_REQUEST && resultCode == Activity.RESULT_OK) {
            val estate: Estate = data?.getSerializableExtra(Utils.EXTRA_ESTATE) as Estate

            estateViewModel.estateId.observe(this, { estateId ->
                // Images
                val images: MutableList<EstateImage> = data.getSerializableExtra(Utils.EXTRA_ESTATE_IMAGE) as MutableList<EstateImage>
                var idx = 0
                images.forEach { image ->
                    if(idx > 0) {
                        estateImageViewModel.insert(EstateImage(estateId = estateId, uri = image.uri, name = image.name))
                    }
                    idx++
                }

                // Places
                val placeIds: MutableList<Int> = data.getSerializableExtra(Utils.EXTRA_PLACE) as MutableList<Int>
                placeIds.forEach { placeId ->
                    estatePlaceViewModel.insert(EstatePlace(estateId = estateId, placeId = placeId.toLong()))
                }

                estateViewModel.allEstates.observe(this, { estatesModel ->
                    setupRecyclerView(findViewById(R.id.estate_list), estatesModel)
                })
            })
            estateViewModel.insert(estate)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, estates: List<EstateModel>) {
        recyclerView.adapter = EstateListAdapter(this, estates, twoPane)
    }
    private fun convertAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Conversion de l'Euro vers le Dollard")
        builder.setMessage("Merci de renseigner un montant")

        val editText = EditText(this)
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        editText.layoutParams = lp
        builder.setView(editText)

        builder.setPositiveButton("Ok",null)

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (editText.text.isNotEmpty()) {
                val text = editText.text.toString()+"€ = "+Utils.convertEuroToDollar(editText.text.toString().toDouble())+"$"
                Toast.makeText(this,text,Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Merci de renseigner un montant", Toast.LENGTH_LONG).show()
            }
        }
    }
}