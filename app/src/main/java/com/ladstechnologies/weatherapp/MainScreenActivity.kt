package com.ladstechnologies.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
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
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.custom_switch.view.*
import kotlinx.android.synthetic.main.drawer_layout.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainScreenActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
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
        val sdf = SimpleDateFormat("hh:mm a")
        val currentDate = sdf.format(Date())
        tv_TimeDate.text = currentDate.toString()

        //..set Permissions
        val MyVersion = Build.VERSION.SDK_INT
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkIfAlreadyhavePermission() && checkIfAlreadyhavePermission2()) {
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
        locationManager= getSystemService(LOCATION_SERVICE) as LocationManager
        var location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if(location==null)
            location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (location != null) {
            onLocationChanged(location)
        } else {
            getLocationUpdates()
        }
    }
    @SuppressLint("MissingPermission")
    fun getLocationUpdates() {

            val isGPSEnabled=locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!
            val isNetworkEnabled=locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)!!


            if(isGPSEnabled)

                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    20000,
                    10f,
                    this
                )

            else if(isNetworkEnabled)
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    20000,
                    10f,
                    this
                )

            else
            {
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
            val addresses:List<Address> = geocoder.getFromLocation(lat!!, long!!, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName
            CITY = city.toString()
            weatherTask().execute()
        }

    inner class weatherTask(): AsyncTask<String, Void, String>() {
        override fun onPreExecute(){
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<ConstraintLayout>(R.id.Main_CL).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
    }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            val mUrl="https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API"
            try {
                response =
                    URL(mUrl).readText(
                        Charsets.UTF_8
                    ) } catch (e: Exception)
            {
                response = null
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

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
                    .format(Date(updatedAt * 1000))
                val temp = """${main.getString("temp")}°C"""
//                val tempMin = """Min Temp: ${main.getString("temp_min")}°C"""
//                val tempMax = """Max Temp: ${main.getString("temp_max")}°C"""
//                val pressure = main.getString("pressure")
//                val humidity = main.getString("humidity")
//                val sunrise: Long = sys.getLong("""sunrise""")
//                val sunset: Long = sys.getLong("""sunset""")
                val windSpeed = wind.getString("""speed""")
                val weatherDescription = weather.getString("description")

                val address = """${jsonObj.getString("name")}, ${sys.getString("country")}"""

                findViewById<TextView>(R.id.tv_LocationHeading).text = address
//                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.tv_weatherStatus).text = weatherDescription.capitalize(Locale.ROOT)
                findViewById<TextView>(R.id.tv_temprature).text = temp
//                findViewById<TextView>(R.id.temp_min).text = tempMin
//                findViewById<TextView>(R.id.temp_max).text = tempMax
//
//                findViewById<TextView>(R.id.sunrise).text =
//                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
//
//                findViewById<TextView>(R.id.sunset).text =
//                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.tv_WindSpeed).text = windSpeed
//                findViewById<TextView>(R.id.pressure).text = pressure
//                findViewById<TextView>(R.id.humidity).text = humidity

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
        mNavigationView = navbar
        val navMenu = mNavigationView.menu
        val menuItem = navMenu.findItem(R.id.menu_Temprature)
        val switch = menuItem.actionView.findViewById(R.id.menu_Temprature) as View

        switch.setOnClickListener {
            toast("Checked ")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                toast("Home")
            }
            R.id.menu_Location -> {
                toast("Location")
//                val intent = Intent(this, activitlocation::class.java)
//                closeDrawer()
//                startActivity(intent)
            }

            R.id.menu_Ads -> {
                toast("RemoveAdds")
            }
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
        val mNavigationView = findViewById<NavigationView>(R.id.navbar)
        val navMenu = mNavigationView.menu
        val menuItem = navMenu.findItem(R.id.menu_Notification)

        menuItem.actionView.btn_NotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                toast("Checked")
            } else {
                toast("Unchecked")
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            toast("Switch ON")
        } else {
            toast("Switch OFF")
        }
    }



//    fun closeDrawer() {
//        if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
//            drawerlayout.closeDrawer(GravityCompat.START)
//        }
//    }
}