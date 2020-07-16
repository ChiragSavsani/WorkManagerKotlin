package com.synbustech.workmanagerkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object{
        const val KEY_COUNT_VALUE = "key_count"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartPeriodic.setOnClickListener {
            setPeriodicWorkRequest()
        }

        btnStartTime.setOnClickListener {
            setOnTimeWorkRequest()
        }
    }

    private fun setOnTimeWorkRequest(){
        val workManager = WorkManager.getInstance(applicationContext)
        val data: Data = Data.Builder().
                putInt(KEY_COUNT_VALUE, 125).build()
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        val filterRequest = OneTimeWorkRequest.Builder(FilteringWorker::class.java)
            .build()

        val compressingRequest = OneTimeWorkRequest.Builder(CompressingWorker::class.java)
            .build()

        val downloadingRequest = OneTimeWorkRequest.Builder(DownloadingWorker::class.java)
            .build()

        val parallelWorker = mutableListOf<OneTimeWorkRequest>()
        parallelWorker.add(downloadingRequest)
        parallelWorker.add(filterRequest)

        workManager.beginWith(parallelWorker)
            .then(compressingRequest)
            .then(uploadRequest)
            .enqueue()

        workManager.getWorkInfoByIdLiveData(uploadRequest.id).observe(this, Observer {
            txtMessage.text = it.state.name
            if(it.state.isFinished){
                val data = it.outputData
                val message = data.getString(UploadWorker.WORKER_KEY)
                txtMessage.append("\n" + message)
            }
        })
    }

    private fun setPeriodicWorkRequest(){

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            DownloadingWorker::class.java, 15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest)

    }
}