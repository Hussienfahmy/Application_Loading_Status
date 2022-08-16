package com.udacity

import android.app.Activity
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
            custom_button.setState(ButtonState.Loading)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            id?.let {
                downloadID = it
                val statue = getDownloadStatues(downloadID)
                if (statue == DownloadStat.SUCCESS) {
                    custom_button.setState(ButtonState.Completed)
                    sendNotification(
                        statues = statue,
                        filename = when (radioGroup.checkedRadioButtonId) {
                            R.id.btn_glide -> "GLide"
                            R.id.btn_load_app -> "Load App"
                            R.id.btn_retrofit -> "Retrofit"
                            else -> ""
                        }
                    )
                }
            }
        }
    }

    private fun download() {
        val url = when (radioGroup.checkedRadioButtonId) {
            R.id.btn_glide -> GLIDE_DOWNLOAD_URL
            R.id.btn_load_app -> LOAD_APP_DOWNLOAD_URL
            R.id.btn_retrofit -> RETROFIT_DOWNLOAD_URL
            else -> ""
        }

        if (url.isBlank()) {
            Toast.makeText(this, "Please Select Repo", Toast.LENGTH_SHORT).show()
            return
        }

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GLIDE_DOWNLOAD_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"

        private const val LOAD_APP_DOWNLOAD_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val RETROFIT_DOWNLOAD_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    }

}
