package com.example.mediaplayer

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.Notification.MediaStyle
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat


class MusicPlayBackService : Service() {
    companion object {
        const val ACTION_BACK = "ACTION_BACK"
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "ACTION_PLAY_PAUSE"
        const val CHANNEL_ID = "CHANNEL_ID"
    }

    private var notificationManager: NotificationManager? = null
    private val music: Music? = null

    override fun onCreate() {
        createChannelNotification()
        if (music != null) {
            sendNotification(music)
        } else {
            Log.d("MusicPlayer", "Music is null")
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
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
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                100,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(100, notification)
        }
    }
}

//    private lateinit var mediaPlayer: MediaPlayer
//    private val binder = MediaBinder()
//    private lateinit var notificationManager: NotificationManager
//    private var musicList: List<Media> = listOf()
//    private var currentIndex = 0
//
//    inner class MediaBinder : Binder() {
//        fun getService(): MediaPlayBackService = this@MediaPlayBackService
//    }
//
//    override fun onBind(intent: Intent?): IBinder = binder
//
//    override fun onCreate() {
//        super.onCreate()
//        val mediaPlayer: MediaPlayer
//        createChannelNotification()
//    }
//
//    fun setMusicList(list: List<Media>) {
//        musicList = list
//    }
//
//    fun playMusic(media: Media) {
//        mediaPlayer.reset()
//        val afd = resources.openRawResourceFd(media.songPath)
//        mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
//        afd.close()
//        mediaPlayer.prepare()
//        mediaPlayer.start()
//
//        startForeground(1, createNotification(media))
//    }
//
//    fun playCurrentMusic() {
//        if (musicList.isNotEmpty()) {
//            playMusic(musicList[currentIndex])
//        }
//    }
//
//    fun pauseMusic(media: Media) {
//        if (mediaPlayer.isPlaying) {
//            mediaPlayer.pause()
//            updateNotification(media)
//        }
//    }
//
//    fun stopMusic() {
//        if (mediaPlayer.isPlaying) {
//            mediaPlayer.stop()
//            stopForeground(STOP_FOREGROUND_REMOVE)
//        }
//    }
//
//    fun isPlaying(): Boolean = mediaPlayer.isPlaying
//
//    fun playNext() {
//        if (musicList.isNotEmpty()) {
//            currentIndex = (currentIndex + 1) % musicList.size
//            playCurrentMusic()
//        }
//    }
//
//    fun playPrevious() {
//        if (musicList.isNotEmpty()) {
//            currentIndex = if (currentIndex - 1 < 0) musicList.size - 1 else currentIndex - 1
//            playCurrentMusic()
//        }
//    }


//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        when (intent?.action) {
//            "ACTION_PLAY_PAUSE" -> {
//                val currentMedia = musicList.getOrNull(currentIndex)
//                if (mediaPlayer.isPlaying) {
//                    currentMedia?.let { pauseMusic(it) }
//                } else {
//                    currentMedia?.let {
//                        mediaPlayer.start()
//                        updateNotification(it)
//                    }
//                }
//            }
//            "ACTION_BACK" -> {
//                playPrevious()
//            }
//            "ACTION_NEXT" -> {
//                playNext()
//            }
//        }
//        return START_STICKY
//    }


//    private fun createNotification(media: Media): Notification {
//
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(media.name)
//            .setContentText(media.author)
//            .setSmallIcon(R.drawable.baseline_music_note_24)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .addAction(R.drawable.back_icon, "Back", createPendingIntent(ACTION_BACK))
//            .addAction(
//                if (mediaPlayer.isPlaying) R.drawable.pause_icon else R.drawable.play_icon,
//                if (mediaPlayer.isPlaying) "Pause" else "Play",
//                createPendingIntent(ACTION_PLAY_PAUSE)
//            )
//            .addAction(R.drawable.next_icon, "Next", createPendingIntent(ACTION_NEXT))
//            .setLargeIcon(BitmapFactory.decodeResource(resources, media.path))
//            .setContentIntent(pendingIntent)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .build()
//        return notification
//    }

//    private fun updateNotification(media: Media) {
//        val notification = createNotification(media)
//        val notificationManager = getSystemService(NotificationManager::class.java)
//        notificationManager?.notify(1, notification)
//    }
//
//    private fun createPendingIntent(action: String): PendingIntent {
//        val intent = Intent(this, MediaPlayBackService::class.java).apply {
//            this.action = action
//        }
//        return PendingIntent.getService(
//            this,
//            0,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//    }
//
//
//    private fun createChannelNotification() {
//        val channel = NotificationChannel(
//            CHANNEL_ID,
//            "Media Play Back Service",
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        notificationManager = getSystemService(NotificationManager::class.java)
//        notificationManager.createNotificationChannel(channel)
//    }
//}