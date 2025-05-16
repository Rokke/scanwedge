package no.talgoe.scanwedge.scanwedge

import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import android.content.Context
import android.content.ContextWrapper

class BatteryMonitor(private val scanW: ScanwedgePlugin, private val log: Logger?) {
    private val TAG = "BatteryMonitor"
    private var isReceiverRegistered = false

    fun monitorBatteryStatus(context: Context) {
        val batteryStatus: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            context.registerReceiver(batteryDataReceiver, intentFilter).also { isReceiverRegistered = true }
        } else {
            context.registerReceiver(batteryDataReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED)).also { isReceiverRegistered = true }
        }
        Log.d(TAG, "monitorBatteryStatus: ${batteryStatus?.toUri(0)}")
    }

    fun dispose(context: Context?) {
        if (isReceiverRegistered) {
            context?.unregisterReceiver(batteryDataReceiver)
            isReceiverRegistered = false
            Log.d(TAG, "BatteryMonitor: Receiver unregistered")
        }
    }

    private val batteryDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            log?.i(TAG, "batteryDataReceiver-onReceive: ${intent.toUri(0)}, ${intent.action}")
            if (intent.action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                val batteryStatus = mutableMapOf<String, Any>()
                val extraBatteryStatus = mutableMapOf<String, Any>()
                // Check if the API version is higher than 36
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    batteryStatus["capacityLevel"] = intent.getIntExtra("android.os.extra.CAPACITY_LEVEL", -1)                  // [API 36] -1: Unsupported, 0: Unknown, 1: Critical, 2: Low, 3: Normal, 4: High, 5: Full
                }
                // Check if the API version is higher than 34
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    intent.getStringExtra("android.os.extra.BATTERY_TECHNOLOGY")?.let{ extraBatteryStatus["batteryTechnology"] = it }          // [API 34] The technology of the battery, such as "Li-ion" or "NiMH".
                    extraBatteryStatus["batteryHealth"] = intent.getIntExtra("android.os.extra.BATTERY_HEALTH", -1)                  // [API 34] The health of the battery.
                    extraBatteryStatus["batteryHealthReason"] = intent.getIntExtra("android.os.extra.BATTERY_HEALTH_REASON", -1)     // [API 34] The reason for the battery health status.
                    batteryStatus["cycleCount"] = intent.getIntExtra("android.os.extra.CYCLE_COUNT", -1)                        // [API 34] The number of charge cycles the battery has gone through.
                    batteryStatus["chargingStatus"] = intent.getIntExtra("android.os.extra.CHARGING_STATUS", -1)                // [API 34] Int value representing the battery charging status
                }
                for (key in intent.extras!!.keySet()) {
                    val value = intent.extras!!.get(key)
                    if (value != null) {
                        when (key) {
                            "status" -> batteryStatus["status"] = value                                         // [API 5] 1: Unknown, 2: Charging, 3: Discharging, 4: Not charging, 5: Full
                            "health" -> batteryStatus["health"] = value                                         // [API 5] 1: Unknown, 2: Good, 3: Overheat, 4: Dead, 5: Over voltage, 6: Unspecified failure, 7: Cold
                            "temperature" -> batteryStatus["temperature"] = (value as Int / 10).toDouble()      // [API 5] integer containing the current battery temperature in tenths of a degree Celsius.
                            "voltage" -> batteryStatus["voltage"] = (value as Int / 1000).toDouble()            // [API 5] integer containing the current battery voltage in millivolts.
                            "level" -> batteryStatus["level"] = value                                           // [API 5] integer between 0 and "scale" representing the current battery level.
                            "scale" -> batteryStatus["scale"] = value                                           // [API 5] integer containing the maximum battery level.
                            "plugged" -> batteryStatus["plugged"] = value                                       // [API 5] 0: Unplugged, 1: AC, 2: USB, 4: Wireless
                            "technology" -> batteryStatus["technology"] = value                                 // [API 5] String containing the technology of the battery, such as "Li-ion" or "NiMH".
                            "present" -> batteryStatus["present"] = value                                       // [API 5] boolean indicating whether a battery is present.
                            "charge_counter" -> batteryStatus["chargeCounter"] = value                          // [API 21] Battery capacity in microampere-hours, as an integer.
                            
                            else -> {
                                val fromHardwarePlugin = scanW.checkHardwarePlugin(key, value)
                                if (fromHardwarePlugin != null) {
                                    fromHardwarePlugin.let { (key, value) ->
                                        batteryStatus[key] = value
                                    }
                                } else {
                                    log?.w(TAG, "batteryDataReceiver-onReceive: ${key} not handled")
                                    extraBatteryStatus[key] = value
                                }
                            }
                        }
                    }
                }
                log?.i(TAG, "batteryDataReceiver-onReceive: ${batteryStatus}")
                batteryStatus["extraMap"] = extraBatteryStatus
                scanW.sendBatteryStatus(batteryStatus)
            } else {
                log?.w(TAG, "batteryDataReceiver-onReceive: ${intent.toUri(0)}, ${intent.action} not ACTION_BATTERY_CHANGED")
            }
        }
    }
}

interface IHardwareBatteryPlugin {
    fun getBatteryValueMap(key: String, value: Any): Pair<String, Any>?
}
