package com.dadachen.magicorientation.utils

import java.io.File
import java.io.RandomAccessFile


fun writeToLocalStorage(filePath: String,content:String){

    val file = File(filePath)
    if (!file.exists()){
        file.createNewFile()
    }
//    Log.d("csv files:", content)

    val raf = RandomAccessFile(file,"rwd")
    raf.seek((file.length()))
    raf.write(content.toByteArray())
    raf.close()
}