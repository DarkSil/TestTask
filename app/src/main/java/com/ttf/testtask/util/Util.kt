package com.ttf.testtask.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.ttf.testtask.background.NotificationJobService

object Util {

    fun scheduleNotifications(context: Context) {
        val component = ComponentName(context, NotificationJobService::class.java)
        val builder = JobInfo.Builder(0, component)
        builder.setMinimumLatency(60*1000*15) //60 sec
        builder.setOverrideDeadline(60*1000*15)
        val jobScheduler = context.getSystemService(JobScheduler::class.java)
        jobScheduler.schedule(builder.build())
    }

}