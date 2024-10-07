package no.talgoe.scanwedge.scanwedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

import no.talgoe.scanwedge.scanwedge.Logger

// Hardware plugin for Zebra devices that extends the IHardwarePlugin interface.
class ZebraPlugin(private val scanW: ScanwedgePlugin, private val log: Logger?) : IHardwarePlugin{
    private val DATAWEDGE_SEND_ACTION = "com.symbol.datawedge.api.ACTION"
    private val NOTIFICATION_ACTION = "com.symbol.datawedge.api.NOTIFICATION_ACTION"
    private val RESULT_SCANNER_IDENTIFIER="com.symbol.datawedge.scanner_identifier"
    private val RESULT_LABEL_TYPE="com.symbol.datawedge.label_type"
    private val RESULT_BARCODE="com.symbol.datawedge.data_string"
    private val RESULT_ACTION = "com.symbol.datawedge.api.RESULT_ACTION"
    private val TAG="ZebraPlugin"
    private val barcodeDataReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        log?.i(TAG, "onReceive: ${intent.toUri(0)}, ${intent.action}")
        if(intent.action.equals(ScanwedgePlugin.SCANWEDGE_ACTION)){
          log?.i(TAG, "SCANWEDGE_ACTION(${intent.extras})")
          val labelType=intent.getStringExtra(RESULT_LABEL_TYPE);
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
    override val apiVersion: String get() = "ZEBRA"
    override fun initialize(context: Context?):Boolean{
      log?.i(TAG, "ZebraPlugin")
      if(context!=null){
        try{
          val filter = IntentFilter()
          filter.addCategory(Intent.CATEGORY_DEFAULT)
          filter.addAction(ScanwedgePlugin.SCANWEDGE_ACTION)
          context.registerReceiver(barcodeDataReceiver, filter)
          val filter2=IntentFilter()
          filter2.addCategory("android.intent.category.DEFAULT")
          filter2.addAction(RESULT_ACTION)
          context.registerReceiver(barcodeDataReceiver, filter2)
          val filterNotification=IntentFilter()
          filterNotification.addCategory(Intent.CATEGORY_DEFAULT)
          filterNotification.addAction(NOTIFICATION_ACTION)
          context.registerReceiver(barcodeDataReceiver, filterNotification)
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
      val zebraConfig=hwConfig?.get("zebra") as? HashMap<String,Any>
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
      log?.d(TAG, "createProfile: enabledBarcodes: ${bParams.keySet().joinToString(", ", "{", "}"){it->"$it=${bParams[it]}"}}")
      bBarcodePlugin.putBundle("PARAM_LIST", bParams)
      arrayBundleConfig.add(bBarcodePlugin)
      val enableKeyStroke=zebraConfig?.get("enableKeyStroke") ?: false
      log?.d(TAG, "createProfile: enableKeyStroke=$enableKeyStroke")
      val bKeyStroke = Bundle()
      bKeyStroke.putString("PLUGIN_NAME", "KEYSTROKE")
      val bKeyStrokeParams = Bundle()
      bKeyStrokeParams.putString("keystroke_output_enabled", "${enableKeyStroke}")
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
      log?.d(TAG, "createProfile: ${bMain.keySet().joinToString(", ", "{", "}"){it->"$it=${bMain[it]}"}}")
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
    fun iterateIntent(parameter: Map<String,Any>):Bundle{
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
    fun sendCommandBundleIntent(command: String, parameter: HashMap<String,Any>, shouldRetry:Boolean):Intent{
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