/** CAGNON LILIAN
 * 12/05/2020
 * APPLICATION METEO
 * API : OpenWeatherMap
 */

package com.doxed.meteo.Retrofit;

//Bibliothèques
import com.doxed.meteo.Model.WeatherResult;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

//Interface pour effectuer une requête GET à partir de la latitude, la longitude, l'id de mon application chez OpenWeatherMap et les unités que je désire utiliser
public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng(@Query("lat") String lat, @Query("lon") String lng,    //Latitude & Longitude
                                                 @Query("appid") String appid,                          //Identifiant
                                                 @Query("units") String unit);                          //Unités
}
