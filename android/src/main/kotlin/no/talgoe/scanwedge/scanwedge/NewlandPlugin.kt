package no.talgoe.scanwedge.scanwedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

// Hardware plugin for Newland devices that extends the IHardwarePlugin interface.
class NewlandPlugin(private val scanW: ScanwedgePlugin, private val log: Logger?) : IHardwarePlugin {
    companion object {
        private const val NL_SCAN_ACTION = "nlscan.action.SCANNER_RESULT"
        private const val ACTION_BAR_SCANCFG = "ACTION_BAR_SCANCFG"
        private const val ACTION_BARCODE_CFG = "ACTION_BARCODE_CFG"
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
                if (barcodeData.isNullOrEmpty() || codeId == null) {
                    // A failed trigger pull lands here too, which is routine rather than an error
                    if (intent.getStringExtra("SCAN_STATE") == "fail") log?.i(TAG, "Scan failed")
                    else log?.e(TAG, "barcode or codeId is null")
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

    override fun initialize(context: Context?): Boolean {
        log?.i(TAG, "$TAG initializing")
        if (context == null)
            return false

        val filter = IntentFilter(ScanwedgePlugin.SCANWEDGE_ACTION)
        filter.addAction(NL_SCAN_ACTION)
        return try {
            context.registerReceiverCompat(barcodeDataReceiver, filter, exported = false)
            // Every other output mode types into the focused field, so the receiver would never fire
            scanW.sendBroadcast(Intent(ACTION_BAR_SCANCFG).putExtra("EXTRA_SCAN_MODE", 3))
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
        log?.i(TAG, "createProfile($name, $enabledBarcodes, $hwConfig, $keepDefaults)")

        @Suppress("UNCHECKED_CAST")
        val newlandConfig = hwConfig?.get("newland") as? HashMap<String, Any>
        for((extra, value) in newlandScannerSettings(newlandConfig)){
            val intent = Intent(ACTION_BAR_SCANCFG)
            when(value) {
                is Long -> intent.putExtra(extra, value)
                is Int -> intent.putExtra(extra, value)
                else -> {
                    log?.e(TAG, "createProfile: no extra type for $extra=$value, skipping it")
                    continue
                }
            }
            scanW.sendBroadcast(intent)
        }

        val settings = newlandBarcodeSettings(enabledBarcodes, keepDefaults)
        log?.i(TAG, "createProfile: $settings")
        for(setting in settings){
            scanW.sendBroadcast(Intent(ACTION_BARCODE_CFG).apply{
                putExtra("CODE_ID", setting.codeId)
                putExtra("PROPERTY", setting.property)
                putExtra("VALUE", setting.value)
            })
        }
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

internal fun newlandBarcodeSettings(
    enabledBarcodes: List<BarcodePlugin>?,
    keepDefaults: Boolean,
): ArrayList<NewlandBarcodeSetting> {
    val asked = ArrayList<NewlandBarcodeSetting>()
    enabledBarcodes?.forEach { it.newlandAddToList(asked) }
    // Both DataBar types enable the same RSS toggle
    val settings = ArrayList(asked.distinct())
    if (keepDefaults) return settings

    // The factory defaults differ per scan engine, so this switches off everything it can name instead
    // Both GS1 DataBar types share one CODE_ID, so an unfiltered sweep would undo its own enable.
    val seenCodeIds = settings.mapTo(HashSet()) { it.codeId }
    for (type in BarcodeTypes.values()) {
        val codeId = type.newlandDecoderName() ?: continue
        if (!seenCodeIds.add(codeId)) continue

        type.newlandDisableBarcode(settings)
    }
    // Hardcoded, unlike the defaults, because a name another engine lacks costs one ignored broadcast
    NEWLAND_UNNAMED_CODE_IDS.forEach { settings.add(NewlandBarcodeSetting(it, "Enable", "0")) }

    return settings
}

// No BarcodeTypes for these, so their reads come back unknown. Names checked on a CM60L.
private val NEWLAND_UNNAMED_CODE_IDS = listOf(
    "AIM128", "CODE11", "CODE16K", "CODE49", "COMPOSITE", "CSC", "DOTCODE", "IND25", "ISBN", "ISSN",
    "ITF14", "ITF6", "MATRIX25", "MICROPDF", "MSIPLSY", "PLSY", "STD25",
)

private val NEWLAND_FLAGS = mapOf(
    "sendScanFailBroadcast" to "SEND_SCAN_FAIL_BROADCAST",
    "soundOnScan" to "EXTRA_SCAN_NOTY_SND",
    "vibrateOnScan" to "EXTRA_SCAN_NOTY_VIB",
    "ledOnScan" to "EXTRA_SCAN_NOTY_LED",
    "mainTriggerKey" to "TRIGGER_MODE_MAIN",
    "leftTriggerKey" to "TRIGGER_MODE_LEFT",
    "rightTriggerKey" to "TRIGGER_MODE_RIGHT",
    "pistolGripTrigger" to "TRIGGER_MODE_BLACK",
)

private val NEWLAND_DURATIONS = mapOf(
    "scanTimeout" to "SCAN_TIMEOUT",
    "rereadDelay" to "NON_REPEAT_TIMEOUT",
    "scanInterval" to "SCAN_INTERVAL",
)

// One extra per broadcast: the handbook caps ACTION_BAR_SCANCFG at three.
internal fun newlandScannerSettings(config: HashMap<String, Any>?): List<Pair<String, Any>> {
    val settings = mutableListOf<Pair<String, Any>>()
    if (config == null) return settings

    for ((key, extra) in NEWLAND_FLAGS) {
        (config[key] as? Boolean)?.let { settings.add(extra to if (it) 1 else 0) }
    }
    for ((key, extra) in NEWLAND_DURATIONS) {
        (config[key] as? Number)?.let { settings.add(extra to it.toLong()) }
    }
    newlandTriggerMode(config["triggerMode"] as? String)?.let { settings.add("EXTRA_TRIG_MODE" to it) }

    return settings
}

internal fun newlandTriggerMode(mode: String?) = when (mode) {
    "level" -> 0
    "continuous" -> 1
    "pulse" -> 2
    "delay" -> 4
    else -> null
}
