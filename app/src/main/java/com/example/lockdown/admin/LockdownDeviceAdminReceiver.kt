package com.example.lockdown.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class LockdownDeviceAdminReceiver : DeviceAdminReceiver() {
    // Este texto lo muestra Android automáticamente en el diálogo de
    // confirmación ANTES de dejar desactivar el admin. Es la advertencia
    // "gratis" que mencioné.
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return "Si desactivas esto, pierdes la protección contra desinstalación del bloqueador."
    }
}