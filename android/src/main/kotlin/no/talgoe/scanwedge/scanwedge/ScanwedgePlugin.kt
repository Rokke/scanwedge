package no.talgoe.scanwedge.scanwedge

import androidx.annotation.NonNull
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.os.Bundle
import android.os.Parcelable

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** ScanwedgePlugin */
class ScanwedgePlugin: FlutterPlugin, MethodCallHandler {
  private val TAG="DataWedgePlugin"
  private val SCANWEDGE_ACTION="no.talgoe.scanwedge.scanwedge.SCAN"
  private val DATAWEDGE_SEND_ACTION = "com.symbol.datawedge.api.ACTION"
  private val NOTIFICATION_ACTION = "com.symbol.datawedge.api.NOTIFICATION_ACTION"
  private val RESULT_SCANNER_IDENTIFIER="com.symbol.datawedge.scanner_identifier"
  private val RESULT_LABEL_TYPE="com.symbol.datawedge.label_type"
  private val RESULT_BARCODE="com.symbol.datawedge.data_string"
  private val RESULT_ACTION = "com.symbol.datawedge.api.RESULT_ACTION"
  private lateinit var channel : MethodChannel
  private lateinit var context: Context

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG, "onAttachedToEngine-Start")
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "scanwedge")
    channel.setMethodCallHandler(this)
    val dataWedgeBroadcastReceiver=createDataWedgeBroadcastReceiver()
    context=flutterPluginBinding.applicationContext
    val filter = IntentFilter();
    filter.addCategory(Intent.CATEGORY_DEFAULT);
    filter.addAction(SCANWEDGE_ACTION);
    context.registerReceiver(dataWedgeBroadcastReceiver, filter);
    val filter2=IntentFilter();
    filter2.addCategory("android.intent.category.DEFAULT");
    filter2.addAction(RESULT_ACTION);
    context.registerReceiver(dataWedgeBroadcastReceiver, filter2);
    val filterNotification=IntentFilter();
    filterNotification.addCategory(Intent.CATEGORY_DEFAULT);
    filterNotification.addAction(NOTIFICATION_ACTION);
    context.registerReceiver(dataWedgeBroadcastReceiver, filterNotification);
  }
  private fun createDataWedgeBroadcastReceiver(): BroadcastReceiver? {
    Log.i(TAG, "createDataWedgeBroadcastReceiver")
    return object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive: ${intent.toUri(0)}, ${intent.action}")
        if(intent.action.equals(SCANWEDGE_ACTION)){
          Log.i(TAG, "SCANWEDGE_ACTION(${intent.extras})")
          channel.invokeMethod("scan", mapOf("barcode" to intent.getStringExtra(RESULT_BARCODE),"labelType" to intent.getStringExtra(RESULT_LABEL_TYPE)))
          // channel.invokeMethod("scan", "${intent.getStringExtra(RESULT_BARCODE)}")//, "${intent.getStringExtra(RESULT_LABEL_TYPE)}")
        }
        else if(intent.action.equals(RESULT_ACTION)){
          if(intent.hasExtra("RESULT_LIST")){
            val res=intent.getSerializableExtra("RESULT_LIST") as ArrayList<Bundle>
            val jsonList=mapOf("modules" to res.map{rInfo->
              // Log.i(TAG, "RESULT_INFO-$rInfo, ${rInfo.keySet().joinToString(", ", "{", "}"){it->"$it=${rInfo[it]}"}}")
              mapOf("module" to rInfo.getString("MODULE"),"result" to rInfo.getString("RESULT"))
            })
            channel.invokeMethod("result", jsonList)
            // res.forEach{ rInfo->
            //   Log.i(TAG, "RESULT_INFO-$rInfo, ${rInfo.keySet().joinToString(", ", "{", "}"){it->"$it=${rInfo[it]}"}}")
            // }
          }
          // Log.i(TAG, "RESULT-${res!!["RESULT_CODE"]}")
          // Log.i(TAG, "RESULT_INFO-$str")
          // if(res is HashMap<String,Any>){
          // }
        }
        else if(intent.action.equals(DATAWEDGE_SEND_ACTION)){
          Log.i(TAG, "DATAWEDGE_SEND_ACTION(${intent.extras})")
        }
        else if(intent.action.equals(NOTIFICATION_ACTION)){
          Log.i(TAG, "NOTIFICATION_ACTION(${intent.extras})")
        }
        //  Could handle return values from DW here such as RETURN_GET_ACTIVE_PROFILE
        //  or RETURN_ENUMERATE_SCANNERS
      }
    }
  }
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    Log.d(TAG, "onMethodCall: ${call.method}, ${call.arguments}")
    if (call.method == "getDeviceInfo") {
      result.success("${android.os.Build.MANUFACTURER}|${android.os.Build.MODEL}|${android.os.Build.PRODUCT}|${android.os.Build.VERSION.RELEASE}|${context.getPackageName()}")
    }else if (call.method == "createProfile") {
      val profileName=call.argument<String>("profileName")
      val packageName=call.argument<String>("packageName")
      if(profileName!=null && packageName!=null){
        createAndAssosiateProfile(profileName, packageName)
        result.success(true)
      }else{
        Log.e(TAG, "Invalid profileName or packageName")
        result.error("INVALID_INPUTPARAMETERS", "Must provide a profileName", "Invalid profile or package-Name")
      }
    }else if(call.method=="sendCommand"){
      val command=call.argument<String>("command")
      val parameter=call.argument<String>("parameter")
      // Log.i(TAG, "onMethodCall: sendCommand($command, $parameter)")
      val dwIntent = Intent()
      dwIntent.action = DATAWEDGE_SEND_ACTION
      dwIntent.putExtra(call.argument<String>("command"), call.argument<String>("parameter"))
      context.sendBroadcast(dwIntent)
      result.success(true)
    }else if(call.method=="sendCommandBundle"){
      val command=call.argument<String>("command")
      val parameter=call.argument<HashMap<String,Any>>("parameter")
      val shouldRetry=call.argument<Boolean>("sendResult")
      // Log.i(TAG, "onMethodCall: sendCommandBundle($command, $parameter, $shouldRetry)")
      if(parameter!=null){
        val dwIntent = Intent()
        dwIntent.action = DATAWEDGE_SEND_ACTION
        val bundle=iterateIntent(parameter)
        // Log.d(TAG, "iterateIntent return: $bundle")
        dwIntent.putExtra(command, bundle)
        if(shouldRetry==true){
          Log.i(TAG, "onMethodCall: SEND_RESULT")
          dwIntent.putExtra("SEND_RESULT", "COMPLETE_RESULT")
          dwIntent.putExtra("COMMAND_IDENTIFIER", "INTENT_API")
        }
        context.sendBroadcast(dwIntent)
        // Log.i(TAG, "onMethodCall: sendCommandBundleOK: $dwIntent, ${dwIntent.extras}")
        result.success(true)
      }else{
        Log.w(TAG, "onMethodCall: no parameter")
        result.success(false)
      }
    } else {
      Log.w(TAG, "onMethodCall: notImplemented(${call.method})")
      result.notImplemented()
    }
  }
  fun createAndAssosiateProfile(profileName: String, packageName: String){
    Log.i(TAG, "createAndAssosiateProfile($profileName)")
    val dwIntent=Intent()
    val bundle=Bundle()
    bundle.putString("CREATE_PROFILE", profileName)
    dwIntent.putExtra("com.symbol.datawedge.api.CREATE_PROFILE", bundle)
    context.sendBroadcast(dwIntent)
    val dwIntentConfig=Intent()
    val profileConfig = Bundle()
    profileConfig.putString("PROFILE_NAME", profileName)
    profileConfig.putString("PROFILE_ENABLED", "true")
    profileConfig.putString("CONFIG_MODE", "UPDATE")
    profileConfig.putParcelableArray("APP_LIST", createIntent(packageName))
    Log.i(TAG, "createAndAssosiateProfile-$profileConfig")
    dwIntentConfig.putExtra("com.symbol.datawedge.api.SET_CONFIG", profileConfig)
    context.sendBroadcast(dwIntentConfig)
  }
  fun iterateIntent(parameter: Map<String,Any>):Bundle{
    val bundle=Bundle()
    for((key,value) in parameter){
      val value=parameter[key]
      if(value is Int){
        // if(key.equals("APP_LIST")) bundle.putParcelableArray("APP_LIST", createIntent(value))
        // Log.d(TAG, "putString: $key => $value")
        bundle.putString(key, value)
      }else if(value is String){
        // if(key.equals("APP_LIST")) bundle.putParcelableArray("APP_LIST", createIntent(value))
        // Log.d(TAG, "putString: $key => $value")
        bundle.putString(key, value)
      }else{
        // Log.d(TAG, "$key: ${value?.javaClass}")
        if(value is List<*>){
          // Log.d(TAG, "List($key)")
          val bundleArray=ArrayList(value.filter{ it is HashMap<*,*>}.map{
            iterateIntent(it as HashMap<String,Any>)
          })
          if(bundleArray.isNotEmpty()){
            // Log.d(TAG, "putParcelableArrayList: $key => $bundleArray")
            if(key=="APP_LIST") bundle.putParcelableArray(key, bundleArray.toTypedArray())
            else bundle.putParcelableArrayList(key, bundleArray)
          }
          val stringArray=value.filter{ it is String}.map{ it as String }
          if(stringArray.isNotEmpty()){
            // Log.d(TAG, "putStringArray: $key => $stringArray")
            bundle.putStringArray(key, stringArray.toTypedArray())
          }
          // if(value.first() is HashMap<*,*>){
          //   // if(key=="APP_LIST2") bundle.putParcelableArray("APP_LIST", createIntent("no.test.talgoe"))
          //   // else{
          //     val bundleArray = value.map{ iterateIntent(it as HashMap<String,Any>)} as ArrayList<Bundle>
          //     Log.d(TAG, "putParcelableArrayList: $key => $bundleArray")
          //     bundle.putParcelableArray(key, bundleArray.toTypedArray())
          //   // }
          // }
          // else{
          //   bundle.putStringArray(key, arrayOf("*"))//(value as List<String?>).toTypedArray())
          //   Log.d(TAG, "putStringArray: $key => ${arrayOf("*")}")
          // }
        }else{
          // Log.d(TAG, "putBundle: $key => $value")
          bundle.putBundle(key, iterateIntent(value as HashMap<String,Any>))
        }
        // else if(key.startsWith("*")) bundle.putParcelableArray(key.substring(1), arrayOf(iterateIntent(value as HashMap<String,Any>)))
      }
    }
    return bundle
  }
  fun createIntent(packageName:String):Array<Bundle>{
    Log.i(TAG, "createIntent($packageName)")
    val bundle=Bundle()
    bundle.putStringArray("ACTIVITY_LIST", arrayOf("*"))
    bundle.putString("PACKAGE_NAME", packageName)
    return arrayOf(bundle)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
