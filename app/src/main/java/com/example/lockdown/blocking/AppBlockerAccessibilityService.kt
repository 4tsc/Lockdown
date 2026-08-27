package com.example.lockdown.blocking

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.lockdown.LockdownApp
import com.example.lockdown.data.BlockRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

private const val TAG = "AppBlocker"
private const val SETTINGS_PACKAGE = "com.android.settings"

class AppBlockerAccessibilityService : AccessibilityService() {

    private lateinit var repository: com.example.lockdown.data.BlockingRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    @Volatile private var todaysRules: List<BlockRule> = emptyList()
    private var cachedDayOfWeek: Int = -1
    private var collectJob: Job? = null
    private var appLabel: String = ""

    private val uninstallKeywords = listOf("desinstalar", "uninstall")

    override fun onServiceConnected() {
        super.onServiceConnected()
        repository = (application as LockdownApp).repository
        appLabel = try {
            packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)).toString()
        } catch (e: Exception) { "" }
        subscribeToToday()
    }

    private fun subscribeToToday() {
        val today = LocalDate.now().dayOfWeek.value
        if (today == cachedDayOfWeek && collectJob?.isActive == true) return
        cachedDayOfWeek = today
        collectJob?.cancel()
        collectJob = serviceScope.launch {
            repository.blockRuleDao.observeForDay(today).collect { rules ->
                todaysRules = rules.filter { it.isEnabled }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        // Anti-manipulación: si estamos dentro de Ajustes y la pantalla
        // menciona el nombre de nuestra app, lo más probable es que sea
        // la de desactivar el admin o la de desinstalar/forzar cierre.
        if (pkg == SETTINGS_PACKAGE) {
            if (isUninstallScreen(rootInActiveWindow)) {
                Log.d(TAG, "pantalla de desinstalación detectada, saliendo")
                performGlobalAction(GLOBAL_ACTION_HOME)
            }
            return
        }

        if (pkg == packageName) return

        subscribeToToday()
        if (isCurrentlyBlocked(pkg)) {
            launchBlockScreen(pkg)
        }
    }

    private fun collectAllText(node: AccessibilityNodeInfo?, sb: StringBuilder, depth: Int = 0) {
        if (node == null || depth > 40) return
        node.text?.let { sb.append(it).append(' ') }
        node.contentDescription?.let { sb.append(it).append(' ') }
        for (i in 0 until node.childCount) {
            collectAllText(node.getChild(i), sb, depth + 1)
        }
    }

    private fun isUninstallScreen(root: AccessibilityNodeInfo?): Boolean {
        if (root == null || appLabel.isEmpty()) return false
        val sb = StringBuilder()
        collectAllText(root, sb)
        val text = sb.toString()
        val hasAppName = text.contains(appLabel, ignoreCase = true)
        val hasUninstallWord = uninstallKeywords.any { text.contains(it, ignoreCase = true) }
        return hasAppName && hasUninstallWord
    }

    private fun isCurrentlyBlocked(pkg: String): Boolean {
        val now = LocalTime.now()
        val nowMinutes = now.hour * 60 + now.minute
        return todaysRules.any { rule ->
            rule.packageName == pkg && isWithinRange(nowMinutes, rule.startMinute, rule.endMinute)
        }
    }

    private fun isWithinRange(now: Int, start: Int, end: Int): Boolean =
        if (start <= end) now in start until end else now >= start || now < end

    private fun launchBlockScreen(blockedPackage: String) {
        val intent = Intent(this, BlockActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            )
            putExtra(BlockActivity.EXTRA_BLOCKED_PACKAGE, blockedPackage)
        }
        startActivity(intent)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        collectJob?.cancel()
        serviceScope.cancel()
    }
}