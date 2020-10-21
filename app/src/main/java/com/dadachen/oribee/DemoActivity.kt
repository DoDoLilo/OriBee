package com.dadachen.oribee

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class DemoActivity : AppCompatActivity() {

    private val gameRot = FloatArray(4)
    private val rotVector = FloatArray(4)
    private lateinit var sensorManager: SensorManager
    private var rotVSensor: Sensor? = null
    private var gameRotSensor:Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private val rotl = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            rotVector[0] = p0!!.values[0]
            rotVector[1] = p0.values[1]
            rotVector[2] = p0.values[2]
            rotVector[3] = p0.values[3]
            runOnUiThread {
                tv_rot.text = "${rotVector[0]}\n${rotVector[1]}\n${rotVector[2]}\n${rotVector[3]}"

            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            Log.d("imu", "rot accuracy changed")
        }
    }
    private val gameRotl = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            gameRot[0] = p0!!.values[0]
            gameRot[1] = p0.values[1]
            gameRot[2] = p0.values[2]
            gameRot[3] = p0.values[3]

            runOnUiThread {
                tv_game.text = "${gameRot[0]}\n${gameRot[1]}\n${gameRot[2]}\n${gameRot[3]}"

            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            Log.d("imu", "rot accuracy changed")
        }
    }


    private fun initSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotVSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        gameRotSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        sensorManager.registerListener(rotl, rotVSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(gameRotl,gameRotSensor,SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onResume() {
        super.onResume()
        initSensor()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(gameRotl)
        sensorManager.unregisterListener(rotl)
        rotVSensor = null
        gameRotSensor = null
    }

//    override fun onStop() {
//        super.onStop()
//        sensorManager.unregisterListener(gameRotl)
//        sensorManager.unregisterListener(rotl)
//    }
}


