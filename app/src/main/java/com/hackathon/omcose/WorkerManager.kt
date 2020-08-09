package com.hackathon.omcose

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.hackathon.omcose.workers.FileIndexerWorker
import java.util.concurrent.TimeUnit

object WorkerManager {
    fun init(appContext:Context) {
        // Register File Indexing Worker
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .build()

        val fileIndexingWork = PeriodicWorkRequestBuilder<FileIndexerWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()


        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
            "FileIndexer",
            ExistingPeriodicWorkPolicy.REPLACE,
            fileIndexingWork
        )

    }
}