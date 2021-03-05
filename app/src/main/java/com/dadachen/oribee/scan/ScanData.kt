package com.dadachen.oribee.scan


abstract class ScanData

data class WifiScanData(
    val BuildingID: String,
    var Date: String,
    val FPscan: MutableList<WifiFPscan>,
    val FPscanMode: String,
    val FloorID: String
): ScanData(){
    override fun toString(): String {
        return "WifiScanData [Building ID='$BuildingID', Floor ID='$FloorID', Date='$Date', FPscanMode='$FPscanMode', FPscan=$FPscan]"
    }
}

data class WifiFPscan(
    var Date: String,
    val Point: Int,
    val PosLatg: String,
    val PosLong: String,
    val PosOrientation: String,
    val SlamX: String,
    val SlamY: String,
    val WifiScanInfo: List<WifiScanInfo>,
    val X: String,
    val Y: String
){
    override fun toString(): String {
        return "FPscan [Date='$Date', Point=$Point, PosLatg='$PosLatg', PosLong='$PosLong', PosOrientation='$PosOrientation', SlamX='$SlamX', SlamY='$SlamY', WifiScanInfo=$WifiScanInfo, X='$X', Y='$Y']"
    }
}

data class WifiScanInfo(
    val AP: Int,
    val BSSID: String,
    var Date: String,
    val Level: Int,
    val SSID: String
){
    override fun toString(): String {
        return "WifiScanInfo [AP=$AP, BSSID='$BSSID', Date='$Date', Level=$Level, SSID='$SSID']"
    }
}

data class BLEScanData(
    val BuildingID: String,
    var Date: String,
    val FPscan: MutableList<BLEFPscan>,
    val FPscanMode: String,
    val FloorID: String
): ScanData(){
    override fun toString(): String {
        return "BLEScanData [Building ID='$BuildingID', Date='$Date', FPscan=$FPscan, FPscanMode='$FPscanMode', Floor ID='$FloorID']"
    }
}

data class BLEFPscan(
    var APcount: Int,
    var APtruecount: Int,
    val BLEScanInfo: MutableList<BLEScanInfo>,
    val BLEScanInfoRaw: MutableList<BLEScanInfoRaw>,
    var Date: String,
    val Point: Int,
    val PosLatg: String,
    val PosLong: String,
    var PosOrientation: String,
    val SlamX: String,
    val SlamY: String,
    val X: String,
    val Y: String
){
    override fun toString(): String {
        return "FPscan [APcount=$APcount, APtruecount=$APtruecount, BLEScanInfo=$BLEScanInfo, BLEScanInfoRaw=$BLEScanInfoRaw, Date='$Date', Point=$Point, PosLatg='$PosLatg', PosLong='$PosLong', PosOrientation='$PosOrientation', SlamX='$SlamX', SlamY='$SlamY', X='$X', Y='$Y']"
    }
}

data class BLEScanInfo(
    val AP: Int,
    var Date: String,
    val Level: Int,
    val MAC: String,
    val Name: String
){
    override fun toString(): String {
        return "BLEScanInfo [AP=$AP, Date='$Date', Level=$Level, MAC='$MAC', Name='$Name']"
    }
}

data class BLEScanInfoRaw(
    val AP: Int,
    var Date: String,
    val Level: Int,
    val MAC: String,
    val Name: String
){
    override fun toString(): String {
        return "BLEScanInfoRaw [AP=$AP, Date='$Date', Level=$Level, MAC='$MAC', Name='$Name']"
    }
}