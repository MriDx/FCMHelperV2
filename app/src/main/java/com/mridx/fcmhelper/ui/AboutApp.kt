/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mridx.fcmhelper.BuildConfig
import com.mridx.fcmhelper.R
import kotlinx.android.synthetic.main.about_app.*

class AboutApp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_app)

        appVersion.text = BuildConfig.VERSION_NAME

    }

    /*fun LaunchBrowser(view: View?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://mridx.github.io/FCMHelper/")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }*/

    fun openUrl(view: View) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://github.com/MriDx/FCMHelperV2")
        startActivity(intent)
    }

}