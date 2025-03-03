package com.example.androidadvancedlab1.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat as MediaNotificationCompat
import com.example.androidadvancedlab1.R
import android.graphics.BitmapFactory

class MusicPlayerService : Service() {

    companion object {
        const val START_FOREGROUND_ACTION = "com.example.musicplayer.action.START_FOREGROUND"
        const val STOP_FOREGROUND_ACTION = "com.example.musicplayer.action.STOP_FOREGROUND"
        const val PAUSE_ACTION = "com.example.musicplayer.action.PAUSE"
        const val PLAY_ACTION = "com.example.musicplayer.action.PLAY"
        const val NOTIFICATION_CHANNEL_ID = "musicplayer_channel"
        const val NOTIFICATION_ID = 1
    }

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private var audioManager: AudioManager? = null
    private var isPlaying = false

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        initializeMediaPlayer()
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "MusicPlayerService")
    }

    private fun initializeMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer().apply {
                val assetFileDescriptor = assets.openFd("kn.mp3")
                setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                assetFileDescriptor.close()
                prepareAsync()
                setOnPreparedListener {
                    Log.d("MusicPlayerService", "MediaPlayer готов к воспроизведению")
                }
                setOnCompletionListener {
                    Log.d("MusicPlayerService", "Музыка завершена")
                    stopSelf()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("MusicPlayerService", "Ошибка MediaPlayer: what=$what, extra=$extra")
                    false
                }
                isLooping = true
            }
        } catch (e: Exception) {
            Log.e("MusicPlayerService", "Ошибка инициализации MediaPlayer: ${e.message}")
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                START_FOREGROUND_ACTION -> {
                    playMusic()
                    isPlaying = true
                    startForeground(NOTIFICATION_ID, createNotification())
                }
                STOP_FOREGROUND_ACTION -> {
                    stopMusic()
                    stopSelf() // Останавливает сервис
                    return START_NOT_STICKY
                }
                PAUSE_ACTION -> {
                    pauseMusic()
                    isPlaying = false
                    updateNotification()
                }
                PLAY_ACTION -> {
                    playMusic()
                    isPlaying = true
                    updateNotification()
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
        mediaSession.release()
        removeNotification()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                R.drawable.pause, "Pause",
                getActionPendingIntent(PAUSE_ACTION)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.play, "Play",
                getActionPendingIntent(PLAY_ACTION)
            )
        }

        val stopAction = NotificationCompat.Action(
            R.drawable.stop, "Stop",
            getActionPendingIntent(STOP_FOREGROUND_ACTION)
        )

        val albumArtBitmap = BitmapFactory.decodeResource(resources, R.drawable.album_cover) // загружаем картинку альбома

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Ауырмайды Жүрек")
            .setContentText(if (isPlaying) "Playing Music" else "Paused")
            .setSmallIcon(R.drawable.music)
            .setLargeIcon(albumArtBitmap) // Добавляем обложку альбома
            .addAction(playPauseAction)
            .addAction(stopAction)
            .setStyle(MediaNotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(0, 1))
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getActionPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun playMusic() {
        if (requestAudioFocus()) {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                Log.d("MusicPlayerService", "Музыка начала воспроизводиться")
            }
        } else {
            Log.e("MusicPlayerService", "Не удалось получить аудиофокус")
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            Log.d("MusicPlayerService", "Музыка приостановлена")
        }
    }

    private fun stopMusic() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
            Log.d("MusicPlayerService", "Музыка остановлена и MediaPlayer освобожден")
        }
        isPlaying = false

        removeNotification()
        stopSelf()
    }

    private fun releaseMediaPlayer() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
            Log.d("MusicPlayerService", "MediaPlayer освобожден")
        }
    }

    private fun requestAudioFocus(): Boolean {
        val result = audioManager?.requestAudioFocus(
            null,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music player is running"
                setSound(null, null) // Отключаем звук уведомления
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            Log.d("MusicPlayerService", "Канал уведомлений создан")
        }
    }

    private fun removeNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID) // Удаляет уведомление
    }
}
