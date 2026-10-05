package no.talgoe.scanwedge.scanwedge

import android.os.Bundle


/// BarcodePlugin class for the barcodetype and min/max length of the code.
class BarcodePlugin(val type: BarcodeTypes, private val minLength: Int?, private val maxLength: Int?, private val log: Logger?) {
    companion object {
        fun createBarcodePlugin(config: HashMap<String, Any>, log: Logger?): BarcodePlugin? {
            log?.i("BarcodePlugin", "createBarcodePlugin: ${config["type"]}")
            // get the barcode type from the config, the type is a string with the value inside the code property of the BarcodeTypes enum
            val type = BarcodeTypes.values().find { it.code == config["type"] }
            // val type = BarcodeTypes.entries.find { it.code == config["type"] }   //This is experimental so waiting with this: Its usage must be marked with '@kotlin.ExperimentalStdlibApi' or '@OptIn(kotlin.ExperimentalStdlibApi::class)'
            if(type == null) {
                log?.e("BarcodePlugin", "createBarcodePlugin: Invalid barcode type")
                return null
            }
            log?.d("BarcodePlugin", "createBarcodePlugin: $type")
            val minLength = config["minLength"] as Int?
            val maxLength = config["maxLength"] as Int?
            return BarcodePlugin(type, minLength, maxLength, log)
        }
    }

    fun zebraAddToBundle(bundle: Bundle) {
        val decoderName = type.zebraDecoderName()
        if(decoderName != null) {
            log?.d("BarcodePlugin", "zebraAddToBundle enable: $type, $decoderName")
            bundle.putString(decoderName, "true")
            if(minLength != null) {
                bundle.putInt("${decoderName}_length1", minLength)
            }
            if(maxLength != null) {
                bundle.putInt("${decoderName}_length2", maxLength)
            }
        }else{
            log?.w("BarcodeTypes", "zebraAddToBundle: Invalid barcode type: $this")
        }
    }

    fun honeywellAddToBundle(bundle: Bundle) {
        val decoderName = type.honeywellDecoderName()
        if(decoderName != null) {
            log?.d("BarcodePlugin", "honeywellAddToBundle enable: $type, $decoderName")
            bundle.putBoolean("${decoderName}_ENABLED", true)
            if(minLength != null) {
                bundle.putInt("${decoderName}_MIN_LENGTH", minLength)
            }
            if(maxLength != null) {
                bundle.putInt("${decoderName}_MAX_LENGTH", maxLength)
            }
        }else{
            log?.e("BarcodePlugin", "honeywellAddToBundle: Invalid barcode type: $type")
        }
    }

    /// Same length settings on another symbology. Used when one decoder covers another - DataLogic
    /// decodes GS1-128 with the Code 128 decoder.
    fun withType(type: BarcodeTypes) = BarcodePlugin(type, minLength, maxLength, log)

    override fun toString() = "BarcodePlugin($type${if(minLength!=null||maxLength!=null) ",$minLength-$maxLength" else ""})"

    fun newlandAddToList(lst: ArrayList<NewlandBarcodeSetting>) {
        val decoderName = type.newlandDecoderName()
        if(decoderName != null) {
            log?.d("BarcodePlugin", "newlandAddToList enable: $type, $decoderName")
            lst.add(NewlandBarcodeSetting(decoderName, "Enable", "1"))
            if(!type.newlandHasLengthControl()){
                if(minLength != null || maxLength != null) {
                    log?.i("BarcodePlugin", "newlandAddToList: $type has no length control on Newland, ignoring $minLength-$maxLength")
                }
                return
            }
            // The scanner drops values outside its range without a word, 0 included
            minLength?.let { lst.add(NewlandBarcodeSetting(decoderName, "Minlen", "$it")) }
            maxLength?.let { lst.add(NewlandBarcodeSetting(decoderName, "Maxlen", "$it")) }
        }else{
            log?.e("BarcodePlugin", "newlandAddToList: Invalid barcode type: $type")
        }
    }

    fun datalogicAddToList(lst: ArrayList<String>) {
        val decoderName = type.datalogicDecoderName()
        if(decoderName != null) {
            log?.d("BarcodePlugin", "datalogicAddToList enable: $type, $decoderName")
            lst.add("${decoderName}_ENABLE=true")
            if(!type.datalogicHasLengthControl()){
                // Only some DataLogic symbologies have length properties. GS1-128 for instance is an
                // option of the Code 128 decoder (CODE128_GS1_ENABLE) and has no CODE128_GS1_LENGTH*
                // properties of its own - it uses Code 128's. Sending a property DataLogic does not
                // know makes the *whole* configuration COMMIT fail, so the scanner silently keeps the
                // previous profile and the caller never learns its barcodes were never enabled.
                if(minLength != null || maxLength != null) {
                    log?.i("BarcodePlugin", "datalogicAddToList: $type has no length control on DataLogic, ignoring $minLength-$maxLength")
                }
                return
            }
            if(minLength==0 && maxLength==0){
                lst.add("${decoderName}_LENGTH_CONTROL=0")
            }else if(minLength!=null || maxLength!=null){
                lst.add("${decoderName}_LENGTH_CONTROL=${if (maxLength == null) 1 else 3}")     // LENGTH_CONTROL DataLogic: 0=None, 1=Only accept length of minLength, 2=only minLength and the maxLength, 3=Range
                if(minLength != null) {
                    lst.add("${decoderName}_LENGTH1=$minLength")
                }
                if(maxLength != null) {
                    lst.add("${decoderName}_LENGTH2=$maxLength")
                }
            }
        }else{
            log?.e("BarcodePlugin", "datalogicAddToList: Invalid barcode type: $type")
        }
    }
}

// One ACTION_BARCODE_CFG broadcast, which carries exactly one property of one symbology.
data class NewlandBarcodeSetting(val codeId: String, val property: String, val value: String)
