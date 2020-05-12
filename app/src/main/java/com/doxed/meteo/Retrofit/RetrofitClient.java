/** CAGNON LILIAN
 * 12/05/2020
 * APPLICATION METEO
 * API : OpenWeatherMap
 */

package com.doxed.meteo.Retrofit;

//Bibliothèques
import retrofit2.Retrofit;                                              //Retrofit permet d'effectuer des requêtes HTTP
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//Client Retrofit
public class RetrofitClient {
    private static Retrofit instance;

    public static Retrofit getInstance(){
        if(instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")                //Lien de l'api d'OpenWeatherMap
                    .addConverterFactory(GsonConverterFactory.create())                 //Conversion de la requête au format GSON
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        return instance;
    }
}
