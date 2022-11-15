package com.skillbox.aslanbolurov.countdowntimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import com.google.android.material.slider.Slider
import com.skillbox.aslanbolurov.countdowntimer.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.concurrent.timer
import kotlinx.coroutines.currentCoroutineContext as currentCoroutineContext1

const val MAX = 60
const val MIN = 0
const val STOP = 0
const val START = 1


val isContinue: Boolean
    get() = if (!isTimerFinish) true else false
var isTimerFinish: Boolean = false
var isRotate: Boolean = false

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val btnState
        get() = if (btnStart.text=="Start") START else STOP
//        set(value) { field=value}
    lateinit var progressBar: ProgressBar
    lateinit var btnStart: Button
    lateinit var slider: Slider
    lateinit var tvCounter: TextView

    private var scope = CoroutineScope(Job() + Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar
        btnStart = binding.btnStartPause
        slider = binding.slider
        tvCounter = binding.tvCounter

        Log.d("TAG", btnState.toString())

        savedInstanceState?.let { bundle ->
//            btnState = bundle.getInt("btnState")
            btnStart.text = bundle.getString("btnText")
            tvCounter.text = bundle.getInt("tvCounterValue").toString()
            slider.isEnabled = bundle.getBoolean("isEnableSlider")
            Log.d("Bul", bundle.getInt("tvCounterValue").toString())
        }

        btnStart.setOnClickListener {
            doWork()
//            changeBtnState()
        }


        slider.addOnChangeListener { _, value, _ ->
            if (!isRotate) {
//                isContinue = false
                binding.tvCounter.setText(slider.value.toInt().toString())
                progressBar.progress = value.toInt()
            }
        }


    }

//    private fun changeBtnState() {
//        when(btnState){
//            START -> btnState= STOP
//            STOP -> btnState= START
//        }
//    }

    override fun onResume() {
        super.onResume()
        if (isRotate && isContinue) {
            doWork()
            isRotate = false
        } else
            isRotate = false
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        if (isContinue) btnState = START else btnState = STOP
//        outState.putInt("btnState", btnState)
        outState.putString("btnText", btnStart.text.toString())
        outState.putBoolean("isEnableSlider", slider.isEnabled)
        outState.putInt("tvCounterValue", tvCounter.text.toString().toInt())
    }

    override fun onDestroy() {
        super.onDestroy()
        isRotate = true
    }


    private fun doWork() {
        var sliderValue = slider.value.toInt()
        when (btnState) {
            if(!isRotate) START else STOP -> {

                if (isContinue) {
                    sliderValue = tvCounter.text.toString().toInt()
                }
                scope = CoroutineScope(Job() + Dispatchers.IO)
                scope.launch {
                    timerStart(sliderValue)
                }

//                isContinue=true
                slider.isEnabled = false
                btnStart.setText(R.string.btnStop)

            }
            if(!isRotate) STOP else START -> {
                scope.cancel()
//                isContinue = false
                slider.isEnabled = true
                btnStart.setText(R.string.btnStart)
            }

        }
    }


    suspend fun timerStart(seconds: Int) {

        (seconds downTo 0).forEach {
            progressBar.progress = it
            tvCounter.setText(it.toString())
            delay(1000)
            if (it == 0) {
                isTimerFinish = true
                btnStart.setText(R.string.btnStart)
            } else isTimerFinish = false
        }
    }

}