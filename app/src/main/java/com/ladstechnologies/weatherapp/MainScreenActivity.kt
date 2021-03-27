package com.ladstechnologies.weatherapp

import android.os.Bundle
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.custom_switch.view.*
import kotlinx.android.synthetic.main.drawer_layout.*


class MainScreenActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    CompoundButton.OnCheckedChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        navbar()
        switchesnotification()
        switchestemprature()
    }

    private fun switchestemprature() {
        val mNavigationView = findViewById(R.id.navbar) as NavigationView
        val navMenu = mNavigationView.menu
        val menuItem = navMenu.findItem(R.id.menu_Temprature)
        val switch = menuItem.actionView.findViewById(R.id.menu_Temprature) as Switch

        switch?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Checked temprature", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Unchecked temprature", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

//    override fun onBackPressed() {
//        if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
//            drawerlayout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
//    }

    fun navbar() {
        navbar.bringToFront()
        var toggle =
            ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.nav_app_bar_open_drawer_description, R.string.navigation_drawer_close)
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.icon_more)

        toggle.setToolbarNavigationClickListener {
            drawerlayout.openDrawer(GravityCompat.START)
        }
        navbar.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var id = item.itemId
        when (id) {
            R.id.menu_home -> {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()

            }
            R.id.menu_Location -> {

//                val intent = Intent(this, activitlocation::class.java)
//                closeDrawer()
//                startActivity(intent)

            }

            R.id.menu_Ads -> {
                Toast.makeText(this, "RemoveAdds", Toast.LENGTH_SHORT).show()

            }
            R.id.menu_Feedback -> {
                Toast.makeText(this, "Feedback and Suggestions", Toast.LENGTH_SHORT).show()

            }
            R.id.menu_Share -> {
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()

            }
        }
        return true
    }

    fun switchesnotification() {
        val mNavigationView = findViewById(R.id.navbar) as NavigationView
        val navMenu = mNavigationView.menu
        val menuItem = navMenu.findItem(R.id.menu_Notification)


        menuItem.actionView.btn_NotificationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                toast("Checked")
            } else {
                toast("Unchecked")
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            // If the switch button is on

            // Show the switch button checked status as toast message
            toast("Switch ON")

        } else {
            // If the switch button is off

            toast("Switch OFF")
        }
    }

//    fun closeDrawer() {
//        if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
//            drawerlayout.closeDrawer(GravityCompat.START)
//        }
//    }
}







