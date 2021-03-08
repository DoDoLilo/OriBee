package com.dadachen.oribee.utils

import android.util.Log
import com.dadachen.oribee.scan.ScanData
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter


fun writeToLocalStorage(filePath: String, content: String) {
    val dir = filePath.substring(0, filePath.indexOfLast { it == '/' })
    Log.d("csv","write path dir: $dir")
    val f = File(dir)
    if (!f.exists()) {
        f.mkdirs()
    }
    val file = File(filePath)
    if (!file.exists()) {
        file.createNewFile()
    }
    val out = FileWriter(file)
    out.write(content)
    out.flush()
    out.close()
}

fun writeToLocalStorage(filePath: String, content: ScanData){
    val temp = toJsonInString(scanData = content)
    writeToLocalStorage(filePath, temp)
}
fun toJsonInString(scanData: ScanData):String = Gson().toJson(scanData)
