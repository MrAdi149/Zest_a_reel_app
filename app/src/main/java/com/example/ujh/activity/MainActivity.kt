package com.example.ujh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.SearchView
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.ujh.R
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.ujh.adapter.VideosAdapter
import com.example.ujh.databinding.ActivityMainBinding
import com.example.ujh.model.VideoModel
import com.firebase.ui.database.FirebaseRecyclerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var actionBar: ActionBar? = null
    //viewPager2- allows the user to flip top, bottom, left and right through pages of data
    lateinit var viewPager2:ViewPager2
    lateinit var adapter: VideosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**set fullscreen*/

        supportActionBar?.hide()

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        /**set find id*/
        viewPager2 = findViewById(R.id.vpager)

        /**set database*/
        val mDataBase = Firebase.database.getReference("videos")

        val options = FirebaseRecyclerOptions.Builder<VideoModel>()
            .setQuery(mDataBase,VideoModel::class.java)
            .build()
        /**set adapter*/
        adapter = VideosAdapter(options)
        viewPager2.adapter = adapter


    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
}