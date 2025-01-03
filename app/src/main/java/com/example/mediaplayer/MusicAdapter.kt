package com.example.mediaplayer

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class MusicAdapter(
    private val musicList: List<Music>,
    private val onItemClick: (Music,Int) -> Unit
) :
    Adapter<MusicAdapter.MusicViewHolder>() {
    class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(music: Music,position: Int,onItemClick: (Music,position:Int) -> Unit) {
            itemView.findViewById<TextView>(R.id.tvNameSong).text = music.name
            itemView.findViewById<TextView>(R.id.tvNameAuthor).text = music.author
            itemView.findViewById<ImageView>(R.id.imgSong).setImageResource(music.imagePath)

            itemView.setOnClickListener() {
                onItemClick(music,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return MusicViewHolder(view)
    }

    override fun getItemCount(): Int = musicList.size

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int,) {
        val music = musicList[position]
        holder.bind(music,position,onItemClick)
    }
}