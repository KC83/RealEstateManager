package com.openclassrooms.realestatemanager.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.*
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
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.di.Injection
import com.openclassrooms.realestatemanager.domain.RealEstateApplication
import com.openclassrooms.realestatemanager.ui.form.EstateFormActivity
import com.openclassrooms.realestatemanager.ui.viewmodel.*
import com.openclassrooms.realestatemanager.utils.*
import com.openclassrooms.realestatemanager.utils.tools.Converter
import com.openclassrooms.realestatemanager.utils.tools.ConverterImpl
import okhttp3.internal.Util
import java.util.*

class EstateListActivity : AppCompatActivity() {
    private var twoPane: Boolean = false
    private val estates: MutableList<EstateModel> = mutableListOf()
    private val searchItem: SearchItem = SearchItem()

    private val clock: Clock by lazy {
        ClockImpl()
    }
    private val internetManager: InternetManager by lazy {
        Injection.provideInternetManager(this)
    }
    private val loan: Loan by lazy {
        LoanImpl()
    }
    private val converter: Converter by lazy {
        ConverterImpl()
    }

    private val estateViewModel: EstateViewModel by viewModels {
        EstateViewModelFactory((application as RealEstateApplication).estateRepository)
    }
    private val estateImageViewModel: EstateImageViewModel by viewModels {
        EstateImageViewModelFactory((application as RealEstateApplication).estateImageRepository)
    }
    private val estatePlaceViewModel: EstatePlaceViewModel by viewModels {
        EstatePlaceViewModelFactory((application as RealEstateApplication).estatePlaceRepository)
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
        }

        estateViewModel.allEstates.observe(this, {
            estates.clear()
            estates.addAll(it)
            setupRecyclerView(findViewById(R.id.estate_list), getFilteredEstateModel(estates))
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
                    Toast.makeText(this, "Aujourd'hui, nous sommes le " + clock.getCurrentDate(), Toast.LENGTH_LONG).show()
                }
                R.id.btn_internet -> {
                    if (internetManager.isConnected()) {
                        Toast.makeText(this, "Vous êtes connecté à internet !", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Vous n'êtes pas connecté à internet !", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this, "Vous n'êtes pas connecté à internet !", Toast.LENGTH_LONG).show()
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

        val btnAddEstate = findViewById<FloatingActionButton>(R.id.button_add_estate)
        btnAddEstate.setOnClickListener {
            val intent = Intent(this, EstateFormActivity::class.java)
            startActivityForResult(intent, Utils.FORM_ACTIVITY_REQUEST)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bottom_app_bar, menu)
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
                    if (idx > 0) {
                        estateImageViewModel.insert(EstateImage(estateId = estateId, uri = image.uri, name = image.name))
                    }
                    idx++
                }

                // Places
                val placeIds: MutableList<Int> = data.getSerializableExtra(Utils.EXTRA_PLACE) as MutableList<Int>
                placeIds.forEach { placeId ->
                    estatePlaceViewModel.insert(EstatePlace(estateId = estateId, placeId = placeId.toLong()))
                }
            })
            estateViewModel.insert(estate)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, estatesModel: List<EstateModel>) {
        recyclerView.adapter = EstateListAdapter(this, estatesModel, twoPane)
    }

    private fun getFilteredEstateModel(estatesModel: List<EstateModel>): List<EstateModel> {
        val filterEstates: MutableList<EstateModel> = mutableListOf()
        estatesModel.forEach { estate ->
            var ok = true

            if (searchItem.status.isNotEmpty()) {
                searchItem.status.forEach {
                    if (estate.status.id.toInt() != it) {
                        ok = false
                    }
                }
            }
            if (searchItem.types.isNotEmpty()) {
                searchItem.types.forEach {
                    if (estate.type.id.toInt() != it) {
                        ok = false
                    }
                }
            }
            if (searchItem.agents.isNotEmpty()) {
                searchItem.agents.forEach {
                    if (estate.agent.id.toInt() != it) {
                        ok = false
                    }
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
            .setTitle("Conversion de devises")
            .setPositiveButton("Valider") { dialog, _ ->
            }
            .setNegativeButton("Annuler") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = materialAlertDialogBuilder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (convertAmount.text?.isNotEmpty() == true) {
                var amount = 0.0
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
                Toast.makeText(this, "Merci de renseigner tous les éléments", Toast.LENGTH_LONG).show()
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
            .setTitle("Simulateur de prêt immobilier")
            .setMessage("Calcul des mensualités de votre prêt immobilier")
            .setPositiveButton("Valider") { dialog, _ -> }
            .setNegativeButton("Annuler") { dialog, _ ->
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
                builder.setTitle("Simulateur de prêt immobilier")
                builder.setMessage("Montant des mensualités : "+loan.getMonthlyPayment()+"/mois")
                builder.show()
            } else {
                Toast.makeText(this, "Merci de renseigner tous les éléments", Toast.LENGTH_LONG).show()
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
            .setTitle("Filtre des biens")
            .setPositiveButton("Valider") { dialog, _ -> }
            .setNeutralButton("Effacer") { dialog, _ -> }
            .setNegativeButton("Annuler") { dialog, _ ->
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
