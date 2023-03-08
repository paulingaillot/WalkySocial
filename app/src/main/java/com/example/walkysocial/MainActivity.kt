package com.example.walkysocial

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.walkysocial.Services.BluetoothService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Test Fonctionnement", "Ca marche");
        val intent = Intent(applicationContext, BluetoothService::class.java)
        applicationContext.startForegroundService(intent)
    }
}