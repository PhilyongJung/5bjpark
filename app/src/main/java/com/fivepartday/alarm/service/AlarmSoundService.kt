package com.fivepartday.alarm.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.fivepartday.alarm.data.UserPreferencesRepository
import com.fivepartday.alarm.scheduler.AlarmSchedulerImpl
import com.fivepartday.alarm.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmSoundService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val notification = NotificationHelper.buildServiceNotification(this).build()
        startForeground(NotificationHelper.SERVICE_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val licensePlate = intent?.getStringExtra("license_plate") ?: ""

        startAlarmSound()
        startVibration()

        // Send broadcast to show alarm ring screen
        val showIntent = Intent("com.fivepartday.alarm.SHOW_ALARM").apply {
            putExtra("license_plate", licensePlate)
            setPackage(packageName)
        }
        sendBroadcast(showIntent)

        // Reschedule next alarm
        CoroutineScope(Dispatchers.IO).launch {
            val repo = UserPreferencesRepository(this@AlarmSoundService)
            val prefs = repo.userPreferences.first()
            if (prefs.isAlarmEnabled) {
                val scheduler = AlarmSchedulerImpl(this@AlarmSoundService)
                scheduler.scheduleFivePartDayAlarms(prefs.licensePlate, prefs.fivePartDayAlarms)
            }
        }

        return START_NOT_STICKY
    }

    private fun startAlarmSound() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmSoundService, alarmUri)
                isLooping = true
                prepare()
                start()
            }
        } catch (_: Exception) {
            // Fallback: no sound
        }
    }

    private fun startVibration() {
        vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    override fun onDestroy() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
        super.onDestroy()
    }
}
