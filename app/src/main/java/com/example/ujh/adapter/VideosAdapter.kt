package com.example.ujh.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ujh.R
import com.example.ujh.activity.AddVideo
import com.example.ujh.activity.FetchUserProfile
import com.example.ujh.activity.UserProfile
import com.example.ujh.model.VideoModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class VideosAdapter (options: FirebaseRecyclerOptions<VideoModel?>):
FirebaseRecyclerAdapter<VideoModel?, VideosAdapter.MyViewHolder>(options)
{

    private var currentPosition: Int = 0

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the custom layout
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.video_row, parent, false)
        // Return a new holder instance
        return MyViewHolder(view)
    }
    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: VideoModel) {
        val shuffledPosition = (0 until itemCount).shuffled().first()
        val shuffledModel = getItem(shuffledPosition)
        holder.setdata(shuffledModel)
//        holder.setdata(model)
    }

    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var videoView: VideoView
        var addVideoFab: ImageView
        var share: ImageView
        var id:TextView
        var title: TextView
        var description: TextView
        var person : ImageView
        var addPerson: ImageView

        fun setdata(obj: VideoModel){

            videoView.setVideoPath(obj.videoUri)
            title.text=obj.title.toString()
            description.text=obj.description.toString()
            videoView.setOnPreparedListener { mediaPlayer -> mediaPlayer.start() }

            addPerson.setOnClickListener { v->

                val intent = Intent(v.context, UserProfile::class.java)
                v.context.startActivity(intent)

            }

            person.setOnClickListener { v->

                val intent = Intent(v.context, FetchUserProfile::class.java)
                v.context.startActivity(intent)

            }

            addVideoFab.setOnClickListener{v->
                val intent = Intent(v.context, AddVideo::class.java)
                v.context.startActivity(intent)
            }

            share.setOnClickListener { v->

                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"

                shareIntent.putExtra(Intent.EXTRA_TEXT,obj.videoUri)

                val chooser = Intent.createChooser(shareIntent,"Share Using")
                v.context.startActivity(chooser)
            }

        }
        //initialize the element used in our project
        init {
            videoView = itemView.findViewById<View>(R.id.videoView) as VideoView
            addVideoFab = itemView.findViewById<View>(R.id.addVideoFab) as ImageView
            share = itemView.findViewById<View>(R.id.share) as ImageView
            title = itemView.findViewById<View>(R.id.textVideoTitle) as TextView
            description = itemView.findViewById<View>(R.id.textVideoDescription) as TextView
            id= itemView.findViewById<View>(R.id.textVideoId) as TextView
            person = itemView.findViewById<View>(R.id.person) as ImageView
            addPerson = itemView.findViewById<View>(R.id.addPerson) as ImageView
        }

    }
}
