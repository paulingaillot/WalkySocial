package com.example.walkysocial

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder {
        Log.d("Test Fonctionnement", "Ca maaarche");
        TODO("Return the communication channel to the service.")
    }
}