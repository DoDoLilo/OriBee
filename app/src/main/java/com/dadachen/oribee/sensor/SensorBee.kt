package com.dadachen.oribee.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.dadachen.oribee.utils.writeToLocalStorage
import java.lang.StringBuilder
import kotlin.concurrent.thread

fun sensorBee(sensorManager: SensorManager, init: SensorBee.() -> Unit): SensorBee {
    val sensorBee = SensorBee(sensorManager)
    sensorBee.init()
    return sensorBee
}

class SensorBee(private val sensorManager: SensorManager) {
    var frequency: Int = 200
    private val stringBuilder = StringBuilder()
    private val filePath: String = ""
    var headingAngles: Double = 0.0
    var offset = 0L
    fun sensorTypes(types: Array<Int>) {
        this.types = types
        sensors = types.map {
            sensorManager.getDefaultSensor(it)
        }
        statusMask = Array(types.size) { false }
    }

    private var types: Array<Int>? = null

    private var status = Status.STOPPING
    private lateinit var sensors: List<Sensor>

    private fun List<FloatArray>.toCsvString(): String {
        return joinToString(",") { it.joinToString(",") }
    }


    private var dataChangedListener: ((List<FloatArray>) -> Unit)? = null
    fun addDataChangedListener(listener: (data: List<FloatArray>) -> Unit) {
        dataChangedListener = listener
    }


    private fun start() {
        stringBuilder.clear()
        thread(start = true) {
            while (status == Status.Running) {
                val d = "${System.currentTimeMillis() + offset}, ${datas.toCsvString()}"
                Log.d("sensor", d)
                stringBuilder.appendLine(d)
                Thread.sleep((1000 / frequency).toLong())
            }
        }
    }

    private enum class Status {
        Running,
        STOPPING
    }

    fun startRecord(offset: Long) {
        status = Status.Running
        this.offset = offset
        start()
    }

    private lateinit var sensorListeners: List<SensorEventListener>
    private lateinit var datas: List<FloatArray>

    fun registerSensors() {
        datas = sensors.map {
            when (it.type) {
                Sensor.TYPE_ROTATION_VECTOR -> FloatArray(4)
                Sensor.TYPE_GAME_ROTATION_VECTOR -> FloatArray(4)
                else -> FloatArray(3)
            }
        }
        sensorListeners = sensors.mapIndexed { index, _ ->
            object : SensorEventListener {
                override fun onSensorChanged(p0: SensorEvent?) {
                    val item = datas[index]
                    for (i in item.indices) {
                        item[i] = p0!!.values[i]
                    }
                    dataChangedListener?.let { it(datas) }

                    //change mask to present the specific sensor ready
                    statusMask[index] = true
                    //only execute following if clause, when just registering Mag and acc
                    if (!isRegistered && statusMask[getIndexFromSensorType(Sensor.TYPE_MAGNETIC_FIELD)] && statusMask[getIndexFromSensorType(
                            Sensor.TYPE_ACCELEROMETER
                        )]
                    ) {
                        calculateHeadingAngles()
                        isRegistered = true
                    }
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                }
            }
        }
        //register sensors
        sensors.forEachIndexed { index, sensor ->
            sensorManager.registerListener(
                sensorListeners[index],
                sensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    private fun calculateHeadingAngles() {
        val rot = FloatArray(9)
        val value = FloatArray(4)
        SensorManager.getRotationMatrix(
            rot,
            null,
            datas.getDataFromSensorType(Sensor.TYPE_ACCELEROMETER),
            datas.getDataFromSensorType(Sensor.TYPE_MAGNETIC_FIELD)
        )
        SensorManager.getOrientation(rot, value)
        headingAngles = Math.toDegrees(value[0].toDouble())
        if (headingAngles < 0) {
            headingAngles += 360
        }
    }

    private var isRegistered = false
    private lateinit var statusMask: Array<Boolean>
    fun resetSensors() {
        unregisterSensors()
        registerSensors()
    }

    fun stopSensors() {
        unregisterSensors()
    }

    private fun unregisterSensors() {
        sensorListeners.forEach {
            sensorManager.unregisterListener(it)
        }
        isRegistered = false
        statusMask.fill(false)
    }

    fun stopRecordAndSave(filePath: String = this.filePath) {
        status = Status.STOPPING
        writeToLocalStorage(filePath, stringBuilder.toString())
    }


    private fun List<FloatArray>.getDataFromSensorType(type: Int): FloatArray {
        return this[getIndexFromSensorType(type)]
    }

    private fun getIndexFromSensorType(type: Int): Int {
        return types?.indexOf(type) ?: -1
    }


}