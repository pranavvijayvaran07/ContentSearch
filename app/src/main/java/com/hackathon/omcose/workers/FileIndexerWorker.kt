package com.hackathon.omcose.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hackathon.omcose.fileindexer.PdfIndexer
import java.lang.Exception
import java.time.LocalDateTime

class FileIndexerWorker(appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams) {
    companion object{
        val LOG_TAG = FileIndexerWorker::class.java.name
    }
    override fun doWork(): Result {
        try {
            startPdfFilesIndexing()
        } catch (exception:Exception){
            Log.e(LOG_TAG, " Exception occured  : ${exception.message}")

            return Result.retry()
        }

        return Result.success()
    }

    private fun startPdfFilesIndexing() {
        Log.d(LOG_TAG, "Pdf Files indexing started and the timestamp is : ${LocalDateTime.now()}")
        val pdfIndexer = PdfIndexer()
        pdfIndexer.getFilesToIndex(applicationContext)
    }

}