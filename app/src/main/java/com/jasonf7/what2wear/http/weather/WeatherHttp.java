package com.jasonf7.what2wear.http.weather;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.jasonf7.what2wear.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jasonf7 on 12/05/15.
 */
public class WeatherHttp {

    private static final String OPEN_WEATHER_MAP_API_CITY = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    private static final String OPEN_WEATHER_MAP_API_COORD = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

    public static WeatherResponse getJSON(Context context, String city){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API_CITY, city));
            Log.d("DEBUG", url.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", context.getString(R.string.openweather_api_key));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

//            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            /*if(data.getInt("cod") != 200){
                return null;
            }*/

            Gson gson = new Gson();
            WeatherResponse response = gson.fromJson(json.toString(), WeatherResponse.class);

            return response;
        }catch(Exception e){
            return null;
        }
    }

    public static WeatherResponse getJSON(Context context, String lat, String lon){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API_COORD, lat, lon));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", context.getString(R.string.openweather_api_key));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

//            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            /*if(data.getInt("cod") != 200){
                return null;
            }*/

            Gson gson = new Gson();
            WeatherResponse response = gson.fromJson(json.toString(), WeatherResponse.class);

            return response;
        }catch(Exception e){
            return null;
        }
    }
}
