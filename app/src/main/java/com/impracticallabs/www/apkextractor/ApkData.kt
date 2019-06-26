package com.impracticallabs.www.apkextractor

import android.content.pm.ApplicationInfo


data class ApkData(val appInfo: ApplicationInfo,
               val appName: String,
               val packageName: String? = "",
               val version: String? = "")