package com.example.tabatatimer.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.example.tabatatimer.Constants.ACTION_TIMER_STATE_CHANGED
import com.example.tabatatimer.Constants.NOTIFICATION_BROADCAST_ACTION
import com.example.tabatatimer.Constants.TIMER_ACTION_TYPE
import com.example.tabatatimer.Constants.TIMER_BROADCAST_ACTION
import com.example.tabatatimer.Constants.TIMER_CHANNEL_ID
import com.example.tabatatimer.Constants.TIMER_STARTED
import com.example.tabatatimer.Constants.TIMER_STOPPED
import com.example.tabatatimer.R
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import com.example.tabatatimer.screens.MainActivity
import kotlinx.coroutines.NonCancellable.start

class TimerService : Service() {
    var currentPos = MutableLiveData(0)
    var currentTimeRemaining = MutableLiveData(0)
    var currentPhase = MutableLiveData(TimerPhase.PREPARATION)
    var currentTimer = MutableLiveData<SequenceDbEntity>()
    var preparationRemaining = MutableLiveData(0)
    var workoutRemaining = MutableLiveData(0)
    var restRemaining = MutableLiveData(0)
    var cyclesRemaining = MutableLiveData(0)

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var timerReceiver: BroadcastReceiver

    private var currentTimerHandler: TimerHandler? = null
    private var currentPhaseRemaining = 0
    private var timerIsRunning = false

    override fun onCreate() {
        super.onCreate()

        timerReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.getStringExtra(ACTION_TIMER_STATE_CHANGED)) {
                    TIMER_STARTED -> start()
                    TIMER_STOPPED -> stop()
                }
            }
        }
        notificationManager = NotificationManagerCompat.from(this)
        registerReceiver(timerReceiver, IntentFilter(NOTIFICATION_BROADCAST_ACTION))
        // ringSound = MediaPlayer.create(this, R.raw.ring_sound)
    }

    fun setTimer(timer: SequenceDbEntity) {
        currentTimer.value = timer
        initService()
    }

    private fun initService() {
        currentTimeRemaining.value = (currentTimer.value!!.warmUpTime).toInt()
        preparationRemaining.value =
            (currentTimer.value!!.warmUpTime).toInt() //TODO Make migration and remove toInt
        workoutRemaining.value = (currentTimer.value!!.workoutTime).toInt()
        restRemaining.value = (currentTimer.value!!.restTime).toInt()
        cyclesRemaining.value = currentTimer.value!!.cycles
        currentPhaseRemaining = (currentTimer.value!!.warmUpTime).toInt()
    }

    fun start() {
        if (currentPhase.value == TimerPhase.FINISHED) {
            if (!selectPhase(TimerPhase.PREPARATION, withoutStopping = true)) {
                selectPhase(TimerPhase.WORKOUT, withoutStopping = true)
            }
            cyclesRemaining.value = currentTimer.value!!.cycles
        } else {
            currentTimerHandler = TimerHandler(currentPhaseRemaining.toLong() * 1000)
            currentTimerHandler!!.setEventCallback(TimerEventCallback())
            currentTimerHandler!!.start()
            timerIsRunning = true
            notifyTimerStarted()
        }
    }

    fun stop() {
        currentTimerHandler?.cancel()
        timerIsRunning = false
        showNotification()
        notifyTimerStopped()

    }

    fun selectPhase(phase: TimerPhase, withoutStopping: Boolean): Boolean {
        currentPhase.value = phase

        when (phase) {
            TimerPhase.PREPARATION -> {
                preparationRemaining.value = (currentTimer.value!!.warmUpTime).toInt()

                if (preparationRemaining.value!! == 0) {
                    return false
                }

                currentPhaseRemaining = (currentTimer.value!!.warmUpTime).toInt()
                currentTimeRemaining.value = currentPhaseRemaining
            }
            TimerPhase.WORKOUT -> {
                workoutRemaining.value = (currentTimer.value!!.workoutTime).toInt()
                currentPhaseRemaining = (currentTimer.value!!.workoutTime).toInt()
                currentTimeRemaining.value = currentPhaseRemaining
            }
            TimerPhase.REST -> {
                restRemaining.value = (currentTimer.value!!.restTime).toInt()

                if (restRemaining.value!! == 0) {
                    return false
                }

                currentPhaseRemaining = (currentTimer.value!!.restTime).toInt()
                currentTimeRemaining.value = currentPhaseRemaining
            }
            else -> {}
        }

        currentTimerHandler?.cancel()
        if (!withoutStopping) {
            stop()
        } else {
            start()
        }

        return true
    }

    fun nextPhase() {
        when (currentPhase.value) {
            TimerPhase.PREPARATION -> {
                selectPhase(TimerPhase.WORKOUT, withoutStopping = true)
            }
            TimerPhase.WORKOUT -> {
                if (!selectPhase(TimerPhase.REST, withoutStopping = true)) {
                    endTimer(withoutStopping = true)
                    stop()
                }
            }
            TimerPhase.REST -> {
                if (cyclesRemaining.value!! > 1) {
                    cyclesRemaining.value = cyclesRemaining.value!! - 1
                    selectPhase(TimerPhase.WORKOUT, withoutStopping = true)
                } else {
                    cyclesRemaining.value = 0
                    endTimer(withoutStopping = true)
                    stop()
                }
            }
            else -> {}
        }
        currentPos.value = currentPos.value!! + 1
    }

    fun endTimer(withoutStopping: Boolean) {
        selectPhase(TimerPhase.FINISHED, withoutStopping = false)
        stop()
        currentPos.value = -1
        notificationManager.cancel(1)
    }

    inner class TimerEventCallback : TimerHandler.EventCallback {
        override fun onStart() {
            notifyTimerStarted()
        }

        override fun onCancel() {}

        override fun onTick() {
            when (currentPhase.value!!) {
                TimerPhase.PREPARATION -> {
                    preparationRemaining.value = preparationRemaining.value!! - 1
                }
                TimerPhase.WORKOUT -> {
                    workoutRemaining.value = workoutRemaining.value!! - 1
                }
                TimerPhase.REST -> {
                    restRemaining.value = restRemaining.value!! - 1
                }
                else -> {}
            }
            showNotification()
            currentPhaseRemaining -= 1
            currentTimeRemaining.value = currentTimeRemaining.value!! - 1
        }

        override fun onFinish() {
            nextPhase()
            //ringSound.start()
        }
    }

    private fun notifyTimerStarted() {
        val intent = Intent(TIMER_BROADCAST_ACTION)
        intent.putExtra(TIMER_ACTION_TYPE, TIMER_STARTED)
        sendBroadcast(intent)
    }

    private fun notifyTimerStopped() {
        val intent = Intent(TIMER_BROADCAST_ACTION)
        intent.putExtra(TIMER_ACTION_TYPE, TIMER_STOPPED)
        sendBroadcast(intent)
    }


    fun showNotification() {

//        val activityIntent = Intent(this, MainActivity::class.java)
//        val activityPendingIntent = PendingIntent.getActivity(
//            this,
//            1,
//            activityIntent,
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
//        )

        val notification = NotificationCompat.Builder(this, TIMER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("${currentPhaseRemaining / 60}m ${currentPhaseRemaining % 60 +1}s")
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setOngoing(true)
            .setSilent(true)

        //   .setContentIntent(activityPendingIntent)
        if (timerIsRunning) {
            val intent = Intent(NOTIFICATION_BROADCAST_ACTION)
            intent.putExtra(ACTION_TIMER_STATE_CHANGED, TIMER_STOPPED)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                2,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT else 0
            )
            notification.addAction(R.drawable.ic_timer, "Stop", pendingIntent)
        } else {
            val intent = Intent(NOTIFICATION_BROADCAST_ACTION)
            intent.putExtra(ACTION_TIMER_STATE_CHANGED, TIMER_STARTED)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                2,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT else 0
            )
            notification.addAction(R.drawable.ic_timer, "Start", pendingIntent)
        }

        notificationManager.notify(1, notification.build())

    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
        notificationManager.cancel(1)
        unregisterReceiver(timerReceiver)
    }

    override fun onBind(intent: Intent): IBinder {
        return TimerServiceBinder()
    }

    inner class TimerServiceBinder : Binder() {
        fun getService() = this@TimerService
    }

}