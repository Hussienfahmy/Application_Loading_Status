package com.udacity

import android.app.DownloadManager
import android.app.DownloadManager.COLUMN_STATUS
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

enum class DownloadStat { SUCCESS, FAIL, UNKNOWN }

val Context.downloadManager
    get() = getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager

fun Context.getDownloadStatues(downloadID: Long): DownloadStat {
    downloadManager.query(DownloadManager.Query().setFilterById(downloadID)).use { cursor ->
        with(cursor) {
            if (this != null && moveToFirst())
            return when(getInt(getColumnIndex(COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> DownloadStat.SUCCESS
                DownloadManager.STATUS_FAILED -> DownloadStat.FAIL
                else -> DownloadStat.UNKNOWN
            }
            return DownloadStat.UNKNOWN
        }
    }
}