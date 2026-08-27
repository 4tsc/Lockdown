package com.example.lockdown.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class InstalledApp(
    val packageName: String,
    val label: String,
    val icon: Drawable
)

object InstalledAppsProvider {
    fun getLaunchableApps(context: Context): List<InstalledApp> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .filter { it.activityInfo.packageName != context.packageName }
            .distinctBy { it.activityInfo.packageName }
            .map {
                InstalledApp(
                    packageName = it.activityInfo.packageName,
                    label = it.loadLabel(pm).toString(),
                    icon = it.loadIcon(pm)
                )
            }
            .sortedBy { it.label.lowercase() }
    }
}