package no.talgoe.scanwedge.scanwedge

class ScanResult(private val barcode: String, private val barcodeType: BarcodeTypes, private val hardwareLabelType: String){
    fun toMap(): Any {
        return mapOf("barcode" to barcode, "barcodeType" to barcodeType.code, "hardwareLabelType" to hardwareLabelType)
    }
    override fun toString(): String {
        return "ScanResult(barcode='$barcode', barcodeType=$barcodeType, hardwareLabelType='$hardwareLabelType')"
    }
}