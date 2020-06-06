/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mridx.fcmhelper.R
import com.mridx.fcmhelper.dataholder.Apps
import kotlinx.android.synthetic.main.app.view.*

class AppsAdapter(context: Context, appsList: ArrayList<Apps>) : RecyclerView.Adapter<AppsAdapter.MyViewholder>() {

    var context : Context? = null
    var appsList : ArrayList<Apps>? = null
    var onItemClicked : ((Apps) -> Unit)? = null
    var onAppDeleted : ((Apps) -> Unit)? = null

    init {
        this.context = context
        this.appsList = appsList
    }

    class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        return MyViewholder(LayoutInflater.from(parent.context).inflate(R.layout.app, parent, false))
    }

    override fun getItemCount(): Int {
        return appsList!!.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        holder.itemView.rowsAppName.text = appsList!![position].appName
        holder.itemView.deleteApp.setOnClickListener{onAppDeleted?.invoke(appsList!![position])}
        holder.itemView.setOnClickListener { v -> onItemClicked?.invoke(appsList!![position])}
    }

}