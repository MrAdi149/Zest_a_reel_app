package com.example.ujh.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ujh.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddVideo : AppCompatActivity() {

    //actionbar
//    private var actionBar: ActionBar? = null

    //constant to pick video
    private val VIDEO_PICK_GALLERY_CODE = 111
    private val VIDEO_PICK_CAMERA_CODE = 101
    private val REQUEST_CODE = 100

    //request camera permission to record video
    private val CAMERA_REQUEST_CODE = 102
    private val GALLERY_REQUEST_CODE = 103


    private lateinit var uploadVideoBtn: Button
    private lateinit var video: VideoView
    private lateinit var pickVideoFab: FloatingActionButton

    //array for camera request permission
    private lateinit var cameraPermission: Array<String>
    private lateinit var galleryPermission: Array<String>


    private lateinit var progressDialog: ProgressDialog


    //uri to picked video
    private var videoUri: Uri? = null

    lateinit var titleEt: EditText
    private var title: String = ""
    lateinit var description: EditText
    private var desc: String = ""


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)

//        actionBar = supportActionBar
//        actionBar?.title = "Add Video"
//        actionBar?.setDisplayShowHomeEnabled(true)
//        actionBar?.setDisplayHomeAsUpEnabled(true)

        //init camera permission array
        cameraPermission = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Uploading Video.....")
        progressDialog.setCanceledOnTouchOutside(false)

        uploadVideoBtn = findViewById(R.id.uploadVideoBtn)
        video = findViewById(R.id.video)
        pickVideoFab = findViewById(R.id.pickVideoFab)
        titleEt = findViewById(R.id.titleEt)
        description = findViewById(R.id.description)



        //upload video
        uploadVideoBtn.setOnClickListener {

            title = titleEt.text.toString().trim()
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
            }

            desc = description.text.toString().trim()
            if (TextUtils.isEmpty(desc)) {
                Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show()
            }
            else if(videoUri == null){
                Toast.makeText(this, "Pick the video first", Toast.LENGTH_SHORT).show()
            }

            else {
                uploadVideoFirebase()
            }

        }

        //pick video
        pickVideoFab.setOnClickListener {
            videoPickDialog()
        }
    }

    private fun uploadVideoFirebase() {
        progressDialog.show()

        val timestamp = "" + System.currentTimeMillis()

        val filePathAndName = "Videos/video_$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)

        storageReference.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadUri = uriTask.result


                if (uriTask.isSuccessful) {

                    val hashMap = HashMap<String, Any>()
                    hashMap["id"] = "$timestamp"
                    hashMap["title"] = "$title"
                    hashMap["description"] = desc
                    hashMap["videoUri"] = "$downloadUri"


                    val dbReference = FirebaseDatabase.getInstance().getReference("videos")
                    dbReference.child(timestamp)
                        .setValue(hashMap)
                        .addOnSuccessListener { taskSnapshot ->
                            progressDialog.dismiss()
                            Toast.makeText(this, "Video Upload", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener { e ->

                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    //set the video to video view
    private fun setVideoToVideoView() {

        //video play control
        val mediaController = MediaController(this)
        mediaController.setAnchorView(video)


        //set media controller
        video.setMediaController(mediaController)
        video.setVideoURI(videoUri)
        video.requestFocus()
        video.setOnPreparedListener{
            //when video is ready don't play it automatically
            video.pause()
        }
    }

    private fun videoPickDialog() {

        //option to display in dialog
        val options = arrayOf("camera", "Gallery")

        //alert dialog
        val builder = AlertDialog.Builder(this)

        //title
        builder.setTitle("Pick Videos From")
            .setItems(options){ dialogInterface, i->

                //handle item click
                if(i==0){

                    //camera clicked
                    if(!checkCameraPermission()){

                        //permission was not allowed, request
                        requestCameraPermission()
                    }
                    else{

                        //permission was allowed, pick video
                        videoPickCamera()
                    }

                }
                else{

                    //gallery clicked
                    videoPickGallery()
                }
            }.show()
    }

    //request camera permission
    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(
            this,
            cameraPermission,
            CAMERA_REQUEST_CODE
        )
    }

    //check if camera permission i.e. camera and storage is allowed or not
    private fun checkCameraPermission():Boolean{

        val result1 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )== PackageManager.PERMISSION_GRANTED

        val result2 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )== PackageManager.PERMISSION_GRANTED

        return result1 && result2

    }

    //video pick from gallery
    private fun videoPickGallery(){
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK

        startActivityForResult(
            Intent.createChooser(intent, "Choose Video"),
            VIDEO_PICK_GALLERY_CODE
        )
    }

    //video pick from camera
    private fun videoPickCamera(){
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }

    override fun onSupportNavigateUp(): Boolean {

        //goto previous activity
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    //handle permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST_CODE->
                if(grantResults.size > 0){

                    //check if permission allowed or not
                    val cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED

                    if(cameraAccepted && storageAccepted){
                        //both permission allowed
                        videoPickCamera()
                    }
                    else{

                        //both or one of those are denied
                        Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        when(requestCode){
            GALLERY_REQUEST_CODE->
                if(grantResults.size > 0){

                    //check if permission allowed or not
                    val galleryAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED

                    if(galleryAccepted && storageAccepted){

                        //both permission allowed
                        videoPickGallery()
                    }
                    else{

                        //both or one of those are denied
                        Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //handle video pick result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK){

            //video is pick from camera or gallery
            if(requestCode == VIDEO_PICK_CAMERA_CODE){

                //video pick from camera
                videoUri = data!!.data
                setVideoToVideoView()
            }
            else if (requestCode == VIDEO_PICK_GALLERY_CODE){

                //video pick from gallery
                videoUri = data!!.data
                setVideoToVideoView()
            }
            else{
                //cancelled picking video
                Toast.makeText(this,"CANCELLED", Toast.LENGTH_SHORT).show()
            }
            super.onActivityResult(requestCode, resultCode, data)
        }

  }
}

