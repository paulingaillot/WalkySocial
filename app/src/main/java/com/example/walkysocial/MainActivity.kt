package com.example.walkysocial

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.walkysocial.Services.BluetoothService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Test Fonctionnement", "Allo c moi");
        val intent = Intent(applicationContext, BluetoothService::class.java)
        applicationContext.startForegroundService(intent)

        // Rendre l'appareil visible

        /*val requestCode = 1;
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivityForResult(discoverableIntent, requestCode)*/

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Register for broadcasts when a device is discovered.

        var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        var sbr = SingBroadcastReceiver()
        registerReceiver(sbr, filter)
        bluetoothAdapter.startDiscovery()


        // Recuperer Adresse mac de l'appareil en question

        val macAddress = bluetoothAdapter.address
        Log.d("MAC address", macAddress)

        Log.d("Test Fonctionnement", "Ca maaarche")
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private class SingBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            val action = intent.action //may need to chain this to a recognizing function
            Log.d("test", "Aller les bleus : $action")
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // Add the name and address to an array adapter to show in a Toast
                val derp =  device!!.address
                Log.d("test", "test "+derp)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
    }
}