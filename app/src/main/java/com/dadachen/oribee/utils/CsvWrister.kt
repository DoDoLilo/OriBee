package com.dadachen.oribee.utils

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