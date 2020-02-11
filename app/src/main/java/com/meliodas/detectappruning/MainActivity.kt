package com.meliodas.detectappruning

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listenToViews(btGetAppRunning, btKillAppRunning)
        startService()
    }

    fun startService() {
        val serviceIntent = Intent(this, ExampleService::class.java)
        serviceIntent.putExtra("inputExtra", 32f)
        ContextCompat.startForegroundService(this, serviceIntent)

    }

    fun stopService() {
        val serviceIntent = Intent(this, ExampleService::class.java)
        stopService(serviceIntent)
    }

    override fun onClick(v: View) {
        when (v) {
            btGetAppRunning -> {
                startService()
            }
            btKillAppRunning -> {
                stopService()
            }
        }
    }

    fun test() {
        val packages: List<ApplicationInfo>
        val pm: PackageManager = packageManager
        //get a list of installed apps.
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0)

        val mActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val myPackage = applicationContext.packageName
        for (packageInfo in packages) {
            if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) continue
            if (packageInfo.packageName == myPackage) continue
            mActivityManager.killBackgroundProcesses(packageInfo.packageName)
        }
    }

    fun amKillProcess(process: String) {
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (runningProcess in runningProcesses) {
            if (runningProcess.processName == process) {
                Process.sendSignal(runningProcess.pid, Process.SIGNAL_KILL)
            }
        }
    }

    fun killApps(context: Context) {
        val pm = context.packageManager
        val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) { //system apps! get out
            if (!isSTOPPED(packageInfo)) {
                amKillProcess(packageInfo.processName)
            }
        }
    }

    fun getActiveApps(context: Context): String? {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        var value: String = "" // basic date stamp
        value += "---------------------------------\n"
        value += "Active Apps\n"
        value += "=================================\n"
        for (packageInfo in packages) { //system apps! get out
            if (!isSTOPPED(packageInfo) && !isSYSTEM(packageInfo)) {
                value += getApplicationLabel(
                        context,
                        packageInfo.packageName
                ).toString() + "\n" + packageInfo.packageName + "\n-----------------------\n"
            }
        }
        return value
    }

    private fun isSTOPPED(pkgInfo: ApplicationInfo): Boolean {
        return pkgInfo.flags and ApplicationInfo.FLAG_STOPPED != 0
    }

    private fun isSYSTEM(pkgInfo: ApplicationInfo): Boolean {
        return pkgInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    fun getApplicationLabel(context: Context, packageName: String): String? {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        var label: String? = null
        for (i in packages.indices) {
            val temp = packages[i]
            if (temp.packageName == packageName) label =
                    packageManager.getApplicationLabel(temp).toString()
        }
        return label
    }

    private fun getAppRunning() {
        val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses

        for (runningProInfo in procInfos) {
            Log.d("Running Processes", "()()" + runningProInfo.processName)
        }
    }

    private fun killAllAppRunning() {
        val packages: List<ApplicationInfo>
        val pm: PackageManager = packageManager
        //get a list of installed apps.
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0)

        val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (packageInfo in packages) {
            if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) continue
            if (packageInfo.packageName == "mypackage") continue
            activityManager.killBackgroundProcesses(packageInfo.packageName)
        }
    }
}
