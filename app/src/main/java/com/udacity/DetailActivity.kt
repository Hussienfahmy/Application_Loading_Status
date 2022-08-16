package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val extras = intent.extras

        val state =
            DownloadStat.valueOf(extras?.getString(EXTRA_STATE) ?: DownloadStat.UNKNOWN.name)
        val fileName = extras?.getString(EXTRA_FILE_NAME) ?: "Unknown Name"

        statues_text.text = state.name
        name_text.text = fileName

        statues_image.setImageResource(when(state) {
            DownloadStat.SUCCESS -> R.drawable.ic_baseline_done_24
            else -> R.drawable.ic_baseline_failed_24
        })

        btn.setOnClickListener { finish() }
    }

    companion object {
        private const val EXTRA_STATE = "statues"
        private const val EXTRA_FILE_NAME = "file_name"
        fun generateBundle(stat: DownloadStat, fileName: String) =
            bundleOf(EXTRA_STATE to stat.name, EXTRA_FILE_NAME to fileName)
    }
}
