package com.dadachen.oribee.scan

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

abstract class ScanManager(val context: Context, val scanConfig: ScanConfig) {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    protected var running = true
    protected fun getTime(): String {
        return dateFormatter.format(Date()).toString()+";" + System.currentTimeMillis()
    }

    abstract fun start()
    open fun stop() {
        running = true
    }
    abstract fun getCount():Int
}

data class ScanConfig(var buildingName: String, var floor: String, var photoId: Int = 0)




