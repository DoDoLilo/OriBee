package com.dadachen.oribee.scan

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

abstract class ScanManager(val context: Context, val scanConfig: ScanConfig) {
    protected var running = true
    protected fun getTime(): String {
        return "${System.currentTimeMillis()+offset}"
    }

    protected var offset:Long = 0L

    abstract fun start(offset:Long)

    abstract fun getCount():Int
    abstract fun stop(personNumber: Int, countNumber: Int)
}

data class ScanConfig(var buildingName: String, var floor: String, var photoId: Int = 0)




