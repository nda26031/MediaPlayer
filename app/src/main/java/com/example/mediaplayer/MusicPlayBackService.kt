package com.example.mediaplayer

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat


class MusicPlayBackService() : Service() {
    companion object {
        const val CHANNEL_ID = "CHANNEL_ID"
        const val ACTION_BACK = "ACTION_BACK"
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PLAY = "ACTION_PLAY"
    }

    private var onPlayAction: ((String, Music) -> Unit)? = null
    private var notificationManager: NotificationManager? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var currentIndex = 0
    private var musicLists: List<Music> = listOf()
    private val localBinder = LocalBinder()

    override fun onCreate() {
        createChannelNotification()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val receiveMusicLists = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra("musicList", Music::class.java)
            } else {
                intent.getParcelableArrayListExtra("musicList") ?: listOf()
            }
            if (receiveMusicLists != null) {
                musicLists = receiveMusicLists
            }


            when (intent.action) {
                ACTION_BACK -> {
                    playBackSong()
                    updateNotification(musicLists[currentIndex])
                }

                ACTION_PLAY_PAUSE -> {
                    playPauseSong()
                    updateNotification(musicLists[currentIndex])
                }

                ACTION_NEXT -> {
                    playNextSong()
                    updateNotification(musicLists[currentIndex])
                }

                ACTION_PLAY -> {
                    val receivePositionMusic = intent.getIntExtra("positionMusic", 0)
                    currentIndex = receivePositionMusic
                    playSong(musicLists[currentIndex])
                    sendNotification(musicLists[currentIndex])
                }

                else -> {}
            }

            intent.action?.let { action -> onPlayAction?.invoke(action,musicLists[currentIndex]) }

        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun setOnPlayAction(onPlayAction: (String, Music) -> Unit) {
        this.onPlayAction = onPlayAction
    }

    private fun playSong(music: Music) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@MusicPlayBackService, getSongUri(music.songPath))
            prepare()
            start()
        }
        Log.d("MusicPlayer", "Playing song: ${music.name}")
        isPlaying = true
    }

    private fun playPauseSong() {
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
        } else {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    private fun playBackSong() {
        if (currentIndex > 0) {
            currentIndex--
            playSong(musicLists[currentIndex])
        } else {
            isPlaying = false
            mediaPlayer?.pause()
            Log.d("MusicPlayer", "This is the first song")
        }
    }

    private fun playNextSong() {
        if (currentIndex < musicLists.size - 1) {
            currentIndex++
            playSong(musicLists[currentIndex])
        } else {
            isPlaying = false
            mediaPlayer?.pause()
            Log.d("MusicPlayer", "This is the last song")
        }
    }

    private fun getSongUri(songPath: Int): Uri {
        return Uri.parse("android.resource://$packageName/$songPath")
    }


    private fun createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Foreground Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)

        }
    }

    private fun sendNotification(music: Music) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setContentTitle(music.name)
            .setContentText(music.author)
            .addAction(R.drawable.back_icon, "back", createBackPendingIntent())
            .addAction(
                if (isPlaying) R.drawable.pause_icon else R.drawable.play_icon,
                if (isPlaying) "pause" else "play",
                createPlayPausePendingIntent()
            )
            .addAction(R.drawable.next_icon, "next", createNextPendingIntent())
            .setLargeIcon(BitmapFactory.decodeResource(resources, music.imagePath))
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startForeground(1, notification)
            }
        } else {
            startForeground(100, notification)
        }
    }

    private fun updateNotification(music: Music) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setContentTitle(music.name)
            .setContentText(music.author)
            .addAction(R.drawable.back_icon, "back", createBackPendingIntent())
            .addAction(
                if (isPlaying) R.drawable.pause_icon else R.drawable.play_icon,
                if (isPlaying) "pause" else "play",
                createPlayPausePendingIntent()
            )
            .addAction(R.drawable.next_icon, "next", createNextPendingIntent())
            .setLargeIcon(BitmapFactory.decodeResource(resources, music.imagePath))
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startForeground(1, notification)
            }
        } else {
            startForeground(1, notification)
        }
    }

    private fun createBackPendingIntent(): PendingIntent? {
        val intent = Intent(this, MusicPlayBackService::class.java).apply {
            action = ACTION_BACK
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createPlayPausePendingIntent(): PendingIntent? {
        val intent = Intent(this, MusicPlayBackService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createNextPendingIntent(): PendingIntent? {
        val intent = Intent(this, MusicPlayBackService::class.java).apply {
            action = ACTION_NEXT
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return localBinder
    }

    inner class LocalBinder : Binder() {
        fun getService(): MusicPlayBackService = this@MusicPlayBackService
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }

    fun getCurrentSong(): Music {
        return musicLists[currentIndex]
    }

}