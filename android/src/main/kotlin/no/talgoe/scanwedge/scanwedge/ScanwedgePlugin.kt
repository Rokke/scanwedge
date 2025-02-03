package no.talgoe.scanwedge.scanwedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

interface IScanHandler {
  fun sendScanResult(scanResult: ScanResult)
  fun sendBroadcast(intent: Intent)
  fun sendResult(data: Any?)
  fun getPackageName(): String
}

/** ScanwedgePlugin */
class ScanwedgePlugin(private var log: Logger?=null): FlutterPlugin, MethodCallHandler, IScanHandler {
  companion object {
    const val SCANWEDGE_ACTION="no.mobcon.scanwedge.SCAN"
  }
  private val TAG="DataWedgePlugin"
  private lateinit var channel : MethodChannel
  private var context: Context?=null
  private var hardwarePlugin: IHardwarePlugin?=null

  override fun sendScanResult(scanResult: ScanResult){
    log?.i(TAG, "sendScanResult: $scanResult")
    channel.invokeMethod("scan", scanResult.toMap())
  }

  override fun sendResult(data: Any?){
    log?.i(TAG, "sendResult: $data")
    channel.invokeMethod("result", data)
  }

  override fun getPackageName(): String{
    return context?.packageName ?:""
  }

  override fun sendBroadcast(intent: Intent){
    log?.i(TAG, "sendBroadcast: ${intent.toUri(0)}")
    val resultReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val resultCode = resultCode
            val resultData = resultData
            val resultExtras = getResultExtras(true)
            log?.i(TAG, "Broadcast result: code=$resultCode, data=$resultData, extras=$resultExtras")
        }
    }
    context?.sendOrderedBroadcast(intent, null, resultReceiver, null, 0, null, null)
  }

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    log=AndroidLogger()
    log?.d(TAG, "onAttachedToEngine-Start")
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "scanwedge")
    channel.setMethodCallHandler(this)
    context=flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    log?.d(TAG, "onMethodCall: ${call.method}, ${call.arguments}")
    if (call.method == "getDeviceInfo") {
      result.success("${android.os.Build.MANUFACTURER}|${android.os.Build.MODEL}|${android.os.Build.PRODUCT}|${android.os.Build.VERSION.RELEASE}|${context?.packageName}|${Settings.Global.getString(context?.contentResolver, Settings.Global.DEVICE_NAME)}")
    }else if(call.method == "toggleScanning"){
      hardwarePlugin?.toggleScanning()
      result.success(true)
    }else if(call.method == "enableScanner"){
      result.success(hardwarePlugin?.enableScanner())
    }else if(call.method == "disableScanner"){
      result.success(hardwarePlugin?.disableScanner())
    }else if(call.method == "createProfile"){
      val argument=call.arguments
      val config = argument as HashMap<String, Any>?
      if(config!=null){
        val barcodeList=ArrayList<BarcodePlugin>()
        if(config["barcodes"] is List<*>){
          val barcodes=config["barcodes"] as List<*>
          log?.i(TAG, "createProfile: barcodes: $barcodes")
          // convert the list of barcodes to a list of BarcodePlugin
          for(barcode in barcodes){
            val barcodePlugin=BarcodePlugin.createBarcodePlugin(barcode)
            if(barcodePlugin!=null) barcodeList.add(barcodePlugin)
          }
        }
        hardwarePlugin?.createProfile(config["name"] as String, barcodeList, config["hwConfig"] as? HashMap<String, Any>, config["keepDefaults"] as Boolean? ?: true)
        result.success(true)
      }else{
        log?.e(TAG, "createProfile: Invalid config")
        result.error("INVALID_INPUTPARAMETERS", "Must provide a config", "Invalid config")
      }
    }else if(call.method=="initializeDataWedge"){
      // Checking if the device is a Zebra device. Checking that Build.MANUFACTURER or Build.MODEL starts with 'ZEBRA' using uppercase letters.
      log?.i(TAG, "isSupported-manufacturer: ${android.os.Build.MANUFACTURER}, model: ${android.os.Build.MODEL}")
      val manufacturer = android.os.Build.MANUFACTURER?.uppercase()?.split(" ")?.get(0)
      if (manufacturer == null) {
        result.error("MANUFACTURER_NOT_FOUND", "Manufacturer not found", "Manufacturer not found")
        return
      }

      // update hardwarePlugin based on manufacturer string
      hardwarePlugin = when (manufacturer) {
        "ZEBRA" -> ZebraPlugin(this, log)
        "HONEYWELL" -> HoneywellPlugin(this, log)
        "DATALOGIC" -> DatalogicPlugin(this, log)
        "NEWLAND" -> NewlandPlugin(this, log)
        else -> null
      }
      hardwarePlugin?.initialize(context)
      log?.i(TAG, "initializeDataWedge: ${hardwarePlugin?.javaClass?.name}")
      result.success("${hardwarePlugin?.apiVersion}|${android.os.Build.MANUFACTURER}|${android.os.Build.MODEL}|${android.os.Build.PRODUCT}|${android.os.Build.VERSION.RELEASE}|${context?.packageName}|${Settings.Global.getString(context?.contentResolver, Settings.Global.DEVICE_NAME)}")
    }else if(call.method=="sendCommand"){
      if(hardwarePlugin is ZebraPlugin){
        val command=call.argument<String>("command")
        val parameter=call.argument<String>("parameter")
        val zebraPlugin=hardwarePlugin as ZebraPlugin
        result.success(zebraPlugin.sendCommand(command!!, parameter!!))
      }else{
        log?.e(TAG, "sendCommand: hardwarePlugin is wrong type: ${hardwarePlugin?.javaClass?.name}")
        result.error("HARDWARE_NOT_SUPPORTED", "Hardware not supported", "Hardware not supported")
      }
    }else if(call.method=="sendCommandBundle"){
      if(hardwarePlugin is ZebraPlugin){
        val zebraPlugin=hardwarePlugin as ZebraPlugin
        val command=call.argument<String>("command")
        val parameter=call.argument<HashMap<String,Any>>("parameter")
        val shouldRetry=call.argument<Boolean>("sendResult")
        log?.i(TAG, "onMethodCall: sendCommandBundle($command, $parameter, $shouldRetry)")
        if(parameter!=null){
          result.success(zebraPlugin.sendCommandBundle(command!!, parameter, shouldRetry!!))
        }else{
          result.error("INVALID_INPUTPARAMETERS", "Must provide a parameter", "Invalid parameter")
        }
      }else{
        log?.e(TAG, "sendCommandBundle: hardwarePlugin is wrong type: ${hardwarePlugin?.javaClass?.name}")
        result.error("HARDWARE_NOT_SUPPORTED", "Hardware not supported", "Hardware not supported")
      }
    } else {
      log?.w(TAG, "onMethodCall: notImplemented(${call.method})")
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    hardwarePlugin?.dispose(context)
    channel.setMethodCallHandler(null)
  }
}
