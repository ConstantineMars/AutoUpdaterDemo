package com.example.autoupdaterdemo

import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun removeAdmin() {
        val cn = ComponentName(packageName, packageName + ".AdminReceiver")
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        dpm.removeActiveAdmin(cn)
    }

    fun update() {
        fun install(context: Context, packageName: String, apkPath: String) {

            // PackageManager provides an instance of PackageInstaller
            val packageInstaller = context.packageManager.packageInstaller

            // Prepare params for installing one APK file with MODE_FULL_INSTALL
            // We could use MODE_INHERIT_EXISTING to install multiple split APKs
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            params.setAppPackageName(packageName)

            // Get a PackageInstaller.Session for performing the actual update
            val sessionId = packageInstaller.createSession(params)
            val session = packageInstaller.openSession(sessionId)

            // Copy APK file bytes into OutputStream provided by install Session
            val out = session.openWrite(packageName, 0, -1)
            val fis = File(apkPath).inputStream()
            fis.copyTo(out)
            session.fsync(out)
            out.close()

            // The app gets killed after installation session commit
            session.commit(
                PendingIntent.getBroadcast(context, sessionId,
                Intent("android.intent.action.MAIN"), 0).intentSender)
        }
    }
}
