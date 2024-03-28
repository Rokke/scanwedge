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
class ZebraPlugin(private val context: Context?, private val channel: MethodChannel, private val log: Logger?) : IHardwarePlugin {
    // private val SCANWEDGE_ACTION="no.talgoe.scanwedge.scanwedge.SCAN"
    private val DATAWEDGE_SEND_ACTION = "com.symbol.datawedge.api.ACTION"
    private val NOTIFICATION_ACTION = "com.symbol.datawedge.api.NOTIFICATION_ACTION"
    private val RESULT_SCANNER_IDENTIFIER="com.symbol.datawedge.scanner_identifier"
    private val RESULT_LABEL_TYPE="com.symbol.datawedge.label_type"
    private val RESULT_BARCODE="com.symbol.datawedge.data_string"
    private val RESULT_ACTION = "com.symbol.datawedge.api.RESULT_ACTION"
    private val TAG="ZebraPlugin"
    // Constructor
    init {
      if(context!=null){
        val dataWedgeBroadcastReceiver=createDataWedgeBroadcastReceiver()
        val filter = IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(ScanwedgePlugin.SCANWEDGE_ACTION);
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
    }
    private fun createDataWedgeBroadcastReceiver(): BroadcastReceiver {
        log?.i(TAG, "createDataWedgeBroadcastReceiver")
        return object : BroadcastReceiver() {
          override fun onReceive(context: Context, intent: Intent) {
            log?.i(TAG, "onReceive: ${intent.toUri(0)}, ${intent.action}")
            if(intent.action.equals(ScanwedgePlugin.SCANWEDGE_ACTION)){
              log?.i(TAG, "SCANWEDGE_ACTION(${intent.extras})")
              val labelType=intent.getStringExtra(RESULT_LABEL_TYPE);
              // remove the start "LABEL-TYPE-" from the labelType and send the remaining string
              channel.invokeMethod("scan", mapOf("barcode" to intent.getStringExtra(RESULT_BARCODE),"labelType" to labelType?.substring(11)))
              // channel.invokeMethod("scan", "${intent.getStringExtra(RESULT_BARCODE)}")//, "${intent.getStringExtra(RESULT_LABEL_TYPE)}")
            }
            else if(intent.action.equals(RESULT_ACTION)){
              if(intent.hasExtra("RESULT_LIST")){
                val res = intent.getParcelableArrayListExtra<Bundle>("RESULT_LIST") //getParcelableArrayList
                val jsonList = mapOf("modules" to res?.map { rInfo ->
                  mapOf("module" to rInfo?.getString("MODULE"), "result" to rInfo?.getString("RESULT"))
                })
                // log?.i(TAG, "RESULT_INFO-$rInfo, ${rInfo.keySet().joinToString(", ", "{", "}"){it->"$it=${rInfo[it]}"}}")
                channel.invokeMethod("result", jsonList)
                // res.forEach{ rInfo->
                //   log?.i(TAG, "RESULT_INFO-$rInfo, ${rInfo.keySet().joinToString(", ", "{", "}"){it->"$it=${rInfo[it]}"}}")
                // }
              }
              // log?.i(TAG, "RESULT-${res!!["RESULT_CODE"]}")
              // log?.i(TAG, "RESULT_INFO-$str")
              // if(res is HashMap<String,Any>){
              // }
            }
            else if(intent.action.equals(DATAWEDGE_SEND_ACTION)){
              log?.i(TAG, "DATAWEDGE_SEND_ACTION(${intent.extras})")
            }
            else if(intent.action.equals(NOTIFICATION_ACTION)){
              log?.i(TAG, "NOTIFICATION_ACTION(${intent.extras})")   
            }
            //  Could handle return values from DW here such as RETURN_GET_ACTIVE_PROFILE
            //  or RETURN_ENUMERATE_SCANNERS
          }
        }
      }
    // fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    //     if(call.method == "startScan"){
    //         // Start the scan
    //         result.success("Scanning started")
    //     }else if(call.method == "stopScan"){
    //         // Stop the scan
    //         result.success("Scanning stopped")
    //         }else{
    //           log?.w(TAG, "onMethodCall: no parameter")
    //           result.success(false)
    //         }
    //           }else{
    //         result.notImplemented()
    //     }
    // }
    fun sendCommandBundle(command: String, parameter: HashMap<String,Any>?, shouldRetry:Boolean):Boolean{
        log?.i(TAG, "sendCommandBundle($command, $parameter, $shouldRetry)")
        if(parameter!=null){
            val dwIntent = sendCommandBundleIntent(command, parameter, shouldRetry)
            context!!.sendBroadcast(dwIntent)
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
        context!!.sendBroadcast(dwIntent)
        return true
    }
    override fun toggleScanning():Boolean {
        log?.i(TAG, "toggleScanning")
        return true
    }
    override fun createProfileTest(config: HashMap<String,Any>):Intent {
      log?.i(TAG, "createProfileTest($config)")
      return Intent()
    }
    override fun createProfile(config: HashMap<String,Any>):Boolean {
      log?.i(TAG, "createProfile($config)")
        return true
    }
    fun iterateIntent(parameter: Map<String,Any>):Bundle{
        val bundle=Bundle()
        for((key,value) in parameter){
          // val value=parameter[key]
          if(value is String){
            // if(key.equals("APP_LIST")) bundle.putParcelableArray("APP_LIST", createIntent(value))
            // log?.d(TAG, "putString: $key => $value")
            bundle.putString(key, value)
          }else{
            // log?.d(TAG, "$key: ${value?.javaClass}")
            if(value is List<*>){
              // log?.d(TAG, "List($key)")
              val bundleArray=ArrayList(value.filter{ it is HashMap<*,*>}.map{
                @Suppress("UNCHECKED_CAST")
                iterateIntent(it as HashMap<String,Any>)
              })
              if(bundleArray.isNotEmpty()){
                // log?.d(TAG, "putParcelableArrayList: $key => $bundleArray")
                if(key=="APP_LIST") bundle.putParcelableArray(key, bundleArray.toTypedArray())
                else bundle.putParcelableArrayList(key, bundleArray)
              }
              val stringArray=value.filter{ it is String}.map{ it as String }
              if(stringArray.isNotEmpty()){
                // log?.d(TAG, "putStringArray: $key => $stringArray")
                bundle.putStringArray(key, stringArray.toTypedArray())
              }
              // if(value.first() is HashMap<*,*>){
              //   // if(key=="APP_LIST2") bundle.putParcelableArray("APP_LIST", createIntent("no.test.talgoe"))
              //   // else{
                //     val bundleArray = value.map{ iterateIntent(it as HashMap<String,Any>)} as ArrayList<Bundle>
              //     log?.d(TAG, "putParcelableArrayList: $key => $bundleArray")
              //     bundle.putParcelableArray(key, bundleArray.toTypedArray())
              //   // }
              // }
              // else{
              //   bundle.putStringArray(key, arrayOf("*"))//(value as List<String?>).toTypedArray())
              //   log?.d(TAG, "putStringArray: $key => ${arrayOf("*")}")
              // }
            }else{
              // log?.d(TAG, "putBundle: $key => $value")
              @Suppress("UNCHECKED_CAST")
              bundle.putBundle(key, (value as? HashMap<String, Any>)?.let { iterateIntent(it) })
            }
            // else if(key.startsWith("*")) bundle.putParcelableArray(key.substring(1), arrayOf(iterateIntent(value as HashMap<String,Any>)))
          }
        }
        return bundle
      }
    }