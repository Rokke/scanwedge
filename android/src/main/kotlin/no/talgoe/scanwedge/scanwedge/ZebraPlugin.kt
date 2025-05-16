package no.talgoe.scanwedge.scanwedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle

// Hardware plugin for Zebra devices that extends the IHardwarePlugin interface.
class ZebraPlugin(private val scanW: ScanwedgePlugin, private val log: Logger?) : IHardwarePlugin, IHardwareBatteryPlugin{
    private val DATAWEDGE_SEND_ACTION = "com.symbol.datawedge.api.ACTION"
    private val NOTIFICATION_ACTION = "com.symbol.datawedge.api.NOTIFICATION_ACTION"
    private val RESULT_LABEL_TYPE="com.symbol.datawedge.label_type"
    private val RESULT_BARCODE="com.symbol.datawedge.data_string"
    private val RESULT_ACTION = "com.symbol.datawedge.api.RESULT_ACTION"
    private val TAG="ZebraPlugin"
    private val barcodeDataReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        log?.i(TAG, "onReceive: ${intent.toUri(0)}, ${intent.action}")
        if(intent.action.equals(ScanwedgePlugin.SCANWEDGE_ACTION)){
          log?.i(TAG, "SCANWEDGE_ACTION(${intent.extras})")
          val labelType=intent.getStringExtra(RESULT_LABEL_TYPE)
          val barcode=intent.getStringExtra(RESULT_BARCODE)
          if(barcode==null || labelType==null){
            log?.e(TAG, "barcode is null")
          }else{
            scanW.sendScanResult(ScanResult(barcode, BarcodeTypes.fromZebraCode(labelType), labelType))
            // remove the start "LABEL-TYPE-" from the labelType and send the remaining string
            // channel.invokeMethod("scan", mapOf("barcode" to intent.getStringExtra(RESULT_BARCODE),"labelType" to labelType?.substring(11)))
          }
        }
        else if(intent.action.equals(RESULT_ACTION)){
          if(intent.hasExtra("RESULT_LIST")){
            val res = intent.getParcelableArrayListExtra<Bundle>("RESULT_LIST") //getParcelableArrayList
            val jsonList = mapOf("modules" to res?.map { rInfo ->
              mapOf("module" to rInfo?.getString("MODULE"), "result" to rInfo?.getString("RESULT"))
            })
            scanW.sendResult(jsonList)
            // channel.invokeMethod("result", jsonList)
          }
        }
        else if(intent.action.equals(DATAWEDGE_SEND_ACTION)){
          log?.i(TAG, "DATAWEDGE_SEND_ACTION(${intent.extras})")
        }
        else if(intent.action.equals(NOTIFICATION_ACTION)){
          log?.i(TAG, "NOTIFICATION_ACTION(${intent.extras})")   
        }
      }
    }
    override fun getBatteryValueMap(key: String, value: Any): Pair<String, Any>?{
      /*
        #Intent;action=android.intent.action.BATTERY_CHANGED;launchFlags=0x60000010;i.battery_usage_numb=18;i.battery_error_status=0;i.battery_type=206;S.technology=Li-ion;i.battery_usage_decommission_threshold=400;i.icon-small=17303637;
        i.max_charging_voltage=5000000;S.zcm_mode=;i.health=2;i.shutdown_level=4;B.zcm_enabled=false;i.max_discharge_temp_shutdown_level=600;i.max_charging_current=500000;i.adjust_shutdown_level=100;i.status=2;i.low_level=18;i.ratedcapacity=3300;
        i.plugged=2;B.present=true;S.mfd=2023-02-02;i.seq=5896;S.zcm_extra=;i.charge_counter=3335150;i.level=100;i.base_cumulative_charge=61149;i.scale=100;S.partnumber=BT-000409-50%20R.B;i.critical_level=10;i.temperature=330;i.voltage=4339;
        i.min_discharge_temp_shutdown_level=-200;S.serialnumber=T5362;i.invalid_charger=0;B.battery_low=false;i.battery_decommission=0;end, android.intent.action.BATTERY_CHANGED
      */
      return when(key){
        "shutdown_level"->"shutdownLevel" to value                                              // Unknown Zebra shutdown_level: The level at which the battery will shut down
        "max_discharge_temp_shutdown_level"->"maxDischargeTempShutdownLevel" to (value as Int / 10).toDouble() // Unknown Zebra max_discharge_temp_shutdown_level: The maximum discharge temperature at which the battery will shut down
        "adjust_shutdown_level"->"adjustShutdownLevel" to value                                 // Unknown Zebra adjust_shutdown_level: The level at which the battery will adjust its shutdown
        "min_discharge_temp_shutdown_level"->"minDischargeTempShutdownLevel" to (value as Int/ 10).toDouble() // Unknown Zebra min_discharge_temp_shutdown_level: The minimum discharge temperature at which the battery will shut down
        "invalid_charger"->"invalidCharger" to value                                            // Unknown Zebra invalid_charger: Indicates if the charger is invalid
        "low_level"->"lowLevel" to value                                                        // Unknown Zebra low_level: The low level of the battery
        "seq"->"seq" to value                                                                   // Unknown Zebra seq: The sequence number of the battery
        "critical_level"->"criticalLevel" to value                                              // Unknown Zebra critical_level: The critical level of the battery
        "max_charging_current"->"maxChargingCurrent" to value                                   // Unknown Zebra max_charging_current: The maximum charging current of the battery
        "battery_error_status"->"batteryErrorStatus" to value                                   // Unknown Zebra battery_error_status: The error status of the battery
        "battery_low"->"batteryLow" to value                                                    // Unknown Zebra battery_low: Indicates if the battery is low
        "battery_type"->"batteryType" to value                                                  // Unknown Zebra battery_type: The type of the battery
        "icon-small"->"iconSmall" to value                                                      // Unknown Zebra icon-small: The icon representing the battery status
        "max_charging_voltage"->"maxChargingVoltage" to value                                   // Unknown Zebra max_charging_voltage: The maximum charging voltage of the battery
        "zcm_enabled"->"zcmEnabled" to value                                                    // Unknown Zebra zcm_enabled: Indicates if the Zebra Charging Management (ZCM) is enabled
        "zcm_mode"->"zcmMode" to value                                                          // Unknown Zebra zcm_mode: The mode of the Zebra Charging Management (ZCM)
        "zcm_extra"->"zcmExtra" to value                                                        // Unknown Zebra zcm_extra: Additional information about the Zebra Charging Management (ZCM)
        "battery_usage_numb"->"batteryUsageNumber" to value                                     // [Power] No of charge cycles.
        "battery_usage_decommission_threshold"->"batteryUsageDecommissionThreshold" to value    // When the "batteryUsageNumber" is greater than or equal to the "batteryUsageDecommissionThreshold", the battery is past its useful life and should be replaced.
        "ratedcapacity"->"ratedCapacity" to value                                               // [Power,Power+] Rated Capacity of the Battery(mAh).
        "mfd"->"mfd" to value                                                                   // [Power,Power+] Battery Manufacture Date(yyyy-mm-dd).
        "base_cumulative_charge"->"baseCumulativeCharge" to value                               // [Power] Cumulative charge using Zebra charging equipment only(mAh).
        "partnumber"->"partNumber" to value                                                     // [Power,Power+] Part Number for Battery Prefix is ’21-” or “82-”. Sample: 21-xxxxx-01 Rev. X.
        "serialnumber"->"serialNumber" to value                                                 // [Power,Power+] Battery Serial Number This value shall match the value showing on the physical label of the battery.
        "battery_decommission"->"batteryDecommission" to value                                  // [Power,Power+] Decommission status of the battery (0=Battery good 1=Decommissioned Battery 2=Status Unknown).
        "total_cumulative_charge"->"totalCumulativeCharge" to value                             // [Power+] Cumulative charge using ALL (Zebra or Non-Zebra) charging equipment(mAh).
        "seconds_since_first_use"->"secondsSinceFirstUse" to value                              // [Power+] Number of seconds passed since the battery was placed in a charger/terminal for the first time(secs).
        "present_capacity"->"presentCapacity" to value                                          // [Power+] Maximum amount of charge that could be pulled from the battery under the present discharge conditions if the battery is fully charged(mAh).
        "health_percentage"->"healthPercentage" to value                                        // [Power+] Battery health indicator in percentage (0 to 100)(%).
        "time_to_empty"->"timeToEmpty" to value                                                 // [Power+] Remaining time until the device becomes unusable under current discharge conditions. If the returned value is 65535, then time_to_empty is considered to be unknown(mins).
        "time_to_full"->"timeToFull" to value                                                   // [Power+] Time until battery is fully charged under present charging conditions. If the returned value is 65535, then time_to_full is considered to be unknown(mins).
        "present_charge"->"presentCharge" to value                                              // [Power+] Amount of usable charge remaining in the battery under current discharge conditions(mAh).
        "bk_voltage"->"bkVoltage" to value                                                      // [Backup Battery] Backup battery voltage(mV).
        else->null
      }
    }
    override val apiVersion: String get() = "ZEBRA"
    override fun initialize(context: Context?):Boolean{
      log?.i(TAG, "ZebraPlugin")
      if(context!=null){
        try{
          val filter = IntentFilter()
          filter.addCategory(Intent.CATEGORY_DEFAULT)
          filter.addAction(ScanwedgePlugin.SCANWEDGE_ACTION)
          context.registerReceiver(barcodeDataReceiver, filter, Context.RECEIVER_EXPORTED)
          val filter2=IntentFilter()
          filter2.addCategory("android.intent.category.DEFAULT")
          filter2.addAction(RESULT_ACTION)
          context.registerReceiver(barcodeDataReceiver, filter2, Context.RECEIVER_EXPORTED)
          val filterNotification=IntentFilter()
          filterNotification.addCategory(Intent.CATEGORY_DEFAULT)
          filterNotification.addAction(NOTIFICATION_ACTION)
          context.registerReceiver(barcodeDataReceiver, filterNotification, Context.RECEIVER_EXPORTED)
          return true
        } catch (e: Exception) {
          log?.e(TAG, "Error in ZebraPlugin: ${e.message}")
        }
      }
      return false
    }
    override fun dispose(context: Context?) {
      context?.unregisterReceiver(barcodeDataReceiver)
    }
    override fun toggleScanning():Boolean {
      log?.i(TAG, "toggleScanning")
      scanW.sendBroadcast(Intent().apply{
        setAction("com.symbol.datawedge.api.ACTION")
        putExtra("com.symbol.datawedge.api.SOFT_SCAN_TRIGGER", "START_SCANNING")
      })
      return true
    }
    override fun disableScanner():Boolean {
      log?.i(TAG, "disableScanner")
      scanW.sendBroadcast(Intent().apply{
        setAction("com.symbol.datawedge.api.ACTION")
        putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "SUSPEND_PLUGIN")   // SUSPEND_PLUGIN is "faster" than DISABLE_PLUGIN
        // putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "DISABLE_PLUGIN")
      })
      return true
    }
    override fun enableScanner():Boolean {
      log?.i(TAG, "enableScanner")
      scanW.sendBroadcast(Intent().apply{
        setAction("com.symbol.datawedge.api.ACTION")
        putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "RESUME_PLUGIN")  // RESUME_PLUGIN is "faster" than ENABLE_PLUGIN
        // putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "ENABLE_PLUGIN")
      })
      return true
    }
    override fun createProfile(name: String, enabledBarcodes: List<BarcodePlugin>?, hwConfig: HashMap<String,Any>?, keepDefaults: Boolean):Boolean {
      log?.i(TAG, "createProfile($name, $enabledBarcodes, $hwConfig, $keepDefaults)")
      val zebraConfig=hwConfig?.get("zebra") as? HashMap<*, *>
      val bMain = Bundle()
      bMain.putString("PROFILE_NAME", name)
      bMain.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST")
      bMain.putString("PROFILE_ENABLED", "true")
      bMain.putString("RESET_CONFIG", "true")
      val arrayBundleConfig=ArrayList<Bundle>()
      val bBarcodePlugin = Bundle()
      bBarcodePlugin.putString("PLUGIN_NAME","BARCODE")
      bBarcodePlugin.putString("RESET_CONFIG","true")
      val bParams = Bundle()
      bParams.putString("scanner_selection","auto")
      bParams.putString("scanner_input_enabled","true")
      val sAimType=convertAimTypeToIndex(zebraConfig?.get("aimType") as? String)
      if(sAimType!=null){
        log?.i(TAG, "createProfile: aimType set: $sAimType")
        bParams.putString("aim_type", "$sAimType")
      }
      if(enabledBarcodes!=null){
        log?.i(TAG, "createProfile: enabledBarcodes: ${enabledBarcodes.map{it.type}}")
        val zebraDefaultTypes=BarcodeTypes.zebraDefaultTypes().toMutableList()
        for(barcode in enabledBarcodes){
          zebraDefaultTypes.remove(barcode.type)
          barcode.zebraAddToBundle(bParams)
        }
        if(!keepDefaults){
          for(barcode in zebraDefaultTypes){
            log?.d(TAG, "removing barcode: $barcode")
            barcode.zebraDisableBarcode(bParams)
          }
        }else{
          log?.d(TAG, "keeping default barcodes")
        }
      }
      log?.d(TAG, "createProfile: enabledBarcodes: ${bParams.keySet().joinToString(", ", "{", "}"){ "$it=${bParams[it]}"}}")
      bBarcodePlugin.putBundle("PARAM_LIST", bParams)
      arrayBundleConfig.add(bBarcodePlugin)
      val enableKeyStroke=zebraConfig?.get("enableKeyStroke") ?: false
      log?.d(TAG, "createProfile: enableKeyStroke=$enableKeyStroke")
      val bKeyStroke = Bundle()
      bKeyStroke.putString("PLUGIN_NAME", "KEYSTROKE")
      val bKeyStrokeParams = Bundle()
      bKeyStrokeParams.putString("keystroke_output_enabled", "$enableKeyStroke")
      bKeyStroke.putBundle("PARAM_LIST", bKeyStrokeParams)
      arrayBundleConfig.add(bKeyStroke)
      arrayBundleConfig.add(Bundle().apply {
        putString("PLUGIN_NAME", "INTENT")
        putString("RESET_CONFIG", "true")
        putBundle("PARAM_LIST", Bundle().apply {
          putString("intent_output_enabled", "true")
          putString("intent_action", ScanwedgePlugin.SCANWEDGE_ACTION)
          putInt("intent_delivery", 2)
        })
      })
      val bundleApp1 = Bundle()
      bundleApp1.putString("PACKAGE_NAME", scanW.getPackageName())
      bundleApp1.putStringArray("ACTIVITY_LIST", arrayOf("*"))
      val arrayList=ArrayList<Bundle>()
      arrayList.add(bundleApp1)
      bMain.putParcelableArray("APP_LIST", arrayList.toTypedArray())
      bMain.putParcelableArrayList("PLUGIN_CONFIG", arrayBundleConfig)
      log?.d(TAG, "createProfile: ${bMain.keySet().joinToString(", ", "{", "}"){ "$it=${bMain[it]}"}}")
      val i = Intent().apply {
        action = DATAWEDGE_SEND_ACTION
        putExtra("com.symbol.datawedge.api.SET_CONFIG", bMain)
        putExtra("SEND_RESULT", "LAST_RESULT")
        putExtra("COMMAND_IDENTIFIER", "INTENT_API")
      }
      scanW.sendBroadcast(i)
      return true
    }

    private fun convertAimTypeToIndex(sAimType: String?):Int?{
      return when(sAimType){
        "trigger"->0
        "timedHold"->1
        "timedRelease"->2
        "pressAndRelease"->3
        "presentation"->4
        "continuousRead"->5
        "pressAndSustain"->6
        "pressAndContinue"->7
        "timedContinuous"->8
        else->null
      }
    }

    private fun iterateIntent(parameter: Map<String,Any>):Bundle{
      val bundle=Bundle()
      for((key,value) in parameter){
        if(value is String){
          bundle.putString(key, value)
        }else{
          if(value is List<*>){
            val bundleArray=ArrayList(value.filter{ it is HashMap<*,*>}.map{
              @Suppress("UNCHECKED_CAST")
              iterateIntent(it as HashMap<String,Any>)
            })
            if(bundleArray.isNotEmpty()){
              if(key=="APP_LIST") bundle.putParcelableArray(key, bundleArray.toTypedArray())
              else bundle.putParcelableArrayList(key, bundleArray)
            }
            val stringArray=value.filter{ it is String}.map{ it as String }
            if(stringArray.isNotEmpty()){
              bundle.putStringArray(key, stringArray.toTypedArray())
            }
          }else{
            @Suppress("UNCHECKED_CAST")
            bundle.putBundle(key, (value as? HashMap<String, Any>)?.let { iterateIntent(it) })
          }
        }
      }
      return bundle
    }

    fun sendCommandBundle(command: String, parameter: HashMap<String,Any>?, shouldRetry:Boolean):Boolean{
      log?.i(TAG, "sendCommandBundle($command, $parameter, $shouldRetry)")
      if(parameter!=null){
        val dwIntent = sendCommandBundleIntent(command, parameter, shouldRetry)
        scanW.sendBroadcast(dwIntent)
        return true
      }else{
        log?.w(TAG, "onMethodCall: no parameter")
        return false
      }
    }

  private fun sendCommandBundleIntent(command: String, parameter: HashMap<String,Any>, shouldRetry:Boolean):Intent{
    log?.i(TAG, "sendCommandBundleIntent($command, $parameter, $shouldRetry)")
    val dwIntent = Intent()
    dwIntent.action = DATAWEDGE_SEND_ACTION
    val bundle=iterateIntent(parameter)
    log?.d(TAG, "sendCommandBundleIntent: $command, ${bundle.keySet().joinToString(", ", "{", "}"){it->"$it=${bundle[it]}"}}")
    dwIntent.putExtra(command, bundle)
    if(shouldRetry==true){
      log?.i(TAG, "onMethodCall: SEND_RESULT")
      dwIntent.putExtra("SEND_RESULT", "COMPLETE_RESULT")
      dwIntent.putExtra("COMMAND_IDENTIFIER", "INTENT_API")
    }
    return dwIntent
  }

  fun sendCommand(command: String, parameter: String):Boolean{
      log?.i(TAG, "sendCommand($command, $parameter)")
      val dwIntent = Intent()
      dwIntent.action = DATAWEDGE_SEND_ACTION
      dwIntent.putExtra(command, parameter)
      scanW.sendBroadcast(dwIntent)
      return true
    }
  }