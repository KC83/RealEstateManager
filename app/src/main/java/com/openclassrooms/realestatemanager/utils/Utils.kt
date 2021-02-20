package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.repository.RealEstateApplication
import com.openclassrooms.realestatemanager.dummy.DropdownItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.util.*
import kotlin.collections.HashMap

class Utils {
    companion object {
        fun getStatus(): List<DropdownItem> {

            /*val list: List<DropdownItem> = Arrays.asList()
            val allStatus: Flow<List<Status>> = RealEstateApplication().StatusRepository.allStatus
            allStatus.map {
                for (status in it) {
                    list.toMutableList().add(DropdownItem(status.name))
                }
            }

            return list
*/
            return Arrays.asList(
                    DropdownItem("A vendre"),
                    DropdownItem("Vendu")
            )
        }

        fun getAgent(): List<DropdownItem> {
            return Arrays.asList(
                    DropdownItem("Kelly CHIAROTTI"),
                    DropdownItem("Marie MARTIN"),
                    DropdownItem("Jean LEBLANC")
            )
        }

        fun getType(): List<DropdownItem> {
            return Arrays.asList(
                    DropdownItem("Maison"),
                    DropdownItem("Villa"),
                    DropdownItem("Appartement"),
                    DropdownItem("Loft"),
                    DropdownItem("Manoir"),
                    DropdownItem("Terrain")
            )
        }
    }
}