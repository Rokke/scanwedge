package no.talgoe.scanwedge.scanwedge

class ScanResult(
    private val barcode: String,
    private val barcodeType: BarcodeTypes,
    private val hardwareLabelType: String,
    private val tokenMap: Map<String, String>?,
) {
    fun toMap(): Any {
        return mapOf(
            "barcode" to barcode,
            "barcodeType" to barcodeType.code,
            "hardwareLabelType" to hardwareLabelType,
            "tokens" to tokenMap
        )
    }

    override fun toString(): String {
        return "ScanResult(barcode='$barcode', barcodeType=$barcodeType, hardwareLabelType='$hardwareLabelType', tokenMap=$tokenMap)"
    }
}