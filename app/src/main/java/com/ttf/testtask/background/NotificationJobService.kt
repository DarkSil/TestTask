package com.ttf.testtask.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ttf.testtask.R
import com.ttf.testtask.util.DBHelper
import com.ttf.testtask.util.Util

class NotificationJobService : JobService() {
    private val NOTIFICATION_CHANNEL_ID = "TestTaskNotificationID"

    private val listOfTimes = ArrayList<String>()
    private val dbHelper by lazy { DBHelper(this) }

    override fun onStartJob(p0: JobParameters?): Boolean {

        println("STARTED")

        val sp = this@NotificationJobService.getSharedPreferences("notifications", Context.MODE_PRIVATE)
        var lastId = sp.getInt("lasNotificationId", 0)

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${DBHelper.TABLE_NAME} ORDER BY id DESC", null)

        if (cursor.moveToFirst())  {
            do {
                val timeColumnIndex = cursor.getColumnIndex("time")
                val time = cursor.getString(timeColumnIndex)

                listOfTimes.add(time)
            } while (cursor.moveToNext())
        }

        cursor.close()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "NAME"
            val descriptionText = "DESCRIPTION"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val description = if (listOfTimes.isEmpty()) {
            getString(R.string.noBoots)
        } else if (listOfTimes.size == 1) {
            getString(R.string.oneBoot).replace("{placeholder}", listOfTimes[0])
        } else {
            getString(R.string.fewBoots).replace(
                "{placeholder}",
                ((listOfTimes[0].toLongOrNull() ?: 0) - (listOfTimes[1].toLongOrNull() ?: 0)).toString()
            )
        }

        val notification =
            NotificationCompat.Builder(this@NotificationJobService, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Reboot")
                .setContentText(description)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                    .bigText(description)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(this@NotificationJobService)
            .cancel(lastId)

        NotificationManagerCompat.from(this@NotificationJobService)
            .notify(++lastId, notification)

        val editor = sp.edit()
        editor.putInt("lasNotificationId", lastId)
        editor.apply()

        Util.scheduleNotifications(applicationContext)

        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }
}