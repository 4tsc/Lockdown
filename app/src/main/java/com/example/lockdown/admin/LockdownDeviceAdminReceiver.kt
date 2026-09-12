package com.example.lockdown.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class LockdownDeviceAdminReceiver : DeviceAdminReceiver() {

    // confirmacion ANTES de dejar desactivar el admin.

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return "Si desactivas esto, pierdes la protección contra desinstalación del bloqueador."
    }
}