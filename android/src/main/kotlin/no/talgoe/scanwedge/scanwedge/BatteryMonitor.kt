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
    fun stopMonitoringBatteryStatus(context: Context) {
        dispose(context)
    }

    fun dispose(context: Context?) {
        if (isReceiverRegistered) {
            context?.unregisterReceiver(batteryDataReceiver)
            isReceiverRegistered = false
            Log.d(TAG, "BatteryMonitor: Receiver unregistered")
        }
    }
/*
    TC26:
        #Intent;action=android.intent.action.BATTERY_CHANGED;launchFlags=0x60000010;i.battery_usage_numb=18;i.battery_error_status=0;i.battery_type=206;S.technology=Li-ion;i.battery_usage_decommission_threshold=400;i.icon-small=17303637;
        i.max_charging_voltage=5000000;S.zcm_mode=;i.health=2;i.shutdown_level=4;B.zcm_enabled=false;i.max_discharge_temp_shutdown_level=600;i.max_charging_current=500000;i.adjust_shutdown_level=100;i.status=2;i.low_level=18;i.ratedcapacity=3300;
        i.plugged=2;B.present=true;S.mfd=2023-02-02;i.seq=5896;S.zcm_extra=;i.charge_counter=3335150;i.level=100;i.base_cumulative_charge=61149;i.scale=100;S.partnumber=BT-000409-50%20R.B;i.critical_level=10;i.temperature=330;i.voltage=4339;
        i.min_discharge_temp_shutdown_level=-200;S.serialnumber=T5362;i.invalid_charger=0;B.battery_low=false;i.battery_decommission=0;end, android.intent.action.BATTERY_CHANGED
    TC27:
        #Intent;action=android.intent.action.BATTERY_CHANGED;launchFlags=0x60000010;i.battery_usage_numb=20;i.battery_error_status=0;i.battery_type=206;S.technology=Li-ion;i.battery_usage_decommission_threshold=400;i.icon-small=17303632;
        i.max_charging_voltage=5000000;S.zcm_mode=;i.health=2;i.shutdown_level=7;B.zcm_enabled=false;i.max_discharge_temp_shutdown_level=600;i.max_charging_current=3000000;i.adjust_shutdown_level=42949672;i.status=5;i.low_level=15;
        i.ratedcapacity=3800;i.plugged=1;B.present=true;S.mfd=2024-01-06;i.seq=2783;S.zcm_extra=;i.charge_counter=3807371;i.level=100;i.base_cumulative_charge=77102;i.scale=100;S.partnumber=BT-000473-0020%20R.A;i.critical_level=10;
        i.temperature=230;i.voltage=4269;i.min_discharge_temp_shutdown_level=-200;S.serialnumber=T2198;i.backup_power_type=0;B.usb_liquid_status=false;i.invalid_charger=0;B.battery_low=false;i.battery_decommission=0;end
    TC56:
        #Intent;action=android.intent.action.BATTERY_CHANGED;launchFlags=0x60000010;i.screenactiveduration=580;i.battery_type=201;i.battery_percent_decommission_threshold=80;S.technology=Li-ion;i.maxbatterytemp=600;i.icon-small=17303451;
        i.max_charging_voltage=4656640;i.present_capacity=3586;i.time_to_empty=7;i.seconds_since_first_use=121303124;i.health=2;i.shutdown_level=4;i.max_discharge_temp_shutdown_level=600;i.total_cumulative_charge=240323;i.max_charging_current=500000;
        i.minbatterytemp=-140;i.status=2;i.low_level=18;i.chargingduration=2091;i.ratedcapacity=4050;i.plugged=2;i.timesincelastcharge=2091;B.present=true;S.mfd=2020-01-11;i.seq=411;i.charge_counter=0;i.level=3;i.base_cumulative_charge=215175;
        i.scale=100;S.partnumber=BT-000314-01%20R.F;i.critical_level=10;i.temperature=377;i.voltage=3177;i.min_discharge_temp_shutdown_level=-140;i.lastchargelength=0;i.timeremaining=506;i.rebootcount=29;S.serialnumber=T5756;i.bkvoltage=0;
        i.deviceoffduration=0;i.health_percentage=97;i.time_to_full=65535;i.deviceonduration=2091;i.timesincelastfullcharge=2091;i.invalid_charger=0;i.present_charge=171;i.battery_decommission=0;end, android.intent.action.BATTERY_CHANGED
    Pixel9:
        #Intent;action=android.intent.action.BATTERY_CHANGED;launchFlags=0x60000010;i.android.os.extra.CAPACITY_LEVEL=3;S.technology=Li-ion;i.icon-small=17303919;i.max_charging_voltage=5000000;i.health=2;
        i.max_charging_current=500000;i.status=4;i.plugged=2;B.present=true;i.android.os.extra.CHARGING_STATUS=4;i.seq=1343;i.charge_counter=3912500;i.level=80;i.scale=100;i.temperature=290;i.voltage=4224;
        i.android.os.extra.CYCLE_COUNT=139;i.invalid_charger=0;B.battery_low=false;end, android.intent.action.BATTERY_CHANGED
    CT37:
        #Intent;action=android.intent.action.BATTERY_CHANGED;launchFlags=0x60000010;S.technology=Li-ion;i.icon-small=17303692;i.max_charging_voltage=5000000;i.health=2;i.max_charging_current=900000;i.status=2;B.BatterySwapping=false;
        i.plugged=2;B.present=true;i.android.os.extra.CHARGING_STATUS=0;i.seq=92;i.charge_counter=310251;i.level=10;i.scale=100;i.temperature=280;i.voltage=3735;i.android.os.extra.CYCLE_COUNT=0;i.invalid_charger=0;B.battery_low=true;
        i.backup_battery_voltage=3933;end, android.intent.action.BATTERY_CHANGED
    M35:
        #Intent;action=android.intent.action.BATTERY_CHANGED;launchFlags=0x60000010;S.technology=Li-ion;i.icon-small=17303620;i.max_charging_voltage=5000000;i.health=2;i.max_charging_current=900000;i.status=2;i.plugged=2;B.present=true;
        i.seq=463;i.charge_counter=356000;i.level=8;i.scale=100;i.temperature=309;i.voltage=3701;i.invalid_charger=0;B.battery_low=true;end, android.intent.action.BATTERY_CHANGED
    SM-G525F XCover5(fully charged in charger):
        current_event: 1073774592(0x40008000), misc_event: 0, online: 39, pogo_plugged: 2, capacity: 280000, current_now: -7, charge_type: 0, hv_charger: false, power_sharing: false, charger_type: 0
SAMSUNG: https://github.com/kdrag0n/osrc_dream/blob/master/drivers/battery_v2/include/sec_battery.h#L56
#define SEC_BAT_CURRENT_EVENT_NONE					0x0000
#define SEC_BAT_CURRENT_EVENT_AFC					0x0001
#define SEC_BAT_CURRENT_EVENT_CHARGE_DISABLE		0x0002
#define SEC_BAT_CURRENT_EVENT_SKIP_HEATING_CONTROL	0x0004
#define SEC_BAT_CURRENT_EVENT_LOW_TEMP_SWELLING		0x0010
#define SEC_BAT_CURRENT_EVENT_HIGH_TEMP_SWELLING	0x0020
#if defined(CONFIG_ENABLE_100MA_CHARGING_BEFORE_USB_CONFIGURED)
#define SEC_BAT_CURRENT_EVENT_USB_100MA			0x0040
#else
#define SEC_BAT_CURRENT_EVENT_USB_100MA			0x0000
#endif
#define SEC_BAT_CURRENT_EVENT_LOW_TEMP			0x0080
#define SEC_BAT_CURRENT_EVENT_SWELLING_MODE		(SEC_BAT_CURRENT_EVENT_LOW_TEMP_SWELLING | SEC_BAT_CURRENT_EVENT_LOW_TEMP | SEC_BAT_CURRENT_EVENT_HIGH_TEMP_SWELLING)
#define SEC_BAT_CURRENT_EVENT_USB_SUPER			0x0100
#define SEC_BAT_CURRENT_EVENT_CHG_LIMIT			0x0200
#define SEC_BAT_CURRENT_EVENT_CALL			0x0400
#define SEC_BAT_CURRENT_EVENT_SLATE			0x0800
#define SEC_BAT_CURRENT_EVENT_VBAT_OVP			0x1000
#define SEC_BAT_CURRENT_EVENT_VSYS_OVP			0x2000
#define SEC_BAT_CURRENT_EVENT_WPC_VOUT_LOCK		0x4000
#define SEC_BAT_CURRENT_EVENT_AICL			0x8000
#define SEC_BAT_CURRENT_EVENT_HV_DISABLE		0x10000
#define SEC_BAT_CURRENT_EVENT_SELECT_PDO		0x20000
#define BATT_MISC_EVENT_UNDEFINED_RANGE_TYPE	0x00000001
#define BATT_MISC_EVENT_WIRELESS_BACKPACK_TYPE	0x00000002
#define BATT_MISC_EVENT_TIMEOUT_OPEN_TYPE	0x00000004
#define BATT_MISC_EVENT_BATT_RESET_SOC		0x00000008

 */
    private val batteryDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            log?.d(TAG, "batteryDataReceiver-onReceive: ${intent.toUri(0)}, ${intent.action}")
            if (intent.action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                // val batteryStatus = mutableMapOf<String, Any>()
                // val manufacturer = scanW.fetchManufacturer()
                // if(manufacturer!=null) batteryStatus["deviceManufacturer"] = manufacturer
                // val extraBatteryStatus = mutableMapOf<String, Any>()
                // val keys = intent.extras!!.keySet()
                // for (key in keys) {
                //     val value = intent.extras!!.get(key)
                //     if (value != null) {
                //         when (key) {
                //             "android.os.extra.CAPACITY_LEVEL" -> batteryStatus["capacityLevel"] = value         // [API 36] -1: Unsupported, 0: Unknown, 1: Critical, 2: Low, 3: Normal, 4: High, 5: Full
                //             "android.os.extra.CYCLE_COUNT" -> batteryStatus["cycleCount"] = value               // [API 34] The number of charge cycles the battery has gone through.
                //             "android.os.extra.CHARGING_STATUS" -> batteryStatus["chargingStatus"] = value       // [API 34] Int value representing the battery charging status
                //             "status" -> batteryStatus["status"] = value                                         // [API 5] 1: Unknown, 2: Charging, 3: Discharging, 4: Not charging, 5: Full
                //             "health" -> batteryStatus["health"] = value                                         // [API 5] 1: Unknown, 2: Good, 3: Overheat, 4: Dead, 5: Over voltage, 6: Unspecified failure, 7: Cold
                //             "temperature" -> batteryStatus["temperature"] = (value as Int / 10).toDouble()      // [API 5] integer containing the current battery temperature in tenths of a degree Celsius.
                //             "voltage" -> batteryStatus["voltage"] = (value as Int / 1000).toDouble()            // [API 5] integer containing the current battery voltage in millivolts.
                //             "level" -> batteryStatus["level"] = value                                           // [API 5] integer between 0 and "scale" representing the current battery level.
                //             "scale" -> batteryStatus["scale"] = value                                           // [API 5] integer containing the maximum battery level.
                //             "plugged" -> batteryStatus["plugged"] = value                                       // [API 5] 0: Unplugged, 1: AC, 2: USB, 4: Wireless
                //             "technology" -> batteryStatus["technology"] = value                                 // [API 5] String containing the technology of the battery, such as "Li-ion" or "NiMH".
                //             "present" -> batteryStatus["present"] = value                                       // [API 5] boolean indicating whether a battery is present.
                //             "charge_counter" -> batteryStatus["chargeCounter"] = value                          // [API 21] Battery capacity in microampere-hours, as an integer.
                //             "icon-small"->batteryStatus["iconSmall"] = value                                     // [API 5] integer containing the resource ID of a small status bar icon indicating the current battery state
                //             "max_charging_voltage"->batteryStatus["maxChargingVoltage"] = value                  // Unknown: The maximum charging voltage of the battery
                //             "max_charging_current"->batteryStatus["maxChargingCurrent"] = value                  // Unknown: The maximum charging current of the battery
                //             "seq"->batteryStatus["seq"] = value                                                  // Unknown: The sequence number of the battery
                //             "invalid_charger"->batteryStatus["invalidCharger"] = value                           // Unknown: Int value set to nonzero if an unsupported charger is attached to the device
                //             "battery_low"->batteryStatus["batteryLow"] = value                                   // Unknown: Indicates if the battery is low

                //             // Samsung
                //             "current_event" -> batteryStatus["currentEvent"] = value                               // Unknown XCover [Power+] Current event. 0x40000000 = Battery is charging, 0x80000000 = Battery is discharging, 0x20000000 = Battery is idle, 0x10000000 = Battery is in a fault state
                //             "misc_event" -> batteryStatus["miscEvent"] = value                                     // Unknown XCover
                //             "online" -> batteryStatus["online"] = value                                            // Unknown XCover
                //             "pogo_plugged" -> batteryStatus["pogoPlugged"] = value                                 // Unknown XCover
                //             "capacity" -> batteryStatus["capacity"] = value                                        // Unknown XCover
                //             "current_now" -> batteryStatus["currentNow"] = value                                   // Unknown XCover
                //             "charge_type" -> batteryStatus["chargeType"] = value                                   // Unknown XCover
                //             "hv_charger" -> batteryStatus["hvCharger"] = value                                     // Unknown XCover
                //             "power_sharing" -> batteryStatus["powerSharing"] = value                               // Unknown XCover
                //             "charger_type" -> batteryStatus["chargerType"] = value                                 // Unknown XCover

                //             else -> {
                //                 val fromHardwarePlugin = scanW.checkHardwarePlugin(key, value)
                //                 if (fromHardwarePlugin != null) {
                //                     fromHardwarePlugin.let { (key, value) ->
                //                         batteryStatus[key] = value
                //                     }
                //                 } else {
                //                     log?.w(TAG, "batteryDataReceiver-onReceive: ${key} not handled")
                //                     extraBatteryStatus[key] = value
                //                 }
                //             }
                //         }
                //     }
                // }
                // log?.d(TAG, "batteryDataReceiver-onReceive: ${batteryStatus}")
                // batteryStatus["extraMap"] = extraBatteryStatus
                // if(batteryStatus["extraMap"] is Map<*, *>) batteryStatus["intentLog"] = intent.toUri(0)
                scanW.sendBatteryStatus(Utilities.createExtendedBatteryMap(intent, scanW, log))
            } else {
                log?.w(TAG, "batteryDataReceiver-onReceive: ${intent.toUri(0)}, ${intent.action} not ACTION_BATTERY_CHANGED")
            }
        }
    }
}

interface IHardwareBatteryPlugin {
    fun getBatteryValueMap(key: String, value: Any): Pair<String, Any>?
}
