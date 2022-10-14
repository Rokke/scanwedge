package no.talgoe.scanwedge.scanwedge

import androidx.annotation.NonNull
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** ScanwedgePlugin */
class ScanwedgePlugin: FlutterPlugin, MethodCallHandler {
  private val TAG="DataWedgePlugin"
  private val SCANWEDGE_ACTION="no.talgoe.datawedge"
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "scanwedge")
    channel.setMethodCallHandler(this)
    val filter = IntentFilter();
    filter.addCategory(Intent.CATEGORY_DEFAULT);
    filter.addAction(SCANWEDGE_ACTION);
    val dataWedgeBroadcastReceiver=createDataWedgeBroadcastReceiver()
  }
  private fun createDataWedgeBroadcastReceiver(): BroadcastReceiver? {
    Log.i(TAG, "createDataWedgeBroadcastReceiver")
    println("test1432")
    return object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        println("onReceive: ${intent.action}")
        // if (intent.action.equals(NOTIFICATION_ACTION)){
        //   println("NOTIFICATION_ACTION")
        // } else if (intent.action.equals(PROFILE_INTENT_ACTION)) {
        //   //  A barcode has been scanned
        //   var scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
        //   var symbology = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_LABEL_TYPE)
        //   // var date = Calendar.getInstance().getTime()
        //   // var df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        //   // var dateTimeString = df.format(date)
        //   var currentScan = Scan(scanData, symbology, "2022-02-02 14:14:14")//dateTimeString);
        //   events?.success(currentScan.toJson())
        if(intent.action.equals(SCANWEDGE_ACTION)){
          Log.i(TAG, "ACTION intent: $intent")
          Log.i(TAG, "ACTION intent: ${intent.toUri(0)}")
          // Bundle bundle = intent.getExtras();
          // if (bundle != null) {
          //     for (String key : bundle.keySet()) {
          //         Log.i(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
          //     }
          // }
        }
        //  Could handle return values from DW here such as RETURN_GET_ACTIVE_PROFILE
        //  or RETURN_ENUMERATE_SCANNERS
      }
    }
  }
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }else if (call.method == "createProfile") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
