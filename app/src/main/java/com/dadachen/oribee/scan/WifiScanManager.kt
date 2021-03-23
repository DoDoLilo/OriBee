package com.dadachen.oribee.scan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import com.dadachen.oribee.utils.writeToLocalStorage

class WifiScanManager(context: Context, scanConfig: ScanConfig) : ScanManager(context, scanConfig) {

    val wifiScanData = WifiScanData(
        BuildingID = "jiyuan",
        Date = "",
        FPscan = mutableListOf(),
        FPscanMode = "1",
        FloorID = "1"
    )
    private var pid = 0

    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                val wifiScanInfos = mutableListOf<WifiScanInfo>()
                var apId = 0
                wifiManager.scanResults.forEach {
                    val item = WifiScanInfo(
                        AP = apId++,
                        BSSID = it.BSSID,
                        SSID = it.SSID,
                        Level = it.level,
                        Date = getTime()
                    )
                    wifiScanInfos.add(item)
                }
                val fPscan = WifiFPscan(
                    Date = getTime(),
                    Point = pid++,
                    PosLatg = "0",
                    PosLong = "0",
                    PosOrientation = "0",
                    SlamX = "0",
                    SlamY = "0",
                    wifiScanInfos,
                    X = "0",
                    Y = "0"
                )
                wifiScanData.FPscan.add(fPscan)
                if (running) {
                    wifiManager.startScan()
                }
            }
        }

    }


    override fun start() {
        val intentFilter = IntentFilter()
        running = true
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)
        val success = wifiManager.startScan()
        wifiScanData.Date = getTime()
        if (!success) {
            //wifi scan failed
            Log.d("wifi scan","failure")
        }
    }

    override fun stop(personNumber:Int, countNumber:Int) {
        running = false
        context.unregisterReceiver(wifiScanReceiver)
        writeToLocalStorage("${context.externalCacheDir}/${scanConfig.buildingName}/WIFI-$personNumber-$countNumber-${Build.MODEL}.json", wifiScanData)
        wifiScanData.FPscan.clear()
    }

    override fun getCount(): Int {
        return pid
    }
}