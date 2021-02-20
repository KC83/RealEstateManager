package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.openclassrooms.realestatemanager.dummy.DropdownItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Philippe on 21/02/2018.
 */

public class OldUtils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * 0.812);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
    public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date());
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    public static Boolean isInternetAvailable(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    /*public static List<DropdownItem> getStatus() {


        return Arrays.asList(
                new DropdownItem("A vendre"),
                new DropdownItem("Vendu")
        );
    }
    public static List<DropdownItem> getAgent() {
        return Arrays.asList(
                new DropdownItem("Kelly CHIAROTTI"),
                new DropdownItem("Marie MARTIN"),
                new DropdownItem("Jean LEBLANC")
        );
    }
    public static List<DropdownItem> getType() {
        return Arrays.asList(
                new DropdownItem("Maison"),
                new DropdownItem("Villa"),
                new DropdownItem("Appartement"),
                new DropdownItem("Loft"),
                new DropdownItem("Manoir"),
                new DropdownItem("Terrain")
        );
    }*/
}
