package com.example.mediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var musicPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var currentSongIndex = 0

    private val musicAdapter by lazy {
        MusicAdapter(musicLists) { media ->
            playSong(media)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rcvSong)
        recyclerView.adapter = musicAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.imgBack).setOnClickListener() { playBackSong() }
        findViewById<ImageView>(R.id.imgPlay).setOnClickListener() { tooglePlay() }
        findViewById<ImageView>(R.id.imgNext).setOnClickListener() { playNextSong() }
    }


    private val musicLists = listOf(
        Music("Nỗi đau đính kèm", "Anh Tú Atus", R.drawable.image_song_5, R.raw.song5),
        Music("Anh đã làm được gì đâu", "Nhật Hoàng", R.drawable.image_song_2, R.raw.song2),
        Music("Vì điều gì", "Buitruonglinh", R.drawable.image_song_3, R.raw.song3),
        Music("1106", "Gnob", R.drawable.image_song_4, R.raw.song4),
        Music("Một nửa sự thật", "24k Right", R.drawable.image_song_1, R.raw.song1)

    )

    private fun playSong(music: Music) {
        musicPlayer?.release() // Release any previous MediaPlayer
        musicPlayer = MediaPlayer.create(this, music.songPath)
        findViewById<ImageView>(R.id.imgPlay).setImageResource(R.drawable.pause_icon)
        findViewById<TextView>(R.id.tvSongPlaying).text = music.name
        Log.d("MusicPlayer", "Playing song: ${music.name}")
        musicPlayer?.start()
        isPlaying = true
        Log.d("MusicPlayer", "is playing: $isPlaying")
    }

    private fun pauseSong() {
        musicPlayer?.pause()
        Log.d("MusicPlayer", "Pause song: ${musicLists[currentSongIndex].name}")
        isPlaying = false
        Log.d("MusicPlayer", "is playing: $isPlaying")
        findViewById<ImageView>(R.id.imgPlay).setImageResource(R.drawable.play_icon)
    }

    private fun resumeSong() {
        musicPlayer?.start()
        Log.d("MusicPlayer", "Resume song: ${musicLists[currentSongIndex].name}")
        isPlaying = true
        Log.d("MusicPlayer", "is playing: $isPlaying")
        findViewById<ImageView>(R.id.imgPlay).setImageResource(R.drawable.pause_icon)
    }

    private fun playNextSong() {
        if (currentSongIndex < musicLists.size - 1) {
            currentSongIndex++
            Log.d("MusicPlayer", "Playing next song: ${musicLists[currentSongIndex].name}")
            playSong(musicLists[currentSongIndex])
        } else {
            Log.d("MusicPlayer", "It's the last song")
            pauseSong()
            Toast.makeText(this, "It's the last song", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playBackSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--
            Log.d("MusicPlayer", "Playing previous song: ${musicLists[currentSongIndex].name}")
            playSong(musicLists[currentSongIndex])
        } else {
            Log.d("MusicPlayer", "It's the first song")
            pauseSong()
            Toast.makeText(this, "It's the first song", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tooglePlay() {
        if (isPlaying) {
            pauseSong()
        } else {
            resumeSong()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer?.release() // Clean up MediaPlayer when activity is destroyed
    }
}