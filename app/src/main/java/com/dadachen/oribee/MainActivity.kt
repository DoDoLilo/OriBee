package com.dadachen.oribee

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible
import com.dadachen.magicorientation.utils.writeToLocalStorage
import com.dadachen.oribee.time.getTimeByHttpClient
import com.dadachen.oribee.time.runServer
import com.dadachen.oribee.utils.Utils
import kotlinx.android.synthetic.main.activity_choose.*
import java.lang.StringBuilder
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val gyro = FloatArray(3)
    private val acc = FloatArray(3)
    private val rotVector = FloatArray(4)
    private lateinit var sensorManager: SensorManager
    private var rotVSensor: Sensor? = null
    private var accVSensor: Sensor? = null
    private var gyroVSensor: Sensor? = null
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        initSensor()
        initView()

    }
    private val rotl = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            rotVector[0] = p0!!.values[0]
            rotVector[1] = p0.values[1]
            rotVector[2] = p0.values[2]
            rotVector[3] = p0.values[3]
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            Log.d("imu", "rot accuracy changed")
        }
    }
    private val gyrol = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            gyro[0] = p0!!.values[0]
            gyro[1] = p0.values[1]
            gyro[2] = p0.values[2]
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            Log.d("imu", "gyro accuracy changed")
        }
    }
    private val accl = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            acc[0] = p0!!.values[0]
            acc[1] = p0.values[1]
            acc[2] = p0.values[2]
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            Log.d("imu", "acc accuracy changed")
        }
    }
    private fun initSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotVSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        accVSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroVSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)

        sensorManager.registerListener(rotl, rotVSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(accl, accVSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(gyrol, gyroVSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onResume() {
        super.onResume()
        initSensor()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(accl)
        sensorManager.unregisterListener(gyrol)
        sensorManager.unregisterListener(rotl)
    }
    private val freq = "freq"
    @SuppressLint("SetTextI18n")
    private fun initView() {
        bt_start_record.isEnabled = true
        bt_end_record.isEnabled = false
        bt_start_record.setOnClickListener {
            startRecord()
            bt_end_record.isEnabled = true
            bt_start_record.isEnabled = false
            seekBar_frequency.isEnabled = false
        }
        bt_end_record.setOnClickListener {
            endRecord()
            bt_start_record.isEnabled = true
            bt_end_record.isEnabled = false
            seekBar_frequency.isEnabled = true
        }

        //seek bar and its display textView initialization
        seekBar_frequency.progress = sharedPreferences.getInt(freq,200)
        frequency = seekBar_frequency.progress
        tv_freq.text = "$frequency Hz"
        seekBar_frequency.incrementProgressBy(10)
        seekBar_frequency.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                tv_freq.text = "$p1 Hz"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val ff = p0?.progress?:200
                frequency = ff
                Utils.setValueBySharedPreference(sharedPreferences,freq,p0!!.progress)
            }

        })
        //init

        //init time sync ui
        initTimeSyncUI()
    }
    private var timeOffset:Long = 0L
    @SuppressLint("SetTextI18n")
    private fun initTimeSyncUI() {
        tv_local_ip_address.text = Utils.getIPAddress(true)
        ev_remote_ip_address.setText(sharedPreferences.getString("ipn", ""))
        var ip = tv_local_ip_address.text.toString()
        bt_server_time.setOnClickListener {
            runServer()
            bt_server_time.text = "服务器已打开"
            bt_server_time.isEnabled = false
            bt_time_sync.isEnabled = false
            bt_time_sync.visibility = View.INVISIBLE
            ev_remote_ip_address.visibility = View.INVISIBLE
            ev_remote_ip_address.isEnabled = false
        }
        bt_time_sync.setOnClickListener {
            val num = ev_remote_ip_address.text.toString()
            Utils.setValueBySharedPreference(sharedPreferences,"ipn",num)
            ip = ip.substring(0,ip.indexOfLast{it=='.'}+1)
            ip += ev_remote_ip_address.text.toString()
            if (Utils.isIP(ip)) {
                val remoteTime = getTimeByHttpClient(ip)
                val localTime = System.currentTimeMillis()
                timeOffset =  remoteTime-localTime
                bt_server_time.isEnabled = false
                bt_server_time.visibility = View.INVISIBLE
                tv_offset_info.text="remote time: $remoteTime\nlocal time: $localTime\noffset: $timeOffset"
                Log.d("time","offet: $timeOffset")
            }else{
                Toast.makeText(this, "$ip is not valid", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private var recording = false
    private val stringBuilder = StringBuilder()
    private var frequency = 200
    private fun startRecord() {
        stringBuilder.clear()
        recording = true
        thread(start = true) {
            while (recording){
                val content = "${System.currentTimeMillis()+timeOffset},${acc[0]},${acc[1]},${acc[2]},${gyro[0]},${gyro[1]},${gyro[2]},${rotVector[0]},${rotVector[1]},${rotVector[2]},${rotVector[3]}"
                stringBuilder.appendLine(content)
                Thread.sleep((1000/frequency).toLong())
            }
        }
    }
    private fun endRecord() {
        recording = false
        writeToLocalStorage("$externalCacheDir/IMU-acc-gyro-rotv-${System.currentTimeMillis()}.csv",stringBuilder.toString())
    }
}