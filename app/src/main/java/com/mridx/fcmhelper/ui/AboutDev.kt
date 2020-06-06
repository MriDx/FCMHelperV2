/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mridx.fcmhelper.R
import com.mridx.fcmhelper.database.Apis
import kotlinx.android.synthetic.main.about_dev.*
import kotlinx.android.synthetic.main.icons_view.view.*

class AboutDev : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_dev)

        setupSocial()

    }

    private fun setupSocial() {
        facebook.imageBox.setImageResource(R.drawable.facebook)
        facebook.setOnClickListener(this)
        facebook.textBox.text = "facebook"
        insta.imageBox.setImageResource(R.drawable.insta)
        insta.textBox.text = "Instagram"
        insta.setOnClickListener(this)
        twitter.imageBox.setImageResource(R.drawable.twitter)
        twitter.textBox.text = "Twitter"
        twitter.setOnClickListener(this)
        mail.imageBox.setImageResource(R.drawable.email)
        mail.textBox.text = "Mail"
        mail.setOnClickListener(this)
        linkedin.imageBox.setImageResource(R.drawable.linkedin)
        linkedin.textBox.text = "Linkedin"
        linkedin.setOnClickListener(this)
        github.imageBox.setImageResource(R.drawable.github)
        github.textBox.text = "Github"
        github.setOnClickListener(this)
        medium.imageBox.setImageResource(R.drawable.medium)
        medium.textBox.text = "Medium"
        medium.setOnClickListener(this)
        codepen.imageBox.setImageResource(R.drawable.codepen)
        codepen.textBox.text = "Codepen"
        codepen.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            facebook.id -> openFacebook()
            insta.id -> handleLinks(Apis().insta)
            twitter.id -> handleLinks(Apis().twitter)
            mail.id -> handleLinks(Apis().mail)
            linkedin.id -> handleLinks(Apis().linkedin)
            github.id -> handleLinks(Apis().github)
            medium.id -> handleLinks(Apis().medium)
            codepen.id -> handleLinks(Apis().codepen)
        }
    }

    private fun handleLinks(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val chooser = Intent.createChooser(
            intent,
            ""
        )
        startActivity(chooser)

    }


    private fun openFacebook() {
        if (packageManager.getPackageInfo(
                "com.facebook.katana",
                PackageManager.GET_META_DATA
            ) != null
        )
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Apis().fbApp)))
        else
            handleLinks(Apis().fb)
    }


}