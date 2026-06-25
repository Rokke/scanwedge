package no.talgoe.scanwedge.scanwedge

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

// Hardware plugin for Newland devices that extends the IHardwarePlugin interface.
class NewlandPlugin(private val scanW: ScanwedgePlugin, private val log: Logger?) : IHardwarePlugin {
    companion object {
        private const val NL_SCAN_ACTION = "nlscan.action.SCANNER_RESULT"
        private const val TAG="NewlandPlugin"
    }

    private val barcodeDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                log?.i(TAG, "onReceive: ${intent.toUri(0)}, ${intent.action}")
                if (intent.action != NL_SCAN_ACTION)
                    return

                val barcodeData = intent.getStringExtra("SCAN_BARCODE1")
                val codeId = intent.getStringExtra("SCAN_BARCODE_TYPE_NAME")
                if (barcodeData == null || codeId == null) {
                    log?.e(TAG, "barcode or codeId is null")
                    return
                }
                val barcodeType = BarcodeTypes.fromNewlandCode(codeId)

                log?.i(TAG, "Barcode Data: $barcodeData, Barcode Type: $barcodeType")
                scanW.sendScanResult(ScanResult(barcodeData, barcodeType, codeId))
            } catch (e: Exception) {
                log?.e(TAG, "Error in barcodeDataReceiver: ${e.message}")
            }
        }
    }

    override val apiVersion: String get() = "NEWLAND"


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun initialize(context: Context?): Boolean {
        log?.i(TAG, "$TAG initializing")
        if (context == null)
            return false

        val filter = IntentFilter(ScanwedgePlugin.SCANWEDGE_ACTION)
        filter.addAction(NL_SCAN_ACTION)
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
        log?.w(TAG, "Cannot programmatically control scanner")
        return false
    }

    override fun disableScanner(): Boolean {
        log?.w(TAG, "Cannot programmatically control scanner")
        return false
    }

    override fun toggleScanning(): Boolean {
        log?.w(TAG, "Cannot programmatically control scanner")
        return false
    }

    override fun dispose(context: Context?) {
        context?.unregisterReceiverSafely(barcodeDataReceiver)
    }
}
