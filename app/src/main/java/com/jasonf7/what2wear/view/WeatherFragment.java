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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Handler handler;
    private Typeface weatherFont;
    private WeatherResponse weatherInfo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("DEBUG", "got location");
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                getWeatherData(String.format("%.2f", lat), String.format("%.2f", lon));
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
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        getWeatherData("Waterloo");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View weatherView = inflater.inflate(R.layout.fragment_weather, container, false);

        TextView weatherIconText = (TextView)weatherView.findViewById(R.id.weatherIconText);
        weatherIconText.setTypeface(weatherFont);

        return weatherView;
    }

    private void getWeatherData(final String lat, final String lon){
        new Thread(){
            public void run(){
                weatherInfo = WeatherHttp.getJSON(getActivity(), lat, lon);
                if(weatherInfo == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),"Weather for (" + lat + "," + lon + ") is unavailable.",
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

    private void getWeatherData(final String city){
        new Thread(){
            public void run(){
                weatherInfo = WeatherHttp.getJSON(getActivity(), city);
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
    }
}
