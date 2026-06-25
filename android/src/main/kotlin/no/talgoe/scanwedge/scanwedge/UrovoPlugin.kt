package no.talgoe.scanwedge.scanwedge

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class UrovoPlugin(private val scanW: ScanwedgePlugin, private val log: Logger?) : IHardwarePlugin {
    companion object {
        private const val UR_SCAN_ACTION = "android.intent.ACTION_DECODE_DATA"
        private const val TAG = "UrovoPlugin"
    }

    private val barcodeDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                log?.i(TAG, "onRecive: ${intent.toUri(0)}, ${intent.action}")
                if (intent.action != UR_SCAN_ACTION)
                    return

                val barcodeData = intent.getStringExtra("com.ubx.datawedge.data_string")
                val codeId = intent.getStringExtra("com.ubx.datawedge.symbology_name")
                if (barcodeData == null || codeId == null) {
                    log?.e(TAG, "barcode or codeId is null")
                    return
                }
                val barcodeType = BarcodeTypes.fromUrovoCode(codeId)

                log?.i(TAG, "Barcode Data: $barcodeData, Barcode Type: $barcodeType")
                scanW.sendScanResult(ScanResult(barcodeData, barcodeType, codeId))
            } catch (e: Exception) {
                log?.e(TAG, "Error in barcodeDataReceiver: ${e.message}")
            }
        }
    }

    override val apiVersion: String get() = "UROVO"

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun initialize(context: Context?): Boolean {
        log?.i(TAG, "$TAG initializing")
        if (context == null)
            return false

        val filter = IntentFilter(ScanwedgePlugin.SCANWEDGE_ACTION)
        filter.addAction(UR_SCAN_ACTION)
        return try {
            context.registerReceiverCompat(barcodeDataReceiver, filter, exported = false)
            true
        } catch (e: Exception) {
            log?.e(TAG, "$TAG initialize, Exception: ${e.message}")
            false
        }
    }

    override fun createProfile(
        name: String,
        enabledBarcodes: List<BarcodePlugin>?,
        hwConfig: HashMap<String, Any>?,
        keepDefaults: Boolean
    ): Boolean {
        return true
    }

    override fun enableScanner(): Boolean {
        log?.w(TAG, "Cannot programatically control scanner")
        return false
    }

    override fun disableScanner(): Boolean {
        log?.w(TAG, "Cannot programatically control scanner")
        return false
    }

    override fun toggleScanning(): Boolean {
        log?.w(TAG, "Cannot programatically control scanner")
        return false
    }

    override fun dispose(context: Context?) {
        context?.unregisterReceiverSafely(barcodeDataReceiver)
    }
}