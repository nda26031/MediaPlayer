package com.example.mediaplayer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
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

    private var musicPlayBackService: MusicPlayBackService? = null
    private var isBound = false

    private val musicLists = listOf(
        Music("Nỗi đau đính kèm", "Anh Tú Atus", R.drawable.image_song_5, R.raw.song5),
        Music("Anh đã làm được gì đâu", "Nhật Hoàng", R.drawable.image_song_2, R.raw.song2),
        Music("Vì điều gì", "Buitruonglinh", R.drawable.image_song_3, R.raw.song3),
        Music("1106", "Gnob", R.drawable.image_song_4, R.raw.song4),
        Music("Một nửa sự thật", "24k Right", R.drawable.image_song_1, R.raw.song1)
    )

    private val musicAdapter by lazy {
        MusicAdapter(musicLists, onItemClick = { music, position ->
            onItemClick(music, position)
        })
    }

    companion object {
        const val ACTION_BACK = "ACTION_BACK"
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "ACTION_NEXT"
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayBackService.LocalBinder
            musicPlayBackService = binder.getService()
            isBound = true
            musicPlayBackService?.setOnPlayAction { action, music ->
                updateUI(music)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicPlayBackService = null
            isBound = false
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

        findViewById<ImageView>(R.id.imgBack).setOnClickListener() {
            val intent = Intent(this, MusicPlayBackService::class.java).apply {
                action = ACTION_BACK
            }
            startService(intent)
        }
        findViewById<ImageView>(R.id.imgPlay).setOnClickListener() {
            val intent = Intent(this, MusicPlayBackService::class.java).apply {
                action = ACTION_PLAY_PAUSE
            }
            startService(intent)
        }
        findViewById<ImageView>(R.id.imgNext).setOnClickListener() {
            val intent = Intent(this, MusicPlayBackService::class.java).apply {
                action = ACTION_NEXT
            }
            startService(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isBound) {
            val intent = Intent(this, MusicPlayBackService::class.java)
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    private fun onItemClick(music: Music, position: Int) {
        val intent = Intent(this, MusicPlayBackService::class.java).apply {
            action = MusicPlayBackService.ACTION_PLAY
            putParcelableArrayListExtra("musicList", ArrayList(musicLists))
            putExtra("positionMusic", position)
        }
        startForegroundService(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun updateUI(music: Music) {
        if (isBound) {
            val isPlaying = musicPlayBackService?.isPlaying()

            if (isPlaying == true) {
                findViewById<ImageView>(R.id.imgPlay).setImageResource(R.drawable.pause_icon)
            } else {
                findViewById<ImageView>(R.id.imgPlay).setImageResource(R.drawable.play_icon)
            }
            findViewById<TextView>(R.id.tvSongPlaying).text = music.name
        }
    }

}