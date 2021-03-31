package com.ladstechnologies.weatherapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.custom_switch.view.*
import kotlinx.android.synthetic.main.drawer_layout.*
import kotlinx.android.synthetic.main.drawer_layout.view.*


class MainScreenActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    CompoundButton.OnCheckedChangeListener {

    private lateinit var mNavigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_layout)

        drawerLayout = findViewById(R.id.drawerlayout)

        btn_DrawerOpen.setOnClickListener {
            if(!drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        switchestemprature()
        switchesnotification()
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