package no.talgoe.scanwedge.scanwedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

// Hardware plugin for Zebra devices that extends the IHardwarePlugin interface.
class HoneywellPlugin(private val scanW: ScanwedgePlugin, private val log: Logger?) : IHardwarePlugin {
    // private val ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA"
    // private val SCANWEDGE_ACTION="no.talgoe.scanwedge.SCAN"
    private val ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER"
    private val ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER"
    private val EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER"
    private val EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE"
    private val EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES"
    private val TAG="HoneywellPlugin"
    private val barcodeDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
        try {
            log?.i(TAG, "onReceive: ${intent.toUri(0)}, ${intent.action}")
            if (ScanwedgePlugin.SCANWEDGE_ACTION == intent.action) {
            val version = intent.getIntExtra("version", 0)
            if (version >= 1) {
                val aimId = intent.getStringExtra("aimId")
                val charset = intent.getStringExtra("charset")
                val codeId = intent.getStringExtra("codeId")
                val barcode = intent.getStringExtra("data")
                // val dataBytes = intent.getByteArrayExtra("dataBytes")
                val timestamp = intent.getStringExtra("timestamp")
                if(barcode==null || codeId==null){
                    log?.e(TAG, "barcode is null")
                }else{
                    log?.i(TAG, "Barcode Data: $barcode, $charset, $codeId, $aimId, $timestamp")
                    scanW.sendScanResult(ScanResult(barcode, BarcodeTypes.fromHoneywellCode(codeId), codeId))
                }
                }else{
                log?.w(TAG, "onReceive: invalid version $version")
                }
            }
        } catch (e: Exception) {
            log?.e(TAG, "Error in barcodeDataReceiver: ${e.message}")
        }
        }
    }
    override val apiVersion: String get() = "HONEYWELL"

    override fun initialize(context: Context?):Boolean {
        log?.i(TAG, "HoneywellPlugin")
        if(context!=null){
            try{
                val filter = IntentFilter(ScanwedgePlugin.SCANWEDGE_ACTION)
                filter.addCategory("SCAN")
                context.registerReceiver(barcodeDataReceiver, filter)
                scanW.sendBroadcast(Intent(ACTION_RELEASE_SCANNER).apply{ setPackage("com.intermec.datacollectionservice") })
                return true
            } catch (e: Exception) {
                log?.e(TAG, "Error in HoneywellPlugin: ${e.message}")
            }
        }
        return false
    }

    override fun dispose(context: Context?) {
        scanW.sendBroadcast(Intent(ACTION_RELEASE_SCANNER).setPackage("com.intermec.datacollectionservice"))
        context?.unregisterReceiver(barcodeDataReceiver)
    }

    override fun toggleScanning():Boolean {
        log?.w(TAG, "toggleScan")
        scanW.sendBroadcast(Intent("com.honeywell.aidc.action.ACTION_CONTROL_SCANNER").apply{
            setPackage("com.intermec.datacollectionservice")
            putExtra("com.honeywell.aidc.extra.EXTRA_SCAN", true)
        })
        return true
    }

    override fun disableScanner():Boolean{
        log?.w(TAG, "disableScanner")
        scanW.sendBroadcast(Intent(ACTION_CLAIM_SCANNER).apply{
            setPackage("com.intermec.datacollectionservice")
            putExtra(EXTRA_PROPERTIES, Bundle().apply{
                putBoolean("TRIG_ENABLE", false)
            })
        })
        return true
    }
    
    override fun enableScanner():Boolean{
        log?.w(TAG, "enableScanner")
        scanW.sendBroadcast(Intent(ACTION_CLAIM_SCANNER).apply{
            setPackage("com.intermec.datacollectionservice")
            putExtra(EXTRA_PROPERTIES, Bundle().apply{
                putBoolean("TRIG_ENABLE", true)
            })
        })
        return true
    }

    override fun createProfile(name: String, enabledBarcodes: List<BarcodePlugin>?, hwConfig: HashMap<String,Any>?, keepDefaults: Boolean):Boolean {
        log?.i(TAG, "createProfile($name, $enabledBarcodes, $hwConfig, $keepDefaults)")
        @Suppress("UNCHECKED_CAST")
        val extraConfig = hwConfig?.get("honeywell") as HashMap<String, Boolean>
        val properties = Bundle().apply{
            putBoolean("DPR_DATA_INTENT", true)
            putString("DPR_DATA_INTENT_ACTION", ScanwedgePlugin.SCANWEDGE_ACTION)
            putString("DPR_DATA_INTENT_CATEGORY", "SCAN")
            extraConfig["enableEanCheckDigitTransmission"]?.let { putBoolean("DEC_EAN13_CHECK_DIGIT_TRANSMIT", it) }
        }
        val honeywellDefaultTypes=BarcodeTypes.honeywellDefaultTypes().toMutableList()
        if(enabledBarcodes!=null){
            for(barcode in enabledBarcodes){
            barcode.honeywellAddToBundle(properties)
            honeywellDefaultTypes.remove(barcode.type)
            }
        }
        if(!keepDefaults){
            for(barcode in honeywellDefaultTypes){
                log?.d("BarcodePlugin", "removing barcode: $barcode")
                barcode.honeywellDisableBarcode(properties)
            }
        }else{
            log?.d(TAG, "keeping default barcodes")
        }
        val intent=Intent(ACTION_CLAIM_SCANNER).apply{
            setPackage("com.intermec.datacollectionservice")
            putExtra(EXTRA_SCANNER, "dcs.scanner.imager")
            putExtra(EXTRA_PROFILE, name)
            putExtra(EXTRA_PROPERTIES, properties)
        }
        log?.i(TAG, "createProfile: ${intent.toUri(0)}")
        scanW.sendBroadcast(intent)
        return true
    }
}