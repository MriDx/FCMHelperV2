/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.dataholder

import java.io.Serializable

open class Apps(val appName : String, var serverKey: String, val id: Int) : Serializable