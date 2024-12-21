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
    private val onItemClick: (Music) -> Unit) :
    Adapter<MusicAdapter.MusicViewHolder>() {

    companion object {
        const val ACTION_BACK = "ACTION_BACK"
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "ACTION_PLAY_PAUSE"
        const val CHANNEL_ID = "CHANNEL_ID"
    }

    class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(music: Music, onItemClick: (Music) -> Unit) {
            itemView.findViewById<TextView>(R.id.tvNameSong).text = music.name
            itemView.findViewById<TextView>(R.id.tvNameAuthor).text = music.author
            itemView.findViewById<ImageView>(R.id.imgSong).setImageResource(music.path)

            itemView.setOnClickListener() {
                val intent = Intent(itemView.context, MusicPlayBackService::class.java).apply {
                    action = ACTION_PLAY_PAUSE
                    putExtra("musicUri", music.songPath)
                }
                onItemClick(music)
                itemView.context.startService(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return MusicViewHolder(view)
    }

    override fun getItemCount(): Int = musicList.size

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music = musicList[position]
        holder.bind(music, onItemClick)
    }
}