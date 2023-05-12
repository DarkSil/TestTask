package com.ttf.testtask.background

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent

class RestartBroadcast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED) {

        }
    }
}