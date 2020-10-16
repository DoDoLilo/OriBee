package com.dadachen.oribee.time

import android.content.Context
import android.net.Uri
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.AsyncHttpRequest
import com.koushikdutta.async.http.AsyncHttpResponse
import com.koushikdutta.async.http.server.AsyncHttpServer
import java.lang.Exception
import kotlin.concurrent.thread

const val port = 5000

fun runServer(){
    thread(start = true) {
        val server = AsyncHttpServer()
        server.get("/"){ _, response->
            response.send("${System.currentTimeMillis()}")
        }
        server.listen(port)
    }
}

val client: AsyncHttpClient = AsyncHttpClient.getDefaultInstance()
fun getTimeByHttpClient(url:String):Long{
    val f = client.executeString(AsyncHttpRequest(Uri.parse(url),"get"), object: AsyncHttpClient.StringCallback(){
        override fun onCompleted(e: Exception?, source: AsyncHttpResponse?, result: String?) {

        }
    })
    return f.get().toLong()
}

