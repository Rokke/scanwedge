package no.talgoe.scanwedge.scanwedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build

/**
 * Registers [receiver] using the SDK-appropriate API. The explicit export flag is only
 * available (and, from API 34, required) on API 33+ (Tiramisu); on older releases the
 * two-argument overload is used. Centralising this guarantees every hardware plugin —
 * current and future — is guarded, instead of each one re-implementing (or forgetting)
 * the version check (see issue: ZebraPlugin previously called the flagged overload
 * unconditionally, which crashes on API 24-25 where it does not exist).
 *
 * @param exported true for receivers that must accept broadcasts from other apps
 *   (e.g. Zebra DataWedge), false for app-internal broadcasts.
 */
fun Context.registerReceiverCompat(receiver: BroadcastReceiver, filter: IntentFilter, exported: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val flag = if (exported) Context.RECEIVER_EXPORTED else Context.RECEIVER_NOT_EXPORTED
        registerReceiver(receiver, filter, flag)
    } else {
        @Suppress("UnspecifiedRegisterReceiverFlag")
        registerReceiver(receiver, filter)
    }
}

/**
 * Unregisters [receiver], ignoring the [IllegalArgumentException] Android throws when the
 * receiver was never registered (e.g. registration failed inside initialize() and returned
 * false), so dispose()/re-init can't crash. (BatteryMonitor achieves the same safety with
 * an isReceiverRegistered flag; the hardware plugins share no such state, so they catch.)
 */
fun Context.unregisterReceiverSafely(receiver: BroadcastReceiver) {
    try {
        unregisterReceiver(receiver)
    } catch (e: IllegalArgumentException) {
        // Receiver was not registered; nothing to do.
    }
}
