package com.dadachen.oribee

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dadachen.oribee.sensor.SensorBee
import com.dadachen.oribee.sensor.sensorBee
import com.dadachen.oribee.time.getTimeByHttpClient
import com.dadachen.oribee.time.runServer
import com.dadachen.oribee.utils.Utils
import kotlinx.android.synthetic.main.activity_choose.*

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sensorBee: SensorBee
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        initView()
        checkAuth(this)
        sensorBee = sensorBee(sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager){
            frequency = 200
            sensorTypes(
                arrayOf(
                    Sensor.TYPE_ACCELEROMETER,
                    Sensor.TYPE_GYROSCOPE,
                    Sensor.TYPE_GAME_ROTATION_VECTOR,
                    Sensor.TYPE_ROTATION_VECTOR,
                    Sensor.TYPE_GYROSCOPE_UNCALIBRATED,
                    Sensor.TYPE_ORIENTATION,
                    Sensor.TYPE_MAGNETIC_FIELD,
                    Sensor.TYPE_GRAVITY,
                    Sensor.TYPE_LINEAR_ACCELERATION
            ))
            registerSensors()

            addDataChangedListener {
                runOnUiThread {
                    //note the order and the value location should depend on the sensor types defined above
                    tv_rotation_vector_4.text = it[2][3].toString()
                }
            }
        }
    }


    private var isStart = false

    @SuppressLint("CommitPrefEdits", "SetTextI18n")
    private fun initView() {
        bt_start_record.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle(if (!isStart) R.string.start_record else R.string.end_record)
                setPositiveButton(R.string.dialog_ok) { _, _ ->
                    if (!isStart) {

                        sensorBee.startRecord(timeOffset)
                        bt_start_record.setBackgroundColor(Color.RED)
                        bt_start_record.text = getString(R.string.end_record)
                        personNumber = ev_person.text.toString().toInt()
                        countNumber = ev_count.text.toString().toInt()
                        sharedPreferences.edit().putInt("person", personNumber).putInt("count", countNumber).apply()
                        isStart = true
                    } else {
                        sensorBee.stopRecordAndSave("${externalCacheDir}/IMU-${personNumber}-$countNumber-${sensorBee.headingAngles} ${Build.MODEL}.csv")
                        bt_start_record.setBackgroundColor(Color.GRAY)
                        isStart = false
                        bt_start_record.text = getString(R.string.start_record)
                    }
                }
                setNegativeButton(R.string.dialog_cancel) { _, _ ->

                }
            }.create().show()
        }

        //init

        //init time sync ui
        initTimeSyncUI()
        timeOffset = sharedPreferences.getInt("offset", 0).toLong()
        tv_offset_info.text = "offset: $timeOffset"

        //for obtaining data
        personNumber = sharedPreferences.getInt("person", 0)
        ev_person.setText(personNumber.toString())
        countNumber = sharedPreferences.getInt("count", 0)
        ev_count.setText(countNumber.toString())


        //init reset bt
        bt_reset_sensor.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("是否重置sensor")
                setPositiveButton("重置") { _, _ ->
                    sensorBee.resetSensors()
                }
                setNegativeButton("取消") { _, _ ->
                    //do nothing
                }
            }.create().show()
        }
    }

    private var timeOffset: Long = 0L

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
            Utils.setValueBySharedPreference(sharedPreferences, "ipn", num)
            ip = ip.substring(0, ip.indexOfLast { it == '.' } + 1)
            ip += ev_remote_ip_address.text.toString()
            if (Utils.isIP(ip)) {
                val remoteTime = getTimeByHttpClient(ip)
                val localTime = System.currentTimeMillis()
                timeOffset = remoteTime - localTime
                bt_server_time.isEnabled = false
                bt_server_time.visibility = View.INVISIBLE
                tv_offset_info.text = "offset: $timeOffset"
                Utils.setValueBySharedPreference(sharedPreferences, "offset", timeOffset.toInt())
                Log.d("time", "offet: $timeOffset")
            } else {
                Toast.makeText(this, "$ip is not valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var personNumber = 0
    private var countNumber = 0


    private fun  checkAuth(activity: Activity?) {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.INTERNET
                ), 1
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorBee.stopSensors()
    }
}