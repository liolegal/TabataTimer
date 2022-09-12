package com.example.tabatatimer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import com.example.tabatatimer.services.TimerPhase

class TimerViewModel :ViewModel(){
    var currentPos=MutableLiveData(0)
    var currentTimeRemaining=MutableLiveData(0)
    var currentTimer = MutableLiveData<SequenceDbEntity>()
    var currentPhase = MutableLiveData(TimerPhase.PREPARATION)
    var preparationRemaining = MutableLiveData(0)
    var workoutRemaining = MutableLiveData(0)
    var restRemaining = MutableLiveData(0)
    var cyclesRemaining = MutableLiveData(0)

}