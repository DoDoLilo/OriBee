package com.dadachen.oribee.scan

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.dadachen.oribee.utils.writeToLocalStorage

class MagScanManager(context: Context, scanConfig: ScanConfig):ScanManager(context, scanConfig) {
    private val d2R = 0.0174532925199433 //(PI/180)
    private var sys_t0 = 0L
    override fun start() {
        stringBuilder.clear()
        sys_t0 = System.currentTimeMillis()
        initSensor()
    }

    override fun stop() {
        super.stop()
        stopSensor()
        writeToLocalStorage("${context.externalCacheDir}/${scanConfig.buildingName}/floor_${scanConfig.floor}_Mag_${System.currentTimeMillis()}.csv",stringBuilder.toString())
    }

    override fun getCount(): Int {
        return 0
    }

    private val stringBuilder = StringBuilder()
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private fun initSensor(){
        val gSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val muSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)
        sensorManager.registerListener(mySensorListener,gSensor,SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(mySensorListener,mSensor,SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(mySensorListener,aSensor,SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(mySensorListener,muSensor,SensorManager.SENSOR_DELAY_FASTEST)
    }

    private fun stopSensor(){
        sensorManager.unregisterListener(mySensorListener)
    }


    private val mySensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_GYROSCOPE -> {
                    val temp_gyro = event.values.clone()
                    val t_gyro: Double = System.currentTimeMillis() / 1000.0 - sys_t0
                    val str_gyro =  String.format(
                        "1,%.3f,%.3f,%.3f,%.3f\n",
                        t_gyro,
                        temp_gyro[1] / d2R,
                        temp_gyro[0] / d2R,
                        -temp_gyro[2] / d2R
                    )
                    stringBuilder.append(str_gyro)
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    val temp_acc = event.values.clone()
                    val t_acc: Double = System.currentTimeMillis() / 1000.0 - sys_t0
                    val str_acc = String.format(
                        "2,%.3f,%.3f,%.3f,%.3f\n", t_acc,
                        temp_acc[1], temp_acc[0], -temp_acc[2]
                    )
                    stringBuilder.append(str_acc)

                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    val temp_mag = event.values.clone()
                    val t_mag: Double = System.currentTimeMillis() / 1000.0 - sys_t0
                    val str_mag = String.format(
                        "3,%.3f,%.3f,%.3f,%.3f\n", t_mag,
                        temp_mag[1], temp_mag[0], -temp_mag[2]
                    )
                    stringBuilder.append(str_mag)

                }
                Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> {
                    val temp_unmag = event.values.clone()
                    val t_unmag: Double = System.currentTimeMillis() / 1000.0 - sys_t0
                    val str_unmag = String.format(
                        "14,%.3f,%.3f,%.3f,%.3f\n", t_unmag,
                        temp_unmag[1], temp_unmag[0], -temp_unmag[2]
                    )
                    val bytes_unmag = str_unmag.toByteArray()
                    stringBuilder.append(bytes_unmag)

                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, i: Int) {}
    }


}