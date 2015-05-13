package com.jasonf7.what2wear.http.weather;

import java.util.List;

/**
 * Created by jasonf7 on 12/05/15.
 * API Parameter reference: http://openweathermap.org/weather-data#current
 */
public class WeatherResponse {
    public long id;
    public long dt;
    public String name;
    public WeatherCoordinate coord;
    public WeatherSystem sys;
    public WeatherTemperature main;
    public WeatherWind wind;
    public WeatherClouds clouds;
    public List<WeatherDescription> weather;
    public transient String rain;
    public transient String snow;
}
