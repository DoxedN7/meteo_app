package com.doxed.meteo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxed.meteo.Common.Common;
import com.doxed.meteo.Model.WeatherResult;
import com.doxed.meteo.Retrofit.IOpenWeatherMap;
import com.doxed.meteo.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CityFragment extends Fragment {

    private List<String> lstCities;             //Liste des villes
    private MaterialSearchBar searchBar;        //Barre de recherche

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

    static CityFragment instance;       //Instance

    public static CityFragment getInstance() {
        if(instance==null)
            instance = new CityFragment();
        return instance;
    }

    public CityFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CityFragment.
     */

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: Rename and change types and number of parameters
    public static CityFragment newInstance(String param1, String param2) {
        CityFragment fragment = new CityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_city, container, false);

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
        searchBar = (MaterialSearchBar)itemView.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);

        //Chargement de la liste des villes
        new LoadCities().execute();

        return itemView;
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {
        @Override
        protected List<String> doInBackgroundSimple() {
            lstCities = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(is);
                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);

                String readed;
                while((readed = in.readLine()) != null)
                    builder.append(readed);
                lstCities = new Gson().fromJson(builder.toString(),new TypeToken<List<String>>(){}.getType());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return lstCities;
        }

        @Override
        protected void onSuccess(final List<String> listCity){
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for(String search : listCity){
                        if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());

                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);
            loading.setVisibility(View.GONE);
        }
    }

    private void getWeatherInformation(String cityName) {
        compositeDisposable.add(mService.getWeatherByCityName(cityName,
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

    @Override
    public void onDestroy(){
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
