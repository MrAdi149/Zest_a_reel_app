package com.example.ujh.activity

import android.app.Dialog
import android.graphics.BitmapFactory
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.ujh.R
import com.example.ujh.databinding.ActivityFetchUserProfileBinding
import com.example.ujh.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.File

class FetchUserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityFetchUserProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var dialog: Dialog
    private lateinit var user: UserModel
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFetchUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        if (uid.isNotEmpty()){
            getUserData()
        }

    }

    private fun getUserData() {
       databaseReference.child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(UserModel::class.java)!!
                binding.tvFullName.setText(user.firstname + "" + user.lastname)
                binding.tvFullBio.setText(user.bio)

                storageReference = FirebaseStorage.getInstance().reference.child("Users/$uid.jpg")

                val picasso = Picasso.get()
                picasso.load(user.imageUrl).into(binding.profileImage)

//                getUserProfile()

            }

            override fun onCancelled(error: DatabaseError) {
                hideProgressBar()
                Toast.makeText(this@FetchUserProfile,"Failed to retrieve image",Toast.LENGTH_SHORT).show()
            }

        })
    }

//    private fun getUserProfile() {
//
//        val localFile = File.createTempFile("tempImage","jpg")
//        storageReference.getFile(localFile).addOnSuccessListener {
//
//            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
//            binding.profileImage.setImageBitmap(bitmap)
//            hideProgressBar()
//
//        }.addOnFailureListener{
//            hideProgressBar()
//            Toast.makeText(this,"Failed to retrieve image",Toast.LENGTH_SHORT).show()
//        }
//
//    }

    private fun showProgressBar(){
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }

}