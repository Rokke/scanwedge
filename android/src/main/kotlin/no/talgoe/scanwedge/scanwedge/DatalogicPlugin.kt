package no.talgoe.scanwedge.scanwedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

// Hardware plugin for Zebra devices that extends the IHardwarePlugin interface.
class DatalogicPlugin(private val scanW: ScanwedgePlugin, private val log: Logger?) : IHardwarePlugin {
    // private val ACTION_BARCODE_DATA = "com.Datalogic.sample.action.BARCODE_DATA"
    // private val SCANWEDGE_ACTION="no.talgoe.scanwedge.SCAN"
    private val ACTION_CONFIGURATION_COMMIT="com.datalogic.device.intent.action.configuration.COMMIT"
    private val EXTRA_CONFIGURATION_CHANGED_MAP="com.datalogic.device.intent.extra.configuration.CHANGED_MAP"
    private val ACTION_START_DECODE = "com.datalogic.decode.action.START_DECODE"
    private val ACTION_BARCODE_STRING="data"
    private val ACTION_EXTRABARCODE_STRING="extra"
    private val ACTION_BARCODE_TYPE="codeId"

    private val TAG="DatalogicPlugin"
    private val barcodeDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                log?.i(TAG, "onReceive: ${intent.toUri(0)}, ${intent.action}")
                if (ScanwedgePlugin.SCANWEDGE_ACTION == intent.action) {
                    val codeId = intent.getStringExtra(ACTION_BARCODE_TYPE)
                    val barcode = intent.getStringExtra(ACTION_BARCODE_STRING)
                    val extra=intent.extras?.getSerializable(ACTION_EXTRABARCODE_STRING)
                    val timestamp = intent.getStringExtra("timestamp")
                    if(codeId==null){
                        log?.e(TAG, "codeId is null, $barcode, $extra, $timestamp")
                    }else if(extra is ByteArray){   // Prefer extra data if available since barcode data might have keyboard wedge prefix/suffix
                        val extraString=String(extra)
                        log?.i(TAG, "extra: ${extra.size}, $extraString, $codeId, $timestamp")
                        scanW.sendScanResult(ScanResult(extraString, BarcodeTypes.fromDatalogicCode(codeId), codeId, null))
                    }else if(barcode==null){
                        log?.e(TAG, "no barcode or extra data, $codeId, $timestamp")
                    }else{
                        log?.i(TAG, "Barcode Data: $barcode, $codeId, $timestamp")
                        scanW.sendScanResult(ScanResult(barcode, BarcodeTypes.fromDatalogicCode(codeId), codeId, null))
                    }
                }
            } catch (e: Exception) {
                log?.e(TAG, "Error in barcodeDataReceiver: ${e.message}")
            }
        }
    }

    override val apiVersion: String get() = "DATALOGIC"

    override fun initialize(context: Context?):Boolean {
        log?.i(TAG, "DatalogicPlugin.initialize")
        if(context!=null){
            try{
                val filter = IntentFilter(ScanwedgePlugin.SCANWEDGE_ACTION)
                filter.addCategory("SCAN")
                context.registerReceiver(barcodeDataReceiver, filter)
                scanW.sendBroadcast(Intent(ACTION_CONFIGURATION_COMMIT).apply{
                    putExtra(EXTRA_CONFIGURATION_CHANGED_MAP, "WEDGE_KEYBOARD_ENABLE=false,WEDGE_INTENT_ACTION_NAME=${ScanwedgePlugin.SCANWEDGE_ACTION},WEDGE_INTENT_CATEGORY_NAME=SCAN,WEDGE_INTENT_EXTRA_BARCODE_STRING=$ACTION_BARCODE_STRING,WEDGE_INTENT_EXTRA_BARCODE_DATA=$ACTION_EXTRABARCODE_STRING,WEDGE_INTENT_EXTRA_BARCODE_TYPE=$ACTION_BARCODE_TYPE,WEDGE_INTENT_ENABLE=true")
                })
                return true
            } catch (e: Exception) {
                log?.e(TAG, "DatalogicPlugin.initialize, Exception: ${e.message}")
            }
        }
        return false
    }

    override fun dispose(context: Context?) {
        context?.unregisterReceiver(barcodeDataReceiver)
    }

    override fun toggleScanning():Boolean {
        log?.w(TAG, "toggleScan")
        scanW.sendBroadcast(Intent(ACTION_START_DECODE))
        return true
    }

    override fun disableScanner():Boolean{
        log?.w(TAG, "disableScanner")
        scanW.sendBroadcast(Intent(ACTION_CONFIGURATION_COMMIT).apply{
            putExtra(EXTRA_CONFIGURATION_CHANGED_MAP, "WEDGE_INTENT_ENABLE=false")
        })
        return true
    }

    override fun enableScanner():Boolean{
        log?.w(TAG, "enableScanner")
        scanW.sendBroadcast(Intent(ACTION_CONFIGURATION_COMMIT).apply{
            putExtra(EXTRA_CONFIGURATION_CHANGED_MAP, "WEDGE_INTENT_ENABLE=true")
        })
        return true
    }

    override fun createProfile(name: String, enabledBarcodes: List<BarcodePlugin>?, hwConfig: HashMap<String,Any>?, keepDefaults: Boolean):Boolean {
        log?.i(TAG, "createProfile($name, $enabledBarcodes, $hwConfig, $keepDefaults)")
        val datalogicDefaultTypes=BarcodeTypes.datalogicDefaultTypes().toMutableList()
        val properties = ArrayList<String>()
        if(enabledBarcodes!=null){
            for(barcode in enabledBarcodes){
                barcode.datalogicAddToList(properties)
                datalogicDefaultTypes.remove(barcode.type)
            }
        }
        if(!keepDefaults){
            for(barcode in datalogicDefaultTypes){
            log?.d("BarcodePlugin", "removing barcode: $barcode")
            barcode.datalogicDisableBarcode(properties)
        }
        }else{
            log?.d(TAG, "keeping default barcodes")
        }
        log?.d(TAG, "properties: $properties")
        scanW.sendBroadcast(Intent(ACTION_CONFIGURATION_COMMIT).apply{
            putExtra(EXTRA_CONFIGURATION_CHANGED_MAP, properties.joinToString(","))
        })
        return true
    }
}