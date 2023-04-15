package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.view.View

class home_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        // Initialise Var as a Final Value
        val drawerLayout: DrawerLayout? = findViewById(R.id.drawerLayout)

        // Event Listener to allow users to click on the nav icon ao display the side nav/drawer
        findViewById<View>(R.id.menuIcon).setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }

    }
}