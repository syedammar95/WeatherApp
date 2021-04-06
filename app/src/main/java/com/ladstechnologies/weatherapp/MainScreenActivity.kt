package com.ladstechnologies.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.custom_switch.view.*
import kotlinx.android.synthetic.main.custom_temp_switch.view.*
import kotlinx.android.synthetic.main.drawer_layout.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainScreenActivity() : AppCompatActivity(),
    OnNavigationItemSelectedListener,
    CompoundButton.OnCheckedChangeListener, LocationListener {

    private lateinit var mNavigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private var lat: Double? = null
    private var long: Double? = null
    private var mDate: String? = null
    private var mProgressBar: ProgressBar? = null
    var CITY: String = ""
    val API: String = "345ace9c6dab5666e63c9371fba3bb05"
    var permissionArrays = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.M)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_layout)

        //.. current Date and time on main screenActivity
        val tv_TimeDate = findViewById<TextView>(R.id.tv_TimeDate)
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentDate = sdf.format(Date())
        tv_TimeDate.text = currentDate.toString()

        //..set Permissions
        val version = Build.VERSION.SDK_INT
        if (version > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkIfAlreadyhavePermission() && checkIfAlreadyhavePermission2()) {
                // TODO: 4/2/2021
            } else {
                requestPermissions(permissionArrays, 101)
            }
        }

        val dateNow = Calendar.getInstance().time
        mDate = DateFormat.format("EEE", dateNow) as String

        //... ProgressBar method
        mProgressBar = ProgressBar(this)
//        mProgressBar?.setTitle("Please wait")
//        mProgressBar?.setCancelable(false)
//        mProgressBar?.setMessage("Displaying data ...")
        getLatlong()

        drawerLayout = findViewById(R.id.drawerlayout)
        btn_DrawerOpen.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        mNavigationView = findViewById(R.id.navbar)
        mNavigationView = navbar
        mNavigationView.setNavigationItemSelectedListener(this)
        switchestemprature()
        switchesnotification()
    }

    var locationManager: LocationManager? = null

    private fun getLatlong() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                115
            )
            return
        }
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        var location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (location == null)
            location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (location != null) {
            onLocationChanged(location)
        } else {
            getLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // TODO: 4/2/2021
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdates() {

        val isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!
        val isNetworkEnabled =
            locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)!!
        if (isGPSEnabled)
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                20000,
                10f,
                this
            ) else
            if (isNetworkEnabled)
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    20000,
                    10f,
                    this
                ) else {
                if (!isGPSEnabled && !isNetworkEnabled) {
                    // notify user
                    AlertDialog.Builder(this)
                        .setMessage("Location Providers not enabled.")
                        .setPositiveButton("Settings", DialogInterface.OnClickListener { _, _ ->
                            startActivity(
                                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            )
                        })
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
                            finish()
                        })
                        .show()
                }
            }
    }

    override fun onLocationChanged(location: Location) {
        lat = location.latitude
        long = location.longitude
        getGeoArea()
    }

    private fun getGeoArea() {
        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(lat!!, long!!, 1)
        val address = addresses[0].getAddressLine(0)
        val city = addresses[0].locality
        val country = addresses[0].countryName
        CITY = city.toString()
        weatherTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<ConstraintLayout>(R.id.Main_CL).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            val response: String?
            val mUrl =
                "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API"

            response = try {
                URL(mUrl).readText(
                    Charsets.UTF_8
                )
            } catch (e: Exception) {
                null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val temp = """${main.getInt("temp")}°C"""
                val windSpeed = wind.getString("""speed""")
                val weatherDescription = weather.getString("description")
                val address = """${jsonObj.getString("name")}, ${sys.getString("country")}"""
                findViewById<TextView>(R.id.tv_LocationHeading).text = address
                findViewById<TextView>(R.id.tv_weatherStatus).text =
                    weatherDescription.capitalize(Locale.ROOT)
                findViewById<TextView>(R.id.tv_temprature).text = temp
                findViewById<TextView>(R.id.tv_WindSpeed).text = windSpeed

                val image = findViewById<ImageView>(R.id.Img_weather)
                when (weatherDescription) {
                    "clear sky" -> {
                        image.setImageResource(R.drawable.sun_clear_sky)
                    }
                    "few clouds", "broken clouds" -> {
                        image.setImageResource(R.drawable.few_clouds_day)
                    }
                    "overcast clouds" -> {
                        image.setImageResource(R.drawable.overcast)
                    }
                    "scattered clouds" -> {
                        image.setImageResource(R.drawable.cloudy)
                    }
                    "thunderstorm", "light thunderstorm", "heavy thunderstorm",
                    "ragged thunderstorm" -> {
                        image.setImageResource(R.drawable.thunderstorm)
                    }
                    "thunderstorm with light rain", "thunderstorm with rain",
                    "thunderstorm with heavy rain", "thunderstorm with light drizzle",
                    "thunderstorm with drizzle", "thunderstorm with heavy drizzle" -> {
                        image.setImageResource(R.drawable.thundr_rain)
                    }
                    "rain", "shower rain", "light rain", "moderate rain",
                    "heavy intensity rain", "very heavy rain", "extreme rain",
                    "light intensity shower rain", "heavy intensity shower rain",
                    "ragged shower rain" -> {
                        image.setImageResource(R.drawable.rain_and_shower_rain)
                    }
                    "drizzle", "light intensity drizzle", "drizzle rain",
                    "shower rain and drizzle", "heavy shower rain and drizzle" -> {
                        image.setImageResource(R.drawable.drizzle_rain)
                    }
                    "snow", "light snow", "Heavy snow", "Light shower sleet",
                    "Light rain and snow", "Rain and snow", "Light shower snow",
                    "Shower snow", "Heavy shower snow" -> {
                        image.setImageResource(R.drawable.snow)
                    }
                    "Sleet" -> {
                        image.setImageResource(R.drawable.snow_sleet)
                    }
                    "mist", "smoke", "Haze", "fog", "squalls" -> {
                        image.setImageResource(R.drawable.mist)
                    }
                }

                //* Views populated, Hiding the loader, Showing the main design *//
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<ConstraintLayout>(R.id.Main_CL).visibility = View.VISIBLE
            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }
    }

    private fun checkIfAlreadyhavePermission2(): Boolean {

        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkIfAlreadyhavePermission(): Boolean {

        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun switchestemprature() {

        val navMenu = mNavigationView.menu
        val menuItem = navMenu.findItem(R.id.menu_Temprature)
        menuItem.actionView.btnchangetemp.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                toast("Fahrenheit")
            } else {
                toast("Celsius")
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                drawerlayout.closeDrawer(mNavigationView)
            }
            R.id.menu_Temprature -> {
                val intent = Intent(this, NextdaysActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_Location -> {
                val intent = Intent(this, ManageLocationActivity::class.java)
                startActivity(intent)
            }
//            R.id.menu_Ads -> {
//                toast("RemoveAdds")
//            }
            R.id.menu_Feedback -> {
                toast("Feedback and Suggestions")
            }
            R.id.menu_Share -> {
                toast("Share")
            }
        }
        return true
    }

    private fun switchesnotification() {
        val navMenu = mNavigationView.menu
        val menuItem = navMenu.findItem(R.id.menu_Notification)
        menuItem.actionView.btn_NotificationSwitch.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            toast("Notifications ON")
        } else {
            toast("Notifications OFF")
        }
    }
}