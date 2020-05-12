/** CAGNON LILIAN
 * 12/05/2020
 * APPLICATION METEO
 * API : OpenWeatherMap
 */

package com.doxed.meteo.Common;

//Bibliothèques
import android.location.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String APP_ID = "a1eab8e185d3d31db5ff5f44565b8587";         //Identifiant de cette application chez OpenWeatherMap
    public static Location current_location=null;                                   //Initialisation de la localisation

    //Conversion du format Unix vers le format date
    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMM yyyy");    //Date au format 'Lundi 1 Janvier 2000'
        String formatted = sdf.format(date);
        return formatted;
    }

    //Conversation du format Unix vers le format hour
    public static String convertUnixToHour(long dt){
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");               //Heure au format '08:55'
        String formatted = sdf.format(date);
        return formatted;
    }

    //Convversion des m/s en Km/h et utilisation de deux décimales
    public static double convertMsToKmh(double ms) {
        ms = ms * 3.6;
        BigDecimal kmh = new BigDecimal(Double.toString(ms));
        return kmh.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
