package com.example.ujh.model

class VideoModel{
    var title:String?=null//we use title values unless it is null
    var description:String?=null
    var videoUri:String?=null

    constructor(){}

    constructor(title: String?, description: String?,url: String?){
        this.title= title
        this.description=description
        this.videoUri=videoUri
    }


}


