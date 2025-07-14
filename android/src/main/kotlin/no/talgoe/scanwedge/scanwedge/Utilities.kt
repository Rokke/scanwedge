package no.talgoe.scanwedge.scanwedge

import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import android.content.Context
import android.content.ContextWrapper
import no.talgoe.scanwedge.scanwedge.ScanwedgePlugin

class Utilities {
    companion object {
        private val TAG = "Utilities"
        fun createExtendedBatteryMap(intent: Intent, scanW: ScanwedgePlugin, log: Logger?): Map<String, Any> {
            if (intent.action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                val batteryStatus = mutableMapOf<String, Any>()
                val manufacturer = scanW.fetchManufacturer()
                if(manufacturer!=null) batteryStatus["deviceManufacturer"] = manufacturer
                val extraBatteryStatus = mutableMapOf<String, Any>()
                val keys = intent.extras!!.keySet()
                for (key in keys) {
                    val value = intent.extras!!.get(key)
                    if (value != null) {
                        when (key) {
                            "android.os.extra.CAPACITY_LEVEL" -> batteryStatus["capacityLevel"] = value         // [API 36] -1: Unsupported, 0: Unknown, 1: Critical, 2: Low, 3: Normal, 4: High, 5: Full
                            "android.os.extra.CYCLE_COUNT" -> batteryStatus["cycleCount"] = value               // [API 34] The number of charge cycles the battery has gone through.
                            "android.os.extra.CHARGING_STATUS" -> batteryStatus["chargingStatus"] = value       // [API 34] Int value representing the battery charging status
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
                            "icon-small"->batteryStatus["iconSmall"] = value                                     // [API 5] integer containing the resource ID of a small status bar icon indicating the current battery state
                            "max_charging_voltage"->batteryStatus["maxChargingVoltage"] = value                  // Unknown: The maximum charging voltage of the battery
                            "max_charging_current"->batteryStatus["maxChargingCurrent"] = value                  // Unknown: The maximum charging current of the battery
                            "seq"->batteryStatus["seq"] = value                                                  // Unknown: The sequence number of the battery
                            "invalid_charger"->batteryStatus["invalidCharger"] = value                           // Unknown: Int value set to nonzero if an unsupported charger is attached to the device
                            "battery_low"->batteryStatus["batteryLow"] = value                                   // Unknown: Indicates if the battery is low

                            // Samsung
                            "current_event" -> batteryStatus["currentEvent"] = value                               // Unknown XCover [Power+] Current event. 0x40000000 = Battery is charging, 0x80000000 = Battery is discharging, 0x20000000 = Battery is idle, 0x10000000 = Battery is in a fault state
                            "misc_event" -> batteryStatus["miscEvent"] = value                                     // Unknown XCover
                            "online" -> batteryStatus["online"] = value                                            // Unknown XCover
                            "pogo_plugged" -> batteryStatus["pogoPlugged"] = value                                 // Unknown XCover
                            "capacity" -> batteryStatus["capacity"] = value                                        // Unknown XCover
                            "current_now" -> batteryStatus["currentNow"] = value                                   // Unknown XCover
                            "charge_type" -> batteryStatus["chargeType"] = value                                   // Unknown XCover
                            "hv_charger" -> batteryStatus["hvCharger"] = value                                     // Unknown XCover
                            "power_sharing" -> batteryStatus["powerSharing"] = value                               // Unknown XCover
                            "charger_type" -> batteryStatus["chargerType"] = value                                 // Unknown XCover

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
                log?.d(TAG, "batteryDataReceiver-onReceive: ${batteryStatus}")
                batteryStatus["extraMap"] = extraBatteryStatus
                if(batteryStatus["extraMap"] is Map<*, *>) batteryStatus["intentLog"] = intent.toUri(0)
                return batteryStatus
            } else {
                log?.w(TAG, "batteryDataReceiver-onReceive: ${intent.toUri(0)}, ${intent.action} not ACTION_BATTERY_CHANGED")
            }
            return emptyMap()
        }
        fun getBatteryStatus(context: Context): String {
            val batteryStatus: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13 (Tiramisu) and higher, use registerReceiver with intent filter and broadcast flag
                val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                context.registerReceiver(null, intentFilter)
            } else {
                // For older versions, use the deprecated method (still works but not recommended for Tiramisu+)
                context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            }

            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

            val batteryPercentage = if (level >= 0 && scale > 0) {
                (level.toFloat() / scale.toFloat() * 100).toInt()
            } else {
                -1
            }

            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

            val batteryHealth = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
            return "$batteryPercentage|$status|$batteryHealth"
        }
        fun getExtendedBatteryStatus(context: Context, scanW: ScanwedgePlugin, log: Logger?): Map<String, Any?> {
            val batteryStatus: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13 (Tiramisu) and higher, use registerReceiver with intent filter and broadcast flag
                val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                context.registerReceiver(null, intentFilter)
            } else {
                // For older versions, use the deprecated method (still works but not recommended for Tiramisu+)
                context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            }
            Log.d("Utilities", "getExtendedBatteryStatus: ${batteryStatus?.toUri(0)}")
/*
#Intent;action=android.intent.action.BATTERY_CHANGED;launchFlags=0x60000010;i.battery_usage_numb=18;i.battery_error_status=0;i.battery_type=206;S.technology=Li-ion;i.battery_usage_decommission_threshold=400;i.icon-small=17303637;
i.max_charging_voltage=5000000;S.zcm_mode=;i.health=2;i.shutdown_level=4;B.zcm_enabled=false;i.max_discharge_temp_shutdown_level=600;i.max_charging_current=500000;i.adjust_shutdown_level=100;i.status=5;i.low_level=18;i.ratedcapacity=3300;
i.plugged=2;B.present=true;S.mfd=2023-02-02;i.seq=5142;S.zcm_extra=;i.charge_counter=3210922;i.level=100;i.base_cumulative_charge=61149;i.scale=100;S.partnumber=BT-000409-50%20R.B;i.critical_level=10;i.temperature=290;i.voltage=4316;
i.min_discharge_temp_shutdown_level=-200;S.serialnumber=T5362;i.invalid_charger=0;B.battery_low=false;i.battery_decommission=0;end */
            return createExtendedBatteryMap(batteryStatus!!, scanW, log)
        }
        // private fun createBatteryStatusMap(batteryStatus: Intent?): Map<String, Any?> {
        //     val batteryMap=mapOf(
        //         "status" to (batteryStatus?.getIntExtra("status", -1) ?: -1),                               // [API 5] 1: Unknown, 2: Charging, 3: Discharging, 4: Not charging, 5: Full
        //         "health" to (batteryStatus?.getIntExtra("health", -1) ?: -1),                               // [API 5] 1: Unknown, 2: Good, 3: Overheat, 4: Dead, 5: Over voltage, 6: Unspecified failure, 7: Cold
        //         "temperature" to ((batteryStatus?.getIntExtra("temperature", 0) ?: 0) / 10.0),              // [API 5] integer containing the current battery temperature in tenths of a degree Celsius.
        //         "voltage" to ((batteryStatus?.getIntExtra("voltage", 0) ?: 0) / 1000.0),                    // [API 5] integer containing the current battery voltage in millivolts.
        //         "cycleCount" to (batteryStatus?.getIntExtra("android.os.extra.CYCLE_COUNT", 0) ?: -1),      // [API 34] The number of charge cycles the battery has gone through.
        //         "chargingStatus" to (batteryStatus?.getIntExtra("android.os.extra.CHARGING_STATUS", -1) ?: -1),// [API 34] Int value representing the battery charging status
        //         "capacityLevel" to (batteryStatus?.getIntExtra("android.os.extra.CAPACITY_LEVEL", 0) ?: -1),// [API 36] -1: Unsupported, 0: Unknown, 1: Critical, 2: Low, 3: Normal, 4: High, 5: Full
        //         "technology" to (batteryStatus?.getStringExtra("technology") ?: ""),                        // [API 5] String containing the technology of the battery, such as "Li-ion" or "NiMH".
        //         "plugged" to (batteryStatus?.getIntExtra("plugged", 0) ?: -1),                              // [API 5] 0: Unplugged, 1: AC, 2: USB, 4: Wireless
        //         "present" to (batteryStatus?.getBooleanExtra("present", false) ?: false),                   // [API 5] boolean indicating whether a battery is present.
        //         "chargeCounter" to (batteryStatus?.getIntExtra("charge_counter", 0) ?: -1),                 // [API 21] Battery capacity in microampere-hours, as an integer.
        //         "level" to (batteryStatus?.getIntExtra("level", 0) ?: -1),                                  // [API 5] integer between 0 and "scale" representing the current battery level.
        //         "scale" to (batteryStatus?.getIntExtra("scale", 0) ?: -1),                                  // [API 5] integer containing the maximum battery level.
        //     );
        //     /* --- Zebra Codes --- */
        //     appendExtraInfoIfExist(batteryMap, "shutdownLevel", batteryStatus?.extras?.get("shutdown_level"))               // Unknown Zebra shutdown_level: The level at which the battery will shut down
        //     appendExtraInfoIfExist(batteryMap, "maxDischargeTempShutdownLevel", batteryStatus?.extras?.get("max_discharge_temp_shutdown_level"))// Unknown Zebra max_discharge_temp_shutdown_level: The maximum discharge temperature at which the battery will shut down
        //     appendExtraInfoIfExist(batteryMap, "adjustShutdownLevel", batteryStatus?.extras?.get("adjust_shutdown_level"))  // Unknown Zebra adjust_shutdown_level: The level at which the battery will adjust its shutdown
        //     appendExtraInfoIfExist(batteryMap, "minDischargeTempShutdownLevel", batteryStatus?.extras?.get("min_discharge_temp_shutdown_level"))// Unknown Zebra min_discharge_temp_shutdown_level: The minimum discharge temperature at which the battery will shut down
        //     appendExtraInfoIfExist(batteryMap, "invalidCharger", batteryStatus?.extras?.get("invalid_charger"))             // Unknown Zebra invalid_charger: Indicates if the charger is invalid
        //     appendExtraInfoIfExist(batteryMap, "lowLevel", batteryStatus?.extras?.get("low_level"))                         // Unknown Zebra low_level: The low level of the battery
        //     appendExtraInfoIfExist(batteryMap, "seq", batteryStatus?.extras?.get("seq"))                                    // Unknown Zebra seq: The sequence number of the battery
        //     appendExtraInfoIfExist(batteryMap, "criticalLevel", batteryStatus?.extras?.get("critical_level"))               // Unknown Zebra critical_level: The critical level of the battery
        //     appendExtraInfoIfExist(batteryMap, "maxChargingCurrent", batteryStatus?.extras?.get("max_charging_current"))    // Unknown Zebra max_charging_current: The maximum charging current of the battery
        //     appendExtraInfoIfExist(batteryMap, "batteryErrorStatus", batteryStatus?.extras?.get("battery_error_status"))    // Unknown Zebra error status: The error status of the battery
        //     appendExtraInfoIfExist(batteryMap, "batteryLow", batteryStatus?.getBooleanExtra("battery_low", false))          // Unknown Zebra battery_low: Indicates if the battery is low
        //     appendExtraInfoIfExist(batteryMap, "batteryType", batteryStatus?.extras?.get("battery_type"))                   // Unknown Zebra battery type: The type of the battery
        //     appendExtraInfoIfExist(batteryMap, "iconSmall", batteryStatus?.extras?.get("icon-small"))                       // Unknown Zebra icon-small: The icon representing the battery status
        //     appendExtraInfoIfExist(batteryMap, "maxChargingVoltage", batteryStatus?.extras?.get("max_charging_voltage"))    // Unknown Zebra max_charging_voltage: The maximum charging voltage of the battery
        //     appendExtraInfoIfExist(batteryMap, "zcmEnabled", batteryStatus?.getBooleanExtra("zcm_enabled", false))          // Unknown Zebra zcm_enabled: Indicates if the Zebra Charging Management (ZCM) is enabled
        //     appendExtraInfoIfExist(batteryMap, "zcmMode", batteryStatus?.getStringExtra("zcm_mode"))                        // Unknown Zebra zcm_mode: The mode of the Zebra Charging Management (ZCM)
        //     appendExtraInfoIfExist(batteryMap, "zcmExtra", batteryStatus?.getStringExtra("zcm_extra"))                      // Unknown Zebra zcm_extra: Additional information about the Zebra Charging Management (ZCM)
        //     appendExtraInfoIfExist(batteryMap, "batteryUsageNumber", batteryStatus?.extras?.get("battery_usage_numb"))      // [Power] No of charge cycles.
        //     appendExtraInfoIfExist(batteryMap, "batteryUsageDecommissionThreshold", batteryStatus?.extras?.get("battery_usage_decommission_threshold"))// When the "batteryUsageNumber" is greater than or equal to the "batteryUsageDecommissionThreshold", the battery is past its useful life and should be replaced.
        //     appendExtraInfoIfExist(batteryMap, "ratedCapacity", batteryStatus?.extras?.get("ratedcapacity"))                // [Power,Power+] Rated Capacity of the Battery(mAh).
        //     appendExtraInfoIfExist(batteryMap, "mfd", batteryStatus?.getStringExtra("mfd"))                                 // [Power,Power+] Battery Manufacture Date(yyyy-mm-dd).
        //     appendExtraInfoIfExist(batteryMap, "baseCumulativeCharge", batteryStatus?.extras?.get("base_cumulative_charge"))// [Power] Cumulative charge using Zebra charging equipment only(mAh).
        //     appendExtraInfoIfExist(batteryMap, "partNumber", batteryStatus?.getStringExtra("partnumber"))                   // [Power,Power+] Part Number for Battery Prefix is ’21-” or “82-”. Sample: 21-xxxxx-01 Rev. X.
        //     appendExtraInfoIfExist(batteryMap, "serialNumber", batteryStatus?.getStringExtra("serialnumber"))               // [Power,Power+] Battery Serial Number This value shall match the value showing on the physical label of the battery.
        //     appendExtraInfoIfExist(batteryMap, "batteryDecommission", batteryStatus?.extras?.get("battery_decommission"))   // [Power,Power+] Decommission status of the battery (0=Battery good 1=Decommissioned Battery 2=Status Unknown).
        //     appendExtraInfoIfExist(batteryMap, "totalCumulativeCharge", batteryStatus?.extras?.get("total_cumulative_charge"))// [Power+] Cumulative charge using ALL (Zebra or Non-Zebra) charging equipment(mAh).
        //     appendExtraInfoIfExist(batteryMap, "secondsSinceFirstUse", batteryStatus?.extras?.get("seconds_since_first_use"))// [Power+] Number of seconds passed since the battery was placed in a charger/terminal for the first time(secs).
        //     appendExtraInfoIfExist(batteryMap, "presentCapacity", batteryStatus?.extras?.get("present_capacity"))           // [Power+] Maximum amount of charge that could be pulled from the battery under the present discharge conditions if the battery is fully charged(mAh).
        //     appendExtraInfoIfExist(batteryMap, "healthPercentage", batteryStatus?.extras?.get("health_percentage"))         // [Power+] Battery health indicator in percentage (0 to 100)(%).
        //     appendExtraInfoIfExist(batteryMap, "timeToEmpty", batteryStatus?.extras?.get("time_to_empty"))                  // [Power+] Remaining time until the device becomes unusable under current discharge conditions. If the returned value is 65535, then time_to_empty is considered to be unknown(mins).
        //     appendExtraInfoIfExist(batteryMap, "timeToFull", batteryStatus?.extras?.get("time_to_full"))                    // [Power+] Time until battery is fully charged under present charging conditions. If the returned value is 65535, then time_to_full is considered to be unknown(mins).
        //     appendExtraInfoIfExist(batteryMap, "presentCharge", batteryStatus?.extras?.get("present_charge"))               // [Power+] Amount of usable charge remaining in the battery under current discharge conditions(mAh).
        //     appendExtraInfoIfExist(batteryMap, "bkVoltage", batteryStatus?.extras?.get("bk_voltage"))                       // [Backup Battery] Backup battery voltage(mV).
        //     Log.d("Utilities", "getExtendedBatteryStatus: $batteryMap")
        //     return batteryMap;
        // }
        // fun appendExtraInfoIfExist(map: Map<String, Any?>, key: String, value: Any?) {
        //     if (value != null) {
        //         (map as MutableMap)[key] = value
        //     }
        // }
    }
    // fun monitorBatteryStatus(context: Context) {
    //     val batteryStatus: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    //         // For Android 13 (Tiramisu) and higher, use registerReceiver with intent filter and broadcast flag
    //         val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    //         context.registerReceiver(batteryDataReceiver, intentFilter)
    //     } else {
    //         // For older versions, use the deprecated method (still works but not recommended for Tiramisu+)
    //         context.registerReceiver(batteryDataReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    //     }
    //     Log.d("Utilities", "monitorBatteryStatus: ${batteryStatus?.toUri(0)}")
    // }
    // private val batteryDataReceiver = object : BroadcastReceiver() {
    //     override fun onReceive(context: Context, intent: Intent) {
    //         log?.i(TAG, "batteryDataReceiver-onReceive: ${intent.toUri(0)}, ${intent.action}")
    //         if(intent.action.equals(Intent.ACTION_BATTERY_CHANGED)){
    //             log?.i(TAG, "ACTION_BATTERY_CHANGED(${intent.extras})")
    //             scanW.sendResult(getExtendedBatteryStatus(context))
    //                 // remove the start "LABEL-TYPE-" from the labelType and send the remaining string
    //                 // channel.invokeMethod("scan", mapOf("barcode" to intent.getStringExtra(RESULT_BARCODE),"labelType" to labelType?.substring(11)))
    //         }else{
    //             log?.w(TAG, "batteryDataReceiver-onReceive: ${intent.toUri(0)}, ${intent.action} not ACTION_BATTERY_CHANGED")
    //         }
    //     }
    // }
}