package com.example.ujh.activity

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.ujh.R
import com.example.ujh.databinding.ActivityUserProfileBinding
import com.example.ujh.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var dialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        binding.profileImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,1)
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        binding.continueBtn.setOnClickListener{

            showProgressBar()

            val firstName =binding.etFirstName.text.toString()
            val lastName =binding.etLastName.text.toString()
            val bio =binding.etBio.text.toString()

            val user=UserModel(firstName,lastName,bio)

            if(uid != null){
                databaseReference.child(uid).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful){
                        uploadProfilePic()
                    }
                    else{
                        hideProgressBar()
                        Toast.makeText(this,"Failed to upload the Profile",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun uploadProfilePic() {
        storageReference = FirebaseStorage.getInstance().getReference("Users/"+auth.currentUser?.uid)
        storageReference.putFile(imageUri).addOnSuccessListener {

            hideProgressBar()

            Toast.makeText(this,"Profile Successfully Updated",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{

            hideProgressBar()

            Toast.makeText(this,"Profile Not Updated",Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener{
            if (it.isSuccessful){
                storageReference.downloadUrl.addOnSuccessListener {task->

                    uploadInfo(task.toString())

                }
            }
        }
    }

    private fun uploadInfo(ImgUrl: String) {

        val user= UserModel(binding.etFirstName.text.toString(),binding.etLastName.text.toString(),binding.etBio.text.toString(),ImgUrl)

        databaseReference.child(auth.uid.toString()).setValue(user).addOnSuccessListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null){
            if(data.data != null){
                imageUri =data.data!!
                binding.profileImage.setImageURI(imageUri)
            }
        }

    }

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