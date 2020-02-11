package com.meliodas.detectappruning

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import java.io.BufferedReader
import java.io.InputStreamReader


class ExampleService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val tempC = intent.getFloatExtra("inputExtra", 0f)
        val tempF = (tempC.toFloat() * 9 / 5) + 32
        val tempCPUC = Utils.cpuTemperature()
        val tempCPUF = (tempCPUC * 9 / 5) + 32

        val textTemp = "${tempC}C/${tempF}F \n Nhiệt độ pin"
        val textTempCPU = "${tempCPUC}C/${tempCPUF}F \n Nhiệt độ CPU"
        val tempNotificationLayout =
            RemoteViews(packageName, R.layout.layout_notification_battery_info)
        tempNotificationLayout.setTextViewText(R.id.tvTemp, textTemp)
        tempNotificationLayout.setTextViewText(R.id.tvTempCPU, textTempCPU)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, "201")
            .setSmallIcon(R.drawable.ic_android)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(tempNotificationLayout)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(201, notification)
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        //do heavy work on a background thread
//stopSelf();
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }

    private val batteryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra("level", 0)
            val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            val bat = "$level%"
            val tempC = (temp / 10).toFloat()
            updateNotification(bat, tempC)
        }
    }

    private fun updateNotification(bat: String, tempC: Float) {
        val tempF = (tempC * 9 / 5) + 32
        val cpu = Utils.getCPUInfo()
        val tempCPUC = Utils.cpuTemperature()
        val tempCPUF = (tempCPUC * 9 / 5) + 32

        val textTemp = "${tempC.toString().replace(".", ",")}°C/${tempF.toString().replace(
            ".",
            ","
        )}°F \n Nhiệt độ pin"
        val textTempCPU = "${tempCPUC.toString().replace(".", ",")}°C/${tempCPUF.toString().replace(
            ".",
            ","
        )}°F \n Nhiệt độ CPU"
        val tempNotificationLayout =
            RemoteViews(packageName, R.layout.layout_notification_battery_info)
        tempNotificationLayout.setTextViewText(R.id.tvTemp, textTemp)
        tempNotificationLayout.setTextViewText(R.id.tvTempCPU, textTempCPU)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, "201")
            .setSmallIcon(android.R.color.transparent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(tempNotificationLayout)
            .setContentIntent(pendingIntent)
            .build()


        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(201, notification)

    }
}