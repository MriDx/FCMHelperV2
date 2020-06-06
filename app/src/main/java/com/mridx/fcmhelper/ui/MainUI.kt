/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.ui

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.mridx.fcmhelper.BuildConfig
import com.mridx.fcmhelper.R
import com.mridx.fcmhelper.adapter.AppsAdapter
import com.mridx.fcmhelper.database.DBHelper
import com.mridx.fcmhelper.database.Helper
import com.mridx.fcmhelper.dataholder.Apps
import kotlinx.android.synthetic.main.dashboard.*
import kotlinx.android.synthetic.main.drawer_layout.*
import kotlinx.android.synthetic.main.main_ui.*
import kotlin.math.roundToInt


class MainUI : AppCompatActivity(), AppAddFragment.OnAppSave,
    NavigationView.OnNavigationItemSelectedListener {

    private val appList: ArrayList<Apps>? = ArrayList()
    var appsAdapter: AppsAdapter? = null
    var helper: Helper? = null
    var action: String? = "ehllo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_layout)

        if (intent.extras != null) {
            performAction(intent.extras!!)
        }

        setSupportActionBar(toolbar)
        supportActionBar?.title = "FCM Helper"

        fab.setOnClickListener { showAppAddDialog() }

        assignToTopic()

        queryApps()

        swipeLayout.setOnRefreshListener {
            if (swipeLayout.isRefreshing)
                getApps()
        }

        setupDrawer()

    }

    private fun performAction(bundle: Bundle) {
        if (!bundle.containsKey("action"))
            return

        if (bundle.getString("action").equals("unsubscribe"))
            unsubscribe(bundle.getString("value"))
        else if (bundle.getString("action").equals("update"))
            openBrowser(bundle.getString("url"))
    }

    private fun openBrowser(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun unsubscribe(value: String?) {
        if (value != null)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(value)
    }

    private fun setupDrawer() {
        val drawer =
            findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle =
            ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
        drawer.addDrawerListener(toggle)
        setWidth()
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun setWidth() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val params =
            nav_view.layoutParams
        params.width = (displayMetrics.widthPixels * 0.85).roundToInt()
        nav_view.layoutParams = params
    }

    private fun assignToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.VERSION_NAME)
            .addOnCompleteListener {
                /*Toast.makeText(
                    this,
                    "subscribed to ${BuildConfig.VERSION_NAME}",
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            Log.d(
                "kaku",
                "${instanceIdResult.id} ${instanceIdResult.token}"
            )
        }
    }

    private fun showAppAddDialog() {
        val addAppFragment: DialogFragment = AppAddFragment()
        addAppFragment.show(supportFragmentManager, "Dialog Fragment")

    }

    private fun queryApps() {
        getApps()
        appsAdapter = AppsAdapter(this, appList!!)
        appsAdapter!!.onItemClicked = { apps ->
            Log.d("kaku", apps.appName)
            val intent = Intent(this, SendUI::class.java)
            val bundle = Bundle()
            bundle.putSerializable("app", apps)
            intent.putExtras(bundle)
            //intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        //appsAdapter!!.onAppDeleted = {apps -> deleteApp(apps.id) }

        appHolder.layoutManager = GridLayoutManager(this, 1)
        appHolder.adapter = appsAdapter

    }

    /*private fun deleteApp(id: Int) {
        if (helper == null)
            helper = Helper(this)

        helper!!.deleteApp(id)
        swipeLayout.isRefreshing = true
        getApps()
    }*/

    fun saveApp(appName: String, serverKey: String) {
        if (helper == null)
            helper = Helper(this)

        helper!!.saveApp(appName, serverKey)
        showSnackbar()
    }

    private fun showSnackbar() {
        val snackbar = Snackbar.make(
            main_layout,
            "App added successfully. Pull to refresh app list !",
            Snackbar.LENGTH_LONG
        )
        snackbar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackbar.setAction("Refresh") { getApps() }
        snackbar.show()
    }

    private fun getApps() {
        if (helper == null)
            helper = Helper(this)
        appList!!.clear()
        extractApps(helper!!.getAllApps())
    }

    private fun extractApps(allApps: Cursor) {
        if (allApps.moveToFirst())
            do {
                appList?.add(
                    Apps(
                        allApps.getString(allApps.getColumnIndex(DBHelper.APP_NAME)),
                        allApps.getString(allApps.getColumnIndex(DBHelper.SERVER_KEY)),
                        allApps.getInt(allApps.getColumnIndex(DBHelper.ID))
                    )
                )
            } while (allApps.moveToNext())
        allApps.close()
        notifyAdapter()
    }

    private fun notifyAdapter() {
        appsAdapter?.notifyDataSetChanged()
        if (appList!!.size < 1)
            showErrorDialog()

        swipeLayout.isRefreshing = false
    }

    private fun showErrorDialog() {
        //Toast.makeText(this, "No App Kela", Toast.LENGTH_SHORT).show()
        Snackbar.make(main_layout, "There's no app added !", Snackbar.LENGTH_SHORT).show()
    }

    override fun onAppSave(appName: String, serverKey: String) {
        saveApp(appName, serverKey)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navEmail -> SendEmail()
            R.id.navReport -> ReportBug()
            R.id.navChkUp
            -> Toast.makeText(this, "Update featured to be added in later version", Toast.LENGTH_SHORT).show()
            R.id.navAbtDev -> AboutDev()
            else -> AboutApp()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun AboutDev() {
        val i = Intent(this, AboutDev::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
    }

    private fun AboutApp() {
        val i = Intent(this, AboutApp::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
    }

    private fun ReportBug() {
        val i = Intent(this, SendReport::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
    }

    private fun SendEmail() {
        val i = Intent(Intent.ACTION_SENDTO)
        i.type = "text/plain"
        //i.putExtra(Intent.EXTRA_SUBJECT, "Mail Subject");
        i.data = Uri.parse("mailto:mridulbaishya272@gmail.com")
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    override fun onResume() {
        super.onResume()
        getApps()
    }
}


