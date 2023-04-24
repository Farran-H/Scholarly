package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class home_page : AppCompatActivity() {

    //Points to Users in node of our database
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://scholarly-login-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")

    //gets user that is signed in
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    private lateinit var storageReference: StorageReference
    private lateinit var mapBtn: Button
    private lateinit var addItemBtn: Button
    private lateinit var viewItemBtn: Button
    private lateinit var mapBtn2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)


        mapBtn = findViewById(R.id.mapBtn)
        addItemBtn = findViewById(R.id.addItemBtn)
        viewItemBtn = findViewById(R.id.viewItemBtn)
        mapBtn2 = findViewById(R.id.mapBtn2)

        // Check if user is logged in
        val user = auth.currentUser
        if (user == null) {
            // If user is not logged in, start LandingPage activity
            val intent = Intent(applicationContext, LandingPage::class.java)
            startActivity(intent)
            finish()
        } else {
            // If user is logged in, continue with the home page logic
            storageReference = FirebaseStorage.getInstance().reference


            val uid = auth.currentUser?.uid
            if (uid != null) {
                databaseReference.child(uid).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {


                            val user = snapshot.getValue(User::class.java)
                            if (user != null) {
                                val fullName = user.fullName
                                // Send the retrieved full name to the xml
                                val textViewFullName: TextView = findViewById(R.id.tvfullname)
                                textViewFullName.text = fullName

                                // Send the retrieved student ID to the xml
                                val studentID = user.studentID
                                val textViewstudentID: TextView = findViewById(R.id.tvstudentID)
                                textViewstudentID.text = studentID

                                //if name is clicked, send to profile page
                                textViewFullName.setOnClickListener {
                                    val intent = Intent(this@home_page, ProfilePage::class.java)
                                    startActivity(intent)
                                    finish()
                                }


                            }
                        } else {
                            // Set  values for fullName and studentID if user snapshot does not exist
                            val textViewFullName: TextView = findViewById(R.id.tvfullname)
                            textViewFullName.text = "Full Name"
                            val textViewstudentID: TextView = findViewById(R.id.tvstudentID)
                            textViewstudentID.text = "Student Number"

                            //if name is clicked, send to profile page
                            textViewFullName.setOnClickListener {
                                // Create an Intent to start the ProfilePageActivity
                                val intent = Intent(this@home_page, ProfilePage::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }

            // Set OnClickListener for mapBtn2 button
            mapBtn2.setOnClickListener {
                // Create an Intent to start the MapScreenActivity
                val intent = Intent(this@home_page, MapsAct::class.java)
                startActivity(intent)
            }


            // Set OnClickListener for mapBtn button
            mapBtn.setOnClickListener {
                // Create an Intent to start the MapScreenActivity
                val intent = Intent(this@home_page, MapScreen::class.java)
                startActivity(intent)
            }

            // Set OnClickListener for mapBtn button
            addItemBtn.setOnClickListener {
                // Create an Intent to start the MapScreenActivity
                val intent = Intent(this@home_page, AddListItemPage::class.java)
                startActivity(intent)
            }

            // Set OnClickListener for to do list button
            viewItemBtn.setOnClickListener {
                // Create an Intent to start the MapScreenActivity
                val intent = Intent(this@home_page, ToDoList::class.java)
                startActivity(intent)
            }


            // Initialise Var as a Final Value
            val drawerLayout: DrawerLayout? = findViewById(R.id.drawerLayout)

            // Event Listener to allow users to click on the nav icon to display the side nav/drawer
            findViewById<View>(R.id.menuIcon).setOnClickListener {
                drawerLayout?.openDrawer(GravityCompat.START)
            }
        }
    }
}
