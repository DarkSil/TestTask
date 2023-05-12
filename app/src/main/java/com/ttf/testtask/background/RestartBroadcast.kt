package com.ttf.testtask.background

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import com.ttf.testtask.util.DBHelper

class RestartBroadcast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED) {
            p0?.let {
                val db = DBHelper(it).writableDatabase

                val contentValues = ContentValues()
                contentValues.put("time", System.currentTimeMillis().toString())

                db.insert(DBHelper.TABLE_NAME, null, contentValues)
            }
        }
    }
}