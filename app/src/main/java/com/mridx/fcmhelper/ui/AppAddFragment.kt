/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mridx.fcmhelper.R
import kotlinx.android.synthetic.main.app_add_ui.view.*

class AppAddFragment : DialogFragment() {

    private var mainUI: MainUI? = null

    interface OnAppSave {
        fun onAppSave(appName: String, serverKey: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.app_add_ui, container, false)
        dialog?.window?.attributes?.gravity = Gravity.FILL_HORIZONTAL
        view.add.setOnClickListener { validateAndProceed(view) }
        return view
    }

    private fun validateAndProceed(view: View) {
        when {
            view.appName.text!!.isEmpty() -> return
            view.serverKey.text!!.isEmpty() -> return
            else -> {
                val listener: OnAppSave? = activity as OnAppSave?
                listener!!.onAppSave(
                    view.appName.text.toString(),
                    view.serverKey.text.toString()
                )
                dialog!!.dismiss()
            }


            //else -> onAppSave?.invoke(view.appName.text.toString(), view.serverKey.text.toString())
        }
    }

}
