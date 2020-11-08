package com.dadachen.oribee

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dadachen.oribee.utils.writeToLocalStorage
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
        initView()

    }
    private val rotl = object : SensorEventListener {
        @SuppressLint("SetTextI18n")
        override fun onSensorChanged(p0: SensorEvent?) {
            rotVector[0] = p0!!.values[0]
            rotVector[1] = p0.values[1]
            rotVector[2] = p0.values[2]
            rotVector[3] = p0.values[3]
            tv_rot_vector.text = "${rotVector[0]}\n${rotVector[1]}\n${rotVector[2]}\n${rotVector[3]}"
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
    private var isStart = false
    private fun initView() {
        bt_start_record.setOnClickListener {
            val dialog = let {
                AlertDialog.Builder(this).apply {
                    setTitle(if(!isStart) R.string.start_record else R.string.end_record)
                    setPositiveButton(R.string.dialog_ok){ _, _ ->
                        if (!isStart) {
                            startRecord()
                            bt_start_record.setBackgroundColor(Color.RED)
                            bt_start_record.text = getString(R.string.end_record)
                        } else {
                            endRecord()
                            bt_start_record.setBackgroundColor(Color.GRAY)

                            bt_start_record.text = getString(R.string.start_record)
                        }
                        isStart = !isStart
                    }
                    setNegativeButton(R.string.dialog_cancel){ _, _ ->

                    }
                }.create()
            }.show()
        }

        //init

        //init time sync ui
        initTimeSyncUI()
        timeOffset = sharedPreferences.getInt("offset",0).toLong()
        tv_offset_info.text="offset: $timeOffset"

        //for obtaining data
        personNumber = sharedPreferences.getInt("person", 0)
        ev_person.setText(personNumber.toString())
        countNumber = sharedPreferences.getInt("count", 0)
        ev_count.setText(countNumber.toString())
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
                tv_offset_info.text="offset: $timeOffset"
                Utils.setValueBySharedPreference(sharedPreferences,"offset",timeOffset.toInt())
                Log.d("time","offet: $timeOffset")
            }else{
                Toast.makeText(this, "$ip is not valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var personNumber = 0
    private var countNumber = 0

    private var recording = false
    private val stringBuilder = StringBuilder()
    private var frequency = 200
    private fun startRecord() {
        stringBuilder.clear()
        recording = true
        personNumber = ev_person.text.toString().toInt()
        countNumber = ev_count.text.toString().toInt()

        Utils.setValueBySharedPreference(sharedPreferences,"person", personNumber)
        Utils.setValueBySharedPreference(sharedPreferences, "count", countNumber)
        Toast.makeText(this, "开始采集", Toast.LENGTH_SHORT).show()
        thread(start = true) {
            while (recording){
                val content = "${System.currentTimeMillis()+timeOffset},${acc[0]},${acc[1]},${acc[2]},${gyro[0]},${gyro[1]},${gyro[2]},${rotVector[0]},${rotVector[1]},${rotVector[2]},${rotVector[3]}"
                stringBuilder.appendLine(content)
                Thread.sleep(5L)
            }
        }
    }
    private fun endRecord() {
        recording = false
        val deviceName = Build.MODEL
        Log.d("device","name is $deviceName")
        Toast.makeText(this, "采集成功", Toast.LENGTH_SHORT).show()
        writeToLocalStorage("$externalCacheDir/IMU-${personNumber}-${countNumber}-$deviceName.csv",stringBuilder.toString())
    }
}