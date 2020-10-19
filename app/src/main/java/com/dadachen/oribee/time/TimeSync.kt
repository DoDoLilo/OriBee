package com.dadachen.oribee.time

import android.net.Uri
import android.util.Log
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.AsyncHttpGet
import com.koushikdutta.async.http.AsyncHttpRequest
import com.koushikdutta.async.http.AsyncHttpResponse
import com.koushikdutta.async.http.server.AsyncHttpServer
import java.util.concurrent.TimeUnit

const val port = 10002

fun runServer() {
    val server = AsyncHttpServer()
    server.get("/") { _, response ->
        response.send("${System.currentTimeMillis()}")
        Log.d("times", "send times")
    }
    server.listen(port)
}

val client: AsyncHttpClient = AsyncHttpClient.getDefaultInstance()
fun getTimeByHttpClient(url: String): Long {
    val s = "http://$url:$port"
    val f = client.executeString(
        AsyncHttpGet(Uri.parse(s)),
        object : AsyncHttpClient.StringCallback() {
            override fun onCompleted(e: Exception?, source: AsyncHttpResponse?, result: String?) {
                Log.d("times", "${source?.message()}")
            }
        })
    return f.get(1, TimeUnit.MINUTES).toLong()
}

