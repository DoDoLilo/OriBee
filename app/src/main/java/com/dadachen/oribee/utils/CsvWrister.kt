package com.dadachen.oribee.utils

import com.dadachen.oribee.scan.ScanData
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter


fun writeToLocalStorage(filePath: String, content: String) {
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
