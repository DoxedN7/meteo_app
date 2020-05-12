/** CAGNON LILIAN
 * 12/05/2020
 * APPLICATION METEO
 * API : OpenWeatherMap
 */

package com.doxed.meteo;

//Bibliothèques
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.doxed.meteo.Common.Common;
import com.doxed.meteo.Model.WeatherResult;
import com.doxed.meteo.Retrofit.IOpenWeatherMap;
import com.doxed.meteo.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */

public class TodayWeatherFragment extends Fragment {

//Variables
    ImageView img_weather;                      //Image de la météo
    TextView txt_city_name,                     //Nom de la ville
            txt_humidity,                       //Humidité
            txt_sunrise,                        //Heure du levé du soleil
            txt_sunset,                         //Heure du couché du soleil
            txt_pressure,                       //Pression atmosphérique
            txt_temperature,                    //Température (celsius)
            txt_date_time,                      //Date
            txt_wind,                           //Vitesse du vent (m/s)
            txt_temp_min,                       //Température minimale (celsius)
            txt_temp_max,                       //Température maximale (celsius)
            txt_precipitation;                  //Précipitations (mm)

    LinearLayout weather_panel;                 //Affichage du panneau des informations météorologiques
    ProgressBar loading;                        //Icone de chargement
    CompositeDisposable compositeDisposable;    //Permet une meilleure gestion de ce qui sera supprimé
    IOpenWeatherMap mService;                   //Service IOpenWeatherMap

    static TodayWeatherFragment instance;       //Instance

    public static TodayWeatherFragment getInstance() {
        if(instance==null)
            instance = new TodayWeatherFragment();
        return instance;
    }

    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Création de la vue
        View itemView = inflater.inflate(R.layout.fragment_today_weather, container, false);

        //Liaison entre les ImageView/TextView de fragment_today_weather et les variables locales
        img_weather = (ImageView)itemView.findViewById(R.id.img_weather);
        txt_city_name = (TextView)itemView.findViewById(R.id.txt_city_name);
        txt_humidity = (TextView)itemView.findViewById(R.id.txt_humidity);
        txt_sunrise = (TextView)itemView.findViewById(R.id.txt_sunrise);
        txt_sunset = (TextView)itemView.findViewById(R.id.txt_sunset);
        txt_pressure = (TextView)itemView.findViewById(R.id.txt_pressure);
        txt_temperature = (TextView)itemView.findViewById(R.id.txt_temperature);
        txt_date_time = (TextView)itemView.findViewById(R.id.txt_date_time);
        txt_wind = (TextView)itemView.findViewById(R.id.txt_wind);
        txt_temp_min = (TextView)itemView.findViewById(R.id.txt_temp_min);
        txt_temp_max = (TextView)itemView.findViewById(R.id.txt_temp_max);
        txt_precipitation = (TextView)itemView.findViewById(R.id.txt_precipitation);
        weather_panel = (LinearLayout)itemView.findViewById(R.id.weather_panel);
        loading = (ProgressBar)itemView.findViewById(R.id.loading);

        //Récupération des informations météorologiques
        getWeatherInformation();

        //Affichage des informations
        return itemView;
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        //Chargement des informations
                            //Récupération de l'image de météo
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/").append(weatherResult.getWeather().get(0).getIcon())
                        .append(".png").toString()).into(img_weather);

                            //Récupérations des données de la ville, de la date et de la vitesse du vent
                        txt_city_name.setText(weatherResult.getName());
                        txt_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        txt_wind.setText(new StringBuilder(String.valueOf(Common.convertMsToKmh(weatherResult.getWind().getSpeed()))).append(" km/h").toString());

                            //Récupération des données de températures moyenne, minimale et maximale
                        txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                        txt_temp_min.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp_min())).append("°C").toString());
                        txt_temp_max.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp_max())).append("°C").toString());

                            //Récupération des données de pression, humidité et précipitations
                        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hPa").toString());
                        txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append("%").toString());
                        txt_precipitation.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPrecipitation())).append("mm").toString());

                            //Récupération des données d'heure de levé et de couché du soleil
                        txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        txt_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));


                        //Affichage du Panneau et on retire l'icone de chargement
                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(),""+throwable.getMessage(),Toast.LENGTH_LONG).show();       //Si erreur
                    }
                })
        );
    }
}
