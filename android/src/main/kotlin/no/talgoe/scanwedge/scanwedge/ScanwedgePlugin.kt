package no.talgoe.scanwedge.scanwedge

import androidx.annotation.NonNull
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

import no.talgoe.scanwedge.scanwedge.IHardwarePlugin
import no.talgoe.scanwedge.scanwedge.ZebraPlugin
import no.talgoe.scanwedge.scanwedge.Logger

/** ScanwedgePlugin */
class ScanwedgePlugin(private var log: Logger?=null): FlutterPlugin, MethodCallHandler {
  companion object {
    // private val SCANWEDGE_ACTION="no.talgoe.scanwedge.scanwedge.SCAN"
    val SCANWEDGE_ACTION="no.mobcon.scanwedge.SCAN"
  }
  private val TAG="DataWedgePlugin"
  private lateinit var channel : MethodChannel
  private var context: Context?=null
  private lateinit var dataWedgeBroadcastReceiver: BroadcastReceiver
  private var hardwarePlugin: IHardwarePlugin?=null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    log?.d(TAG, "onAttachedToEngine-Start")
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "scanwedge")
    channel.setMethodCallHandler(this)
    context=flutterPluginBinding.applicationContext
    // hardwarePlugin=ZebraPlugin(context, channel)
  }
  override fun onMethodCall(call: MethodCall, result: Result) {
    log?.d(TAG, "onMethodCall: ${call.method}, ${call.arguments}")
    if (call.method == "getDeviceInfo") {
      result.success("${android.os.Build.MANUFACTURER}|${android.os.Build.MODEL}|${android.os.Build.PRODUCT}|${android.os.Build.VERSION.RELEASE}|${context?.getPackageName()}")
    }else if(call.method == "toggleScanning"){
      hardwarePlugin?.toggleScanning()
      result.success(true)
    }else if(call.method == "createProfile"){
      val argument=call.arguments
        @Suppress("UNCHECKED_CAST")
        val config = argument as HashMap<String, Any>?
        if(config!=null){
          hardwarePlugin?.createProfile(config)
          result.success(true)
        }else{
          log?.e(TAG, "createProfile: Invalid config")
          result.error("INVALID_INPUTPARAMETERS", "Must provide a config", "Invalid config")
        }
    }else if(call.method=="sendCommand"){
      if(hardwarePlugin is ZebraPlugin){
        val command=call.argument<String>("command")
        val parameter=call.argument<String>("parameter")
        // log?.i(TAG, "onMethodCall: sendCommand($command, $parameter)")
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
    }else if(call.method=="initializeDataWedge"){
      // Checking if the device is a Zebra device. Checking that Build.MANUFACTURER or Build.MODEL starts with 'ZEBRA' using uppercase letters.
      log?.i(TAG, "isSupported-manufacturer: ${android.os.Build.MANUFACTURER}, model: ${android.os.Build.MODEL}")
      if(android.os.Build.MANUFACTURER!=null){
        val manufacturer=android.os.Build.MANUFACTURER.uppercase().split(" ")[0]
        // update hardwarePlugin based on manufacturer string
        hardwarePlugin=if(manufacturer=="ZEBRA") ZebraPlugin(context, channel, log) else if(manufacturer=="HONEYWELL") HoneywellPlugin(context, channel, log) else null
        log?.i(TAG, "initializeDataWedge: ${hardwarePlugin?.javaClass?.name}")
        result.success(manufacturer)
      }else{
        log?.i(TAG, "initializeDataWedge: Build.MANUFACTURER is null so test: ${call.arguments}")
        if(call.arguments=="HONEYWELL") hardwarePlugin=HoneywellPlugin(null, null, log)
        log?.d(TAG, "initializeDataWedge: ${hardwarePlugin?.javaClass?.name}")
        result.success("HONEYWELL")
      }
/*    }else if (call.method == "createProfile") {   // Not used anylonger
      val profileName=call.argument<String>("profileName")
      val packageName=call.argument<String>("packageName")
      if(profileName!=null && packageName!=null){
        createAndAssosiateProfile(profileName, packageName)
        result.success(true)
      }else{
        log?.e(TAG, "Invalid profileName or packageName")
        result.error("INVALID_INPUTPARAMETERS", "Must provide a profileName", "Invalid profile or package-Name")
      }*/
    } else {
      log?.w(TAG, "onMethodCall: notImplemented(${call.method})")
      result.notImplemented()
    }
  }
  // fun createAndAssosiateProfile(profileName: String, packageName: String){
  //   log?.i(TAG, "createAndAssosiateProfile($profileName)")
  //   val dwIntent=Intent()
  //   val bundle=Bundle()
  //   bundle.putString("CREATE_PROFILE", profileName)
  //   dwIntent.putExtra("com.symbol.datawedge.api.CREATE_PROFILE", bundle)
  //   context.sendBroadcast(dwIntent)
  //   val dwIntentConfig=Intent()
  //   val profileConfig = Bundle()
  //   profileConfig.putString("PROFILE_NAME", profileName)
  //   profileConfig.putString("PROFILE_ENABLED", "true")
  //   profileConfig.putString("CONFIG_MODE", "UPDATE")
  //   profileConfig.putParcelableArray("APP_LIST", createIntent(packageName))
  //   log?.i(TAG, "createAndAssosiateProfile-$profileConfig")
  //   dwIntentConfig.putExtra("com.symbol.datawedge.api.SET_CONFIG", profileConfig)
  //   context.sendBroadcast(dwIntentConfig)
  // }
  fun createIntent(packageName:String):Array<Bundle>{
    log?.i(TAG, "createIntent($packageName)")
    val bundle=Bundle()
    bundle.putStringArray("ACTIVITY_LIST", arrayOf("*"))
    bundle.putString("PACKAGE_NAME", packageName)
    return arrayOf(bundle)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
