package com.openclassrooms.realestatemanager.ui.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstateRoomDatabase
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.domain.RealEstateApplication
import com.openclassrooms.realestatemanager.domain.repository.EstateImageRepository
import com.openclassrooms.realestatemanager.domain.repository.EstatePlaceRepository
import com.openclassrooms.realestatemanager.domain.repository.EstateRepository
import com.openclassrooms.realestatemanager.ui.detail.EstateDetailFragment
import com.openclassrooms.realestatemanager.ui.form.EstateFormActivity
import com.openclassrooms.realestatemanager.ui.viewmodel.*
import com.openclassrooms.realestatemanager.utils.ChipItem
import com.openclassrooms.realestatemanager.utils.SearchItem
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.tools.*
import org.koin.android.ext.android.inject
import java.io.Serializable
import java.util.*

class EstateListActivity : AppCompatActivity() {
    private var twoPane: Boolean = false
    private val estates: MutableList<EstateModel> = mutableListOf()
    private val searchItem: SearchItem = SearchItem()

    private val internetManager: InternetManager by inject()

    private val clock: Clock by lazy {
        ClockImpl()
    }
    private val loan: Loan by lazy {
        LoanImpl()
    }
    private val converter: Converter by lazy {
        ConverterImpl()
    }

    private val estateViewModel: EstateViewModel by viewModels {
        EstateViewModelFactory(
            (application as RealEstateApplication).estateRepository,
            (application as RealEstateApplication).estateImageRepository,
            (application as RealEstateApplication).estatePlaceRepository
        )
    }
    private val placeViewModel: PlaceViewModel by viewModels {
        PlaceViewModelFactory((application as RealEstateApplication).placeRepository)
    }
    private val statusViewModel: StatusViewModel by viewModels {
        StatusViewModelFactory((application as RealEstateApplication).statusRepository)
    }
    private val agentViewModel: AgentViewModel by viewModels {
        AgentViewModelFactory((application as RealEstateApplication).agentRepository)
    }
    private val typeViewModel: TypeViewModel by viewModels {
        TypeViewModelFactory((application as RealEstateApplication).typeRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estate_list)

        if (findViewById<RelativeLayout>(R.id.estate_detail_container) != null) {
            twoPane = true

            findViewById<FrameLayout>(R.id.estate_detail_container).visibility = View.GONE
            findViewById<TextView>(R.id.estate_detail_container_no_result).visibility = View.VISIBLE
        }

        estateViewModel.allEstates.observe(this, {
            estates.clear()
            estates.addAll(it)
            setupRecyclerView(findViewById(R.id.estate_list), getFilteredEstateModel(estates), true)
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
                R.id.btn_converter -> {
                    convertAlert()
                }
                R.id.btn_today_date -> {
                    Toast.makeText(this, getString(R.string.today_date)+" : " + clock.getCurrentDate(), Toast.LENGTH_LONG).show()
                }
                R.id.btn_internet -> {
                    if (internetManager.isConnected()) {
                        Toast.makeText(this, getString(R.string.internet_connected), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, getString(R.string.internet_disconnected), Toast.LENGTH_LONG).show()
                    }
                }
                R.id.btn_mortgage_loan -> {
                    mortgageLoanAlert()
                }
            }

            true
        }

        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            menuItem.isChecked = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            when(menuItem.itemId) {
                R.id.btn_maps -> {
                    if (internetManager.isConnected()) {
                        val intent = Intent(this, MapActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, getString(R.string.internet_disconnected), Toast.LENGTH_LONG).show()
                    }
                    true
                }
                R.id.btn_filter -> {
                    filterAlert()
                    true
                }
                else -> false
            }
        }

        /*
        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Utils.saveEstate(result.data,this,estateViewModel,estateImageViewModel,estatePlaceViewModel)
            }
        }*/

        val btnAddEstate = findViewById<FloatingActionButton>(R.id.button_add_estate)
        btnAddEstate.setOnClickListener {
            val intent = Intent(this, EstateFormActivity::class.java)
            startActivityForResult(intent,Utils.FORM_ACTIVITY_REQUEST)
            //startForResult.launch(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bottom_app_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Utils.FORM_ACTIVITY_REQUEST && resultCode == Activity.RESULT_OK) {
            estateViewModel.estateId.observe(this, {
                if (it > 0) {
                    // Toast when the estate is saved
                    Toast.makeText(this, R.string.form_save, Toast.LENGTH_LONG).show()

                    if (twoPane) {
                        estateViewModel.getEstateById(it).observe(this, { estateModel ->
                            val fragment = EstateDetailFragment().apply {
                                arguments = Bundle().apply {
                                    putSerializable(Utils.EXTRA_ESTATE_MODEL, estateModel as Serializable)
                                }
                            }
                            this.supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.estate_detail_container, fragment)
                                .commit()
                        })
                    }
                }
            })
            estateViewModel.saveEstate(data)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, estatesModel: List<EstateModel>, checkVisibility: Boolean = false) {
        recyclerView.adapter = EstateListAdapter(this, estatesModel, twoPane)
        if(estatesModel.isNotEmpty()) {
            findViewById<RecyclerView>(R.id.estate_list).visibility = View.VISIBLE
            findViewById<TextView>(R.id.estate_list_no_result).visibility = View.GONE
        } else {
            findViewById<RecyclerView>(R.id.estate_list).visibility = View.GONE
            findViewById<TextView>(R.id.estate_list_no_result).visibility = View.VISIBLE
        }

        if (twoPane) {
            var ok = true
            if(checkVisibility) {
                if (findViewById<FrameLayout>(R.id.estate_detail_container).visibility == View.VISIBLE) {
                    ok = false
                }
            }

            if (ok) {
                findViewById<FrameLayout>(R.id.estate_detail_container).visibility = View.GONE
                findViewById<TextView>(R.id.estate_detail_container_no_result).visibility = View.VISIBLE
            }
        }
    }

    private fun getFilteredEstateModel(estatesModel: List<EstateModel>): List<EstateModel> {
        val filterEstates: MutableList<EstateModel> = mutableListOf()
        estatesModel.forEach { estate ->
            var ok = true

            if (searchItem.status.isNotEmpty()) {
                var hasStatus = false
                searchItem.status.forEach {
                    if (estate.status.id.toInt() == it) {
                        hasStatus = true
                    }
                }

                if (!hasStatus) {
                    ok = false
                }
            }
            if (searchItem.types.isNotEmpty()) {
                var hasType = false
                searchItem.types.forEach {
                    if (estate.type.id.toInt() == it) {
                        hasType = true
                    }
                }

                if (!hasType) {
                    ok = false
                }
            }
            if (searchItem.agents.isNotEmpty()) {
                var hasAgent = false
                searchItem.agents.forEach {
                    if (estate.agent.id.toInt() == it) {
                        hasAgent = true
                    }
                }

                if (!hasAgent) {
                    ok = false
                }
            }
            if (searchItem.places.isNotEmpty()) {
                if (estate.estatePlaces.isNotEmpty()) {
                    searchItem.places.forEach {
                        var hasPlace = false
                        estate.estatePlaces.forEach { estatePlace ->
                            if (estatePlace.placeId.toInt() == it) {
                                hasPlace = true
                            }
                        }

                        if (!hasPlace) {
                            ok = false
                        }
                    }
                } else {
                    ok = false
                }
            }
            if (searchItem.price != 0.0) {
                if (estate.estate.price.toDouble() != searchItem.price) {
                    ok = false
                }
            }
            if (searchItem.surface != 0.0) {
                if (estate.estate.surface.toDouble() != searchItem.surface) {
                    ok = false
                }
            }
            if (searchItem.city.isNotEmpty()) {
                if (estate.estate.city.toLowerCase(Locale.getDefault()) != searchItem.city.toLowerCase(
                        Locale.getDefault())) {
                    ok = false
                }
            }

            if (ok) {
                filterEstates.add(estate)
            }
        }

        return filterEstates
    }

    private fun convertAlert() {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        val dialogConvert = LayoutInflater.from(this).inflate(R.layout.dialog_convert,null,false)

        val convertAmount = dialogConvert.findViewById<TextInputEditText>(R.id.dialog_text_input_edit_convert_amount)
        val radioButtonEuro = dialogConvert.findViewById<RadioButton>(R.id.dialog_radio_button_euro)
        val radioButtonDollar = dialogConvert.findViewById<RadioButton>(R.id.dialog_radio_button_dollar)

        materialAlertDialogBuilder.setView(dialogConvert)
            .setTitle(getString(R.string.converter))
            .setPositiveButton(getString(R.string.validate)) { _, _ ->
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = materialAlertDialogBuilder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (convertAmount.text?.isNotEmpty() == true) {
                var amount: Double
                var text = ""

                if (radioButtonEuro.isChecked) {
                    amount = converter.convertEuroToDollar(convertAmount.text.toString().toDouble())
                    text = convertAmount.text.toString()+"€ = "+amount+"$"
                }
                if (radioButtonDollar.isChecked) {
                    amount = converter.convertDollarToEuro(convertAmount.text.toString().toDouble())
                    text = convertAmount.text.toString()+"$ = "+amount+"€"
                }

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Conversion de devises")
                builder.setMessage(text)
                builder.show()
            } else {
                Toast.makeText(this, getString(R.string.form_error), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mortgageLoanAlert() {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        val dialogMortgageLoan = LayoutInflater.from(this).inflate(R.layout.dialog_mortgage_loan,null,false)

        val loanAmount = dialogMortgageLoan.findViewById<TextInputEditText>(R.id.dialog_text_input_edit_loan_amount)
        val interestRate = dialogMortgageLoan.findViewById<TextInputEditText>(R.id.dialog_text_input_edit_interest_rate)
        val loanDuration = dialogMortgageLoan.findViewById<TextInputEditText>(R.id.dialog_text_input_edit_loan_duration)

        materialAlertDialogBuilder.setView(dialogMortgageLoan)
            .setTitle(getString(R.string.mortgage_loan))
            .setMessage(getString(R.string.monthly_payment))
            .setPositiveButton(getString(R.string.validate)) { _, _ -> }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = materialAlertDialogBuilder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (loanAmount.text?.isNotEmpty() == true && interestRate.text?.isNotEmpty() == true && loanDuration.text?.isNotEmpty() == true) {
                loan.setLoanAmount(loanAmount.text.toString().toDouble())
                loan.setInterestRate(interestRate.text.toString().toDouble())
                loan.setLoanDuration(loanDuration.text.toString().toInt())

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.mortgage_loan))
                builder.setMessage(getString(R.string.amount_monthly_payment)+loan.getMonthlyPayment())
                builder.show()
            } else {
                Toast.makeText(this, getString(R.string.form_error), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun filterAlert() {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        val dialogFilter = LayoutInflater.from(this).inflate(R.layout.dialog_filter,null,false)

        val price = dialogFilter.findViewById<TextInputEditText>(R.id.dialog_text_input_edit_price)
        val surface = dialogFilter.findViewById<TextInputEditText>(R.id.dialog_text_input_edit_surface)
        val city = dialogFilter.findViewById<TextInputEditText>(R.id.dialog_text_input_edit_city)

        if (searchItem.price != 0.0) {
            price.setText(searchItem.price.toString())
        }
        if (searchItem.surface != 0.0) {
            surface.setText(searchItem.surface.toString())
        }
        city.setText(searchItem.city)

        // Set chips for the status
        val statusChipGroup = dialogFilter.findViewById<ChipGroup>(R.id.dialog_chip_group_status)
        statusViewModel.allStatus.observe(this, { allStatus ->
            allStatus.forEach { status ->
                var isChecked = false
                if (searchItem.status.contains(status.id.toInt())) {
                    isChecked = true
                }

                val chipItem = ChipItem(status.id.toInt(), status.name,0)
                Utils.addChip(this,statusChipGroup,chipItem,false,isChecked)
            }
        })

        // Set chips for the agent
        val agentChipGroup = dialogFilter.findViewById<ChipGroup>(R.id.dialog_chip_group_agent)
        agentViewModel.allAgents.observe(this, { allAgents ->
            allAgents.forEach { agent ->
                var isChecked = false
                if (searchItem.agents.contains(agent.id.toInt())) {
                    isChecked = true
                }

                val chipItem = ChipItem(agent.id.toInt(), agent.fullName,0)
                Utils.addChip(this,agentChipGroup,chipItem,false,isChecked)
            }
        })

        // Set chips for the type
        val typeChipGroup = dialogFilter.findViewById<ChipGroup>(R.id.dialog_chip_group_type)
        typeViewModel.allTypes.observe(this, { allTypes ->
            allTypes.forEach { type ->
                var isChecked = false
                if (searchItem.types.contains(type.id.toInt())) {
                    isChecked = true
                }

                val chipItem = ChipItem(type.id.toInt(), type.name,0)
                Utils.addChip(this,typeChipGroup,chipItem,false,isChecked)
            }
        })

        // Set chips for the place
        val placeChipGroup = dialogFilter.findViewById<ChipGroup>(R.id.dialog_chip_group_place)
        placeViewModel.allPlaces.observe(this, { allPlaces ->
            allPlaces.forEach { place ->
                var isChecked = false
                if (searchItem.places.contains(place.id.toInt())) {
                    isChecked = true
                }

                val chipItem = ChipItem(place.id.toInt(), place.name,place.logo)
                Utils.addChip(this,placeChipGroup,chipItem,false,isChecked)
            }
        })

        materialAlertDialogBuilder.setView(dialogFilter)
            .setTitle(getString(R.string.estate_filter))
            .setPositiveButton(getString(R.string.validate)) { _, _ -> }
            .setNeutralButton(getString(R.string.erase)) { _, _ -> }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = materialAlertDialogBuilder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            searchItem.status = statusChipGroup.checkedChipIds
            searchItem.types = typeChipGroup.checkedChipIds
            searchItem.agents = agentChipGroup.checkedChipIds
            searchItem.places = placeChipGroup.checkedChipIds

            searchItem.price = 0.0
            if (price.text?.isNotEmpty() == true) {
                searchItem.price = price.text.toString().toDouble()
            }
            searchItem.surface = 0.0
            if (surface.text?.isNotEmpty() == true) {
                searchItem.surface = surface.text.toString().toDouble()
            }
            searchItem.city = ""
            if (city.text?.isNotEmpty() == true) {
                searchItem.city = city.text.toString()
            }

            setupRecyclerView(findViewById(R.id.estate_list), getFilteredEstateModel(estates))
            dialog.dismiss()
        }
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            statusChipGroup.clearCheck()
            agentChipGroup.clearCheck()
            typeChipGroup.clearCheck()
            placeChipGroup.clearCheck()

            price.setText("")
            surface.setText("")
            city.setText("")
        }
    }
}
