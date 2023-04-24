package com.example.myapplication

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.example.myapplication.databinding.ActivityAddUserDetailsPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*
import android.app.Activity
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

class AddUserDetailsPage : AppCompatActivity() {

    private lateinit var binding : ActivityAddUserDetailsPageBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var databaseReference : DatabaseReference
    private lateinit var dialog : Dialog
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserDetailsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance("https://scholarly-login-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")

        if (uid != null) {
            databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {


                            binding.editfullName.setText(user.fullName)
                            binding.editUserCourse.setText(user.course)
                            binding.editUserstudentID.setText(user.studentID)
                            binding.editUserDob.setText(user.dob)

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }




        binding.uploadBtn.setOnClickListener {
            // Open a file picker or camera intent to allow the user to choose/select a profile picture
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.saveBtn.setOnClickListener {
            showProgressBar()

            val fullName = binding.editfullName.text.toString()
            val course = binding.editUserCourse.text.toString()
            val studentID = binding.editUserstudentID.text.toString()
            val dob = binding.editUserDob.text.toString()


            // Validate form fields
            if (fullName.isEmpty() || course.isEmpty() || studentID.isEmpty()) {
                hideProgressBar()
                Toast.makeText(this@AddUserDetailsPage, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate course format
            if (!isValidCourseFormat(course)) {
                hideProgressBar()
                Toast.makeText(this@AddUserDetailsPage, "Course code invalid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //student id has to be 8 digits
            if (studentID.length != 8) {
                hideProgressBar()
                Toast.makeText(this@AddUserDetailsPage, "Student ID must be 8 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Log the values of fullName, course, and studentID variables
            Log.d("TAG", "fullName: $fullName")
            Log.d("TAG", "course: $course")
            Log.d("TAG", "studentID: $studentID")

            val user = User(fullName, course, studentID, dob)
            if (uid != null) {
                databaseReference.child(uid).setValue(user).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        hideProgressBar()
                        Toast.makeText(this@AddUserDetailsPage, "Profile successfully updated", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ProfilePage::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        hideProgressBar()
                        Toast.makeText(this@AddUserDetailsPage, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    hideProgressBar()
                    Toast.makeText(this@AddUserDetailsPage, "Failed to update profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                hideProgressBar()
                Toast.makeText(this@AddUserDetailsPage, "User ID is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressBar(){
        dialog = Dialog(this@AddUserDetailsPage)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.show()
    }

    private fun hideProgressBar() {
        dialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }



    // Function to validate the course format
    fun isValidCourseFormat(course: String): Boolean {
        val regex = "[A-Za-z]{2}[0-9]{3}".toRegex()
        return regex.matches(course)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the image URI from the result
            val imageUri = data.data

            // Get the image bitmap from the image URI
            val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            // Convert the image bitmap to a byte array
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageBytes = baos.toByteArray()

            // Create a temporary file to store the image bytes
            val tempFile = File.createTempFile("profile_picture", ".jpg")
            tempFile.writeBytes(imageBytes)

            // Call the function to upload the image to Firebase Storage
            uploadImageToFirebaseStorage(tempFile)
        }
    }



    private fun uploadImageToFirebaseStorage(imageFile: File) {
        showProgressBar()

        // Get the current user ID
        val uid = auth.currentUser?.uid

        // Create a unique filename for the image by appending "profilepic" to the user ID
        val imageName = "$uid" + "_profilepic.jpg"

        // Create a reference to the Firebase Storage location where the image will be uploaded
        val imageRef = storageRef.child("profile_picture/$imageName")

        // Upload the image file to Firebase Storage
        val uploadTask = imageRef.putFile(Uri.fromFile(imageFile))

        // Add a success listener to the upload task
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Get the download URL of the uploaded image
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Hide the progress bar
                hideProgressBar()

                // Get the image URL as a string
                val imageUrl = uri.toString()

                // Update the user profile with the image URL
                if (uid != null) {
                    databaseReference.child(uid).child("profilePic").setValue(imageUrl)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@AddUserDetailsPage,
                                    "Profile picture successfully updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@AddUserDetailsPage,
                                    "Failed to update profile picture",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(
                                this@AddUserDetailsPage,
                                "Failed to update profile picture: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }

        // Add a failure listener to the upload task
        uploadTask.addOnFailureListener { exception ->
            // Hide the progress bar
            hideProgressBar()

            Toast.makeText(
                this@AddUserDetailsPage,
                "Failed to upload profile picture: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
