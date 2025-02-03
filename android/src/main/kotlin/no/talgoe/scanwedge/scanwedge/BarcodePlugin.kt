package no.talgoe.scanwedge.scanwedge

import android.util.Log
import android.os.Bundle


/// BarcodePlugin class for the barcodetype and min/max length of the code.
class BarcodePlugin(val type: BarcodeTypes, private val minLength: Int?, private val maxLength: Int?) {
    companion object {
        fun createBarcodePlugin(config: HashMap<String, Any>): BarcodePlugin? {
            Log.i("BarcodePlugin", "createBarcodePlugin: ${config["type"]}")
            // get the barcode type from the config, the type is a string with the value inside the code property of the BarcodeTypes enum
            val type = BarcodeTypes.entries.find { it.code == config["type"] }
            if(type == null) {
                Log.e("BarcodePlugin", "createBarcodePlugin: Invalid barcode type")
                return null
            }
            Log.d("BarcodePlugin", "createBarcodePlugin: $type")
            val minLength = config["minLength"] as Int?
            val maxLength = config["maxLength"] as Int?
            return BarcodePlugin(type, minLength, maxLength)
        }
    }

    fun zebraAddToBundle(bundle: Bundle) {
        val decoderName = type.zebraDecoderName()
        if(decoderName != null) {
            Log.d("BarcodePlugin", "zebraAddToBundle enable: $type, $decoderName")
            bundle.putString(decoderName, "true")
            if(minLength != null) {
                bundle.putInt("${decoderName}_length1", minLength)
            }
            if(maxLength != null) {
                bundle.putInt("${decoderName}_length2", maxLength)
            }
        }else{
            Log.w("BarcodeTypes", "zebraAddToBundle: Invalid barcode type: $this")
        }
    }

    fun honeywellAddToBundle(bundle: Bundle) {
        val decoderName = type.honeywellDecoderName()
        if(decoderName != null) {
            Log.d("BarcodePlugin", "honeywellAddToBundle enable: $type, $decoderName")
            bundle.putBoolean("${decoderName}_ENABLED", true)
            if(minLength != null) {
                bundle.putInt("${decoderName}_MIN_LENGTH", minLength)
            }
            if(maxLength != null) {
                bundle.putInt("${decoderName}_MAX_LENGTH", maxLength)
            }
        }else{
            Log.e("BarcodePlugin", "honeywellAddToBundle: Invalid barcode type: $type")
        }
    }

    fun datalogicAddToList(lst: ArrayList<String>) {
        val decoderName = type.datalogicDecoderName()
        if(decoderName != null) {
            Log.d("BarcodePlugin", "datalogicAddToList enable: $type, $decoderName")
            lst.add("${decoderName}_ENABLE=true")
            if(minLength != null) {
                lst.add("${decoderName}_LENGTH1=$minLength")
            }
            if(maxLength != null) {
                lst.add("${decoderName}_LENGTH2=$maxLength")
            }
        }else{
            Log.e("BarcodePlugin", "datalogicAddToList: Invalid barcode type: $type")
        }
    }
}