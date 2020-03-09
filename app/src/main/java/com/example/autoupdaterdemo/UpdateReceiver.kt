package com.example.autoupdaterdemo

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import timber.log.Timber

class UpdateReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.e("update broadcast received")

        if (context == null) {
            return
        }

        val i = Intent(context, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)

        Toast.makeText(context, "Updated to version #${BuildConfig.VERSION_CODE}!", Toast.LENGTH_LONG).show()
    }
}