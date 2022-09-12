package com.example.tabatatimer.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.example.tabatatimer.model.room.entities.SequenceDbEntity

class TimerService : Service() {
    var currentPos=MutableLiveData(0)
    var currentTimeRemaining=MutableLiveData(0)
    var currentPhase = MutableLiveData(TimerPhase.PREPARATION)
    var currentTimer = MutableLiveData<SequenceDbEntity>()
    var preparationRemaining = MutableLiveData(0)
    var workoutRemaining = MutableLiveData(0)
    var restRemaining = MutableLiveData(0)
    var cyclesRemaining = MutableLiveData(0)

    private var currentTimerHandler: TimerHandler? = null
    private var currentPhaseRemaining = 0
    private var timerIsRunning = false


    fun setTimer(timer: SequenceDbEntity) {
        currentTimer.value = timer
        initService()
    }

    private fun initService() {
        currentTimeRemaining.value=(currentTimer.value!!.warmUpTime).toInt()
        preparationRemaining.value =
            (currentTimer.value!!.warmUpTime ).toInt() //TODO Make migration and remove toInt
        workoutRemaining.value = (currentTimer.value!!.workoutTime).toInt()
        restRemaining.value = (currentTimer.value!!.restTime ).toInt()
        cyclesRemaining.value = currentTimer.value!!.cycles
        currentPhaseRemaining = (currentTimer.value!!.warmUpTime).toInt()
    }

    fun start() {
        if (currentPhase.value == TimerPhase.FINISHED) {
            if (!selectPhase(TimerPhase.PREPARATION, withoutStopping = true)) {
                selectPhase(TimerPhase.WORKOUT, withoutStopping = true)
            }
            cyclesRemaining.value=currentTimer.value!!.cycles
        } else {
            currentTimerHandler = TimerHandler(currentPhaseRemaining.toLong() * 1000)
            currentTimerHandler!!.setEventCallback(TimerEventCallback())
            currentTimerHandler!!.start()
            timerIsRunning = true
        }
    }

    fun stop() {
        currentTimerHandler?.cancel()
        timerIsRunning = false

    }

    fun selectPhase(phase: TimerPhase, withoutStopping: Boolean): Boolean {
        currentPhase.value = phase

        when (phase) {
            TimerPhase.PREPARATION -> {
                preparationRemaining.value = (currentTimer.value!!.warmUpTime ).toInt()

                if (preparationRemaining.value!! == 0) {
                    return false
                }

                currentPhaseRemaining = (currentTimer.value!!.warmUpTime ).toInt()
                currentTimeRemaining.value=currentPhaseRemaining
            }
            TimerPhase.WORKOUT -> {
                workoutRemaining.value = (currentTimer.value!!.workoutTime ).toInt()
                currentPhaseRemaining = (currentTimer.value!!.workoutTime ).toInt()
                currentTimeRemaining.value=currentPhaseRemaining
            }
            TimerPhase.REST -> {
                restRemaining.value = (currentTimer.value!!.restTime ).toInt()

                if (restRemaining.value!! == 0) {
                    return false
                }

                currentPhaseRemaining = (currentTimer.value!!.restTime ).toInt()
                currentTimeRemaining.value=currentPhaseRemaining
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
                }
                else {
                    cyclesRemaining.value = 0
                    endTimer(withoutStopping = true)
                    stop()
                }
            }
            else -> {}
        }
        currentPos.value=currentPos.value!!+1
    }
    fun endTimer(withoutStopping: Boolean) {
            selectPhase(TimerPhase.FINISHED, withoutStopping = false)
            stop()
          //  notificationManager.cancel(NOTIFICATION_ID)
    }
    inner class TimerEventCallback : TimerHandler.EventCallback {
        override fun onStart() {

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

            currentPhaseRemaining -= 1
            currentTimeRemaining.value=currentTimeRemaining.value!!-1
        }

        override fun onFinish() {
            nextPhase()
            //ringSound.start()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        stop()
    }
    override fun onBind(intent: Intent): IBinder {
        return TimerServiceBinder()
    }

    inner class TimerServiceBinder : Binder() {
        fun getService() = this@TimerService
    }
}