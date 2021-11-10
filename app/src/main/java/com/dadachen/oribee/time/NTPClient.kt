package com.dadachen.oribee.time

import com.instacart.library.truetime.TrueTimeRx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

object NTPClient {
    lateinit var dispose: Disposable
    fun sync(ipAddress:String,dateHandler:(Long,Long)->Unit){
        dispose = TrueTimeRx.build()
            .initializeRx(ipAddress)
            .subscribeOn(Schedulers.io())
            .subscribe ({ date ->
                dateHandler(date.time, System.currentTimeMillis())
            }, {throwable->
                throwable.printStackTrace()
        })
    }

    fun stop(){
        dispose.dispose()
    }
}