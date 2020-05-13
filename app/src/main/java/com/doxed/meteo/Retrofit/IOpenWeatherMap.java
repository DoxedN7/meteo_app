/** CAGNON LILIAN
 * 12/05/2020
 * APPLICATION METEO
 * API : OpenWeatherMap
 */

package com.doxed.meteo.Retrofit;

//Bibliothèques
import com.doxed.meteo.Model.WeatherResult;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

//Interface pour effectuer une requête GET pour récupérer les informations
public interface IOpenWeatherMap {

    //Recherche par latitude et longitude
    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng(@Query("lat") String lat, @Query("lon") String lng,    //Latitude & Longitude
                                                 @Query("appid") String appid,                          //Identifiant
                                                 @Query("units") String unit);                          //Unités

    //Recherche par nom de ville
    @GET("weather")
    Observable<WeatherResult> getWeatherByCityName(@Query("q") String cityName,                             //Nom de la ville
                                               @Query("appid") String appid,                            //Identifiant
                                               @Query("units") String unit);                            //Unités


}
