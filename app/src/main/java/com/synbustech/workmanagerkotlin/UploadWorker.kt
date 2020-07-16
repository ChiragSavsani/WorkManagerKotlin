package com.synbustech.workmanagerkotlin

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class UploadWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    companion object{
        const val WORKER_KEY = "worker_key"
    }
    override fun doWork(): Result {
        try{
            val count = inputData.getInt(MainActivity.KEY_COUNT_VALUE, 0)

            for(i in 0 until count) {
                Log.i("MYTAG", "Uploading $i")
            }

            val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = time.format(Date())

            val outPutData = Data.Builder()
                .putString(WORKER_KEY, currentDate)
                .build()
            return Result.success(outPutData)
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}