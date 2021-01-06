package com.dadachen.oribee

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.dadachen.oribee.utils.writeToLocalStorage
import java.lang.StringBuilder
import kotlin.concurrent.thread
import kotlin.properties.Delegates

fun sensorBee(sensorManager: SensorManager,init: SensorBee.()->Unit):SensorBee{
    val sensorBee = SensorBee(sensorManager)
    sensorBee.init()
    return sensorBee
}
class SensorBee(private val sensorManager: SensorManager){
    var frequency:Double = 200.0
    private val stringBuilder = StringBuilder()

    fun sensorTypes(types:Array<Int>) {
        this.types = types
        sensors = types.map {
            sensorManager.getDefaultSensor(it)
        }
    }
    private var types:Array<Int>? = null

    private var status  = Status.STOPPING
    private lateinit var sensors:List<Sensor>

    private fun start() {
        stringBuilder.clear()
        thread(start = true) {

            Thread.sleep((1000/frequency).toLong())
        }
    }

    private enum class Status {
        Running,
        STOPPING
    }

    fun startRecord() {
        status = Status.Running
        registerSensors()
        start()
    }

    private lateinit var sensorListeners:List<SensorEventListener>
    private lateinit var datas:List<FloatArray>

    private fun registerSensors() {
        datas = sensors.map {
            when(it.type){
                Sensor.TYPE_ROTATION_VECTOR -> FloatArray(4)
                Sensor.TYPE_GAME_ROTATION_VECTOR -> FloatArray(4)
                else -> FloatArray(3)
            }
        }
        sensorListeners = sensors.mapIndexed { index, sensor ->
            object :SensorEventListener{
                override fun onSensorChanged(p0: SensorEvent?) {
                   val item = datas[index]
                    for (i in item.indices) {
                        item[i] = p0!!.values[i]
                    }
                }
                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                }
            }
        }
        //register sensors
        sensors.forEach {

        }
    }

    fun stopRecordAndSave(filePath:String) {
        status = Status.STOPPING
        writeToLocalStorage(filePath, stringBuilder.toString())
    }

}