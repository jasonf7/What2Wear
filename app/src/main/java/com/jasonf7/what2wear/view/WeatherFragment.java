package com.jasonf7.what2wear.view;


import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonf7.what2wear.R;
import com.jasonf7.what2wear.http.weather.WeatherHttp;
import com.jasonf7.what2wear.http.weather.WeatherResponse;

import android.os.Handler;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LOC_LATITUDE = "location_latitude";
    private static final String ARG_LOC_LONGITUDE = "location_longitude";

    // TODO: Rename and change types of parameters
    private double locLatitude;
    private double locLongitude;

    private Handler handler;
    private Typeface weatherFont;
    private WeatherResponse weatherInfo;

    private TextView locationText;
    private TextView lastUpdateText;
    private TextView weatherIconText;
    private TextView temperatureText;
    private TextView detailsText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(double param1, double param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LOC_LATITUDE, param1);
        args.putDouble(ARG_LOC_LONGITUDE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WeatherFragment() {
        // Required empty public constructor
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locLatitude = getArguments().getDouble(ARG_LOC_LATITUDE);
            locLongitude = getArguments().getDouble(ARG_LOC_LONGITUDE);
        }

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");

        if(locLatitude > 0 && locLongitude > 0){
            getWeatherData(String.format("%.2f", locLatitude), String.format("%.2f", locLongitude));
        } else {
            final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Log.d("DEBUG", "Poll for location");
            final LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.d("DEBUG", "Got location");
                    locLongitude = location.getLongitude();
                    locLatitude = location.getLatitude();
                    getWeatherData(String.format("%.2f", locLatitude), String.format("%.2f", locLongitude));
                    lm.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View weatherView = inflater.inflate(R.layout.fragment_weather, container, false);

        locationText = (TextView)weatherView.findViewById(R.id.locationText);

        lastUpdateText = (TextView)weatherView.findViewById(R.id.lastUpdateText);

        weatherIconText = (TextView)weatherView.findViewById(R.id.weatherIconText);
        weatherIconText.setTypeface(weatherFont);

        temperatureText = (TextView)weatherView.findViewById(R.id.temperatureText);

        detailsText = (TextView)weatherView.findViewById(R.id.detailsText);

        return weatherView;
    }

    private void getWeatherData(final String lat, final String lon){
//        new Thread(){
//            public void run(){
//                URL url = null;
//                try {
//                    url = new URL(String.format(WeatherHttp.OPEN_WEATHER_MAP_API_COORD, lat, lon));
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                weatherInfo = WeatherHttp.getJSON(getActivity(), url);
//                if(weatherInfo == null){
//                    handler.post(new Runnable(){
//                        public void run(){
//                            Toast.makeText(getActivity(),"Weather for (" + lat + "," + lon + ") is unavailable.",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    });
//                } else {
//                    handler.post(new Runnable(){
//                        public void run(){
//                            Log.d("DEBUG", "Weather info retrieved!");
//                            updateWeatherUI(weatherInfo);
//                        }
//                    });
//                }
//            }
//        }.start();

        new Thread(){
            public void run(){
                URL url = null;
                try {
                    url = new URL(String.format(WeatherHttp.OPEN_WEATHER_MAP_API_COORD, lat, lon));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                weatherInfo = WeatherHttp.getJSON(getActivity(), url);
                if(weatherInfo == null){
                    Toast.makeText(getActivity(),"Weather for (" + lat + "," + lon + ") is unavailable.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.d("DEBUG", "Weather info retrieved!");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateWeatherUI(weatherInfo);
                        }
                    });

                }
            }
        }.start();
    }

    private void getWeatherData(final String city){
        new Thread(){
            public void run(){
                URL url = null;
                try {
                    url = new URL(String.format(WeatherHttp.OPEN_WEATHER_MAP_API_CITY, city));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                weatherInfo = WeatherHttp.getJSON(getActivity(), url);
                if(weatherInfo == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),"Weather for " + city + " is unavailable.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            Log.d("DEBUG", "Weather info retrieved!");
                            updateWeatherUI(weatherInfo);
                        }
                    });
                }
            }
        }.start();
    }

    private void updateWeatherUI(WeatherResponse weather){
        Log.d("DEBUG", weather.name);
        Log.d("DEBUG", weather.sys.country);
        Log.d("DEBUG", String.format("%.2f",weather.main.temp));
        Log.d("DEBUG", String.format("%.2f",weather.main.humidity));
        Log.d("DEBUG", String.format("%.2f",weather.main.pressure));
        Log.d("DEBUG", weather.weather.get(0).description);
        Log.d("DEBUG", weather.dt + "");

        locationText.setText(weather.name + ", " + weather.sys.country);

        DateFormat df = DateFormat.getDateTimeInstance();
        String updatedOn = df.format(new Date(weather.dt*1000));
        lastUpdateText.setText("Last update: " + updatedOn);

        setWeatherIcon((int) weather.weather.get(0).id, weather.sys.sunrise, weather.sys.sunset);

        temperatureText.setText(String.format("%.2f", weather.main.temp) + "\u00b0 C");

        detailsText.setText(weather.weather.get(0).description + "\n"
                            + "Humidity: " + String.format("%.2f",weather.main.humidity)+ "%" + "\n"
                            + "Pressure: " + String.format("%.2f",weather.main.pressure)+ "hPa");
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIconText.setText(icon);
    }
}
