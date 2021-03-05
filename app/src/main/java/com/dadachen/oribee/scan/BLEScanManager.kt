package com.dadachen.oribee.scan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import com.dadachen.oribee.utils.writeToLocalStorage
import java.util.*

class BLEScanManager(context: Context, scanConfig: ScanConfig) : ScanManager(context, scanConfig) {
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var pid = 0
    private var count = 0
    private var newRecord = true
    val bleScanData = BLEScanData(
        BuildingID = "shi",
        Date = "",
        FPscan = mutableListOf(),
        FPscanMode = "2",
        FloorID = ""
    )
    private lateinit var tempBle: BLEFPscan
    override fun start() {
        Log.d("ble scan", "start")
        bleScanData.Date = getTime()
        bluetoothAdapter.bluetoothLeScanner.startScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                if (newRecord) {
                    if(count>0){
                        bleScanData.FPscan.add(tempBle)
                    }
                    count = 0
                    tempBle = BLEFPscan(
                        0,
                        0,
                        mutableListOf(),
                        mutableListOf(),
                        getTime(),
                        pid++,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                    newRecord = false
                }
                val level = result?.rssi ?: 200
                val mac = result?.device?.address ?: ""
                val name = result?.device?.name?:""
                val time = getTime()
                if(level<70){
                    tempBle.BLEScanInfo.add(BLEScanInfo(count,time,level,mac,name))
                    tempBle.APtruecount++
                }
                tempBle.BLEScanInfoRaw.add(BLEScanInfoRaw(count,time,level,mac, name))
                tempBle.APcount++
                count++
                Log.d(
                    "ble scan",
                    "device mac: ${result?.device?.address}, rssi: ${result?.rssi}"
                )

            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d("ble scan", "failure")
            }

        })
        Timer().schedule(object : TimerTask() {
            override fun run() {
                newRecord = true
            }
        }, 0L,2000L)
    }

    override fun stop() {
        super.stop()
        bluetoothAdapter.bluetoothLeScanner.stopScan(object : ScanCallback(){})
        writeToLocalStorage("${context.externalCacheDir}/${scanConfig.buildingName}/floor_${scanConfig.floor}/BLElinescan.json", bleScanData)
    }

    override fun getCount(): Int {
        return pid
    }
}