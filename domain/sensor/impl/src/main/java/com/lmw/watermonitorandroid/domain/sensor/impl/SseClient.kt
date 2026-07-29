package com.lmw.watermonitorandroid.domain.sensor.impl

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SseClient @Inject constructor(
    client: OkHttpClient
) {
    private val sseClient: OkHttpClient = client.newBuilder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .addNetworkInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            if ("text/event-stream" == response.header("content-type")) {
                Log.d("SSE", "SSE stream opened, skipping logging for this connection")
            }
            response
        }
        .build()

    fun connect(url: String): Flow<String> = callbackFlow {
        val request = Request.Builder()
            .url(url)
            .header("Accept", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .build()
        val factory = EventSources.createFactory(sseClient)

        Log.d("SSE", "connecting to $url")

        val eventSource = factory.newEventSource(request, object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: okhttp3.Response) {
                Log.d("SSE", "onOpen: connection established")
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                Log.d("SSE", "onEvent: id=$id type=$type data=$data")
                trySend(data)
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: okhttp3.Response?) {
                Log.e("SSE", "onFailure: ${t?.message} response=${response?.code}", t)
                close(t ?: java.io.IOException("SSE connection failed"))
            }

            override fun onClosed(eventSource: EventSource) {
                Log.d("SSE", "onClosed")
                close()
            }
        })

        awaitClose { eventSource.cancel() }
    }
}