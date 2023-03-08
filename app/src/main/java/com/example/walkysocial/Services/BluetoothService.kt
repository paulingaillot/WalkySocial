package com.example.walkysocial.Services

import android.Manifest
import android.R
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat


class BluetoothService : Service() {


    override fun onBind(intent: Intent?): IBinder? {
        Log.d("test", "pitié")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("test", "pitié")
        // Ajoutez votre logique de création de service ici

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Test Fonctionnement", "Ca maarche")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "channel_id",
            "My Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "channel_id")
            .setSmallIcon(R.drawable.ic_notification_overlay)
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line..."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        val notification = builder.build()

        startForeground(1234, notification)

         val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return START_STICKY_COMPATIBILITY
        }
        var list = bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()

        if(list.isEmpty()) {
            Log.d("test", "vide")
        }
        for(item in list) {
            Log.d("test", item.toString())
        }

        // Ajoutez votre logique de service ici
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("test", "pitié")
        super.onDestroy()
        // Ajoutez votre logique de destruction de service ici
    }

}