package com.ladstechnologies.weatherapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal class Current {
    @SerializedName("dt")
    @Expose
    var dt: Int? = null

    @SerializedName("sunrise")
    @Expose
    var sunrise: Int? = null

    @SerializedName("sunset")
    @Expose
    var sunset: Int? = null

    @SerializedName("temp")
    @Expose
    var temp: Double? = null

    @SerializedName("feels_like")
    @Expose
    var feelsLike: Double? = null

    @SerializedName("pressure")
    @Expose
    var pressure: Int? = null

    @SerializedName("humidity")
    @Expose
    var humidity: Int? = null

    @SerializedName("dew_point")
    @Expose
    var dewPoint: Double? = null

    @SerializedName("uvi")
    @Expose
    var uvi: Int? = null

    @SerializedName("clouds")
    @Expose
    var clouds: Int? = null

    @SerializedName("visibility")
    @Expose
    var visibility: Int? = null

    @SerializedName("wind_speed")
    @Expose
    var windSpeed: Double? = null

    @SerializedName("wind_deg")
    @Expose
    var windDeg: Int? = null

    @SerializedName("weather")
    @Expose
    var weather: List<Weather>? = null
}

internal class Example {
    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lon")
    @Expose
    var lon: Double? = null

    @SerializedName("timezone")
    @Expose
    var timezone: String? = null

    @SerializedName("timezone_offset")
    @Expose
    var timezoneOffset: Int? = null

    @SerializedName("current")
    @Expose
    var current: Current? = null

    @SerializedName("minutely")
    @Expose
    var minutely: List<Minutely>? = null
}

internal class Minutely {
    @SerializedName("dt")
    @Expose
    var dt: Int? = null

    @SerializedName("precipitation")
    @Expose
    var precipitation: Int? = null
}

internal class Weather {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("main")
    @Expose
    var main: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null
}