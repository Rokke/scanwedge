package no.talgoe.scanwedge.scanwedge

import android.util.Log
import android.os.Bundle

enum class BarcodeTypes(val code: String) {
    AZTEC("aztec"),
    CODABAR("codabar"),
    CODE128("code128"),
    CODE39("code39"),
    CODE93("code93"),
    DATAMATRIX("datamatrix"),
    EAN13("ean13"),
    EAN8("ean8"),
    EAN128("ean128"),
    GS1_DATABAR("gs1DataBar"),
    GS1_DATABAR_EXPANDED("gs1DataBarExpanded"),
    I2OF5("i2of5"),
    MAILMARK("mailmark"),
    MAXICODE("maxicode"),
    MICROQR("microqr"),
    PDF417("pdf417"),
    QRCODE("qrCode"),
    UPCA("upca"),
    UPCE0("upce0"),
    UNKNOWN("unknown");
    companion object {
        fun zebraDefaultTypes(): List<BarcodeTypes> {
            return listOf(AZTEC, CODABAR, CODE128, CODE39, DATAMATRIX, EAN8, EAN13, GS1_DATABAR, GS1_DATABAR_EXPANDED, MAILMARK, MAXICODE, PDF417, QRCODE, UPCA, UPCE0)
        }
        fun honeywellDefaultTypes(): List<BarcodeTypes> {
            return listOf(AZTEC, CODABAR, CODE128, CODE39, DATAMATRIX, EAN8, EAN13, GS1_DATABAR, MAXICODE, PDF417, QRCODE, UPCA, UPCE0, EAN128, I2OF5, CODE93)
        }
        fun datalogicDefaultTypes(): List<BarcodeTypes> {
            return listOf(AZTEC, CODABAR, CODE128, CODE39, CODE93, DATAMATRIX, EAN8, EAN128, EAN13, GS1_DATABAR, MAXICODE, PDF417, QRCODE, UPCA, UPCE0, I2OF5)
        }
        fun fromZebraCode(codeId: String?):BarcodeTypes{
            return when(codeId){
                "LABEL-TYPE-AZTEC" -> AZTEC
                "LABEL-TYPE-CODABAR" -> CODABAR
                "LABEL-TYPE-CODE128" -> CODE128
                "LABEL-TYPE-CODE39" -> CODE39
                "LABEL-TYPE-CODE93" -> CODE93
                "LABEL-TYPE-DATAMATRIX" -> DATAMATRIX
                "LABEL-TYPE-EAN8" -> EAN8
                "LABEL-TYPE-EAN128" -> EAN128
                "LABEL-TYPE-EAN13" -> EAN13
                "LABEL-TYPE-GS1_DATABAR" -> GS1_DATABAR
                "LABEL-TYPE-GS1_DATABAR_EXP" -> GS1_DATABAR_EXPANDED
                "LABEL-TYPE-I2OF5" -> I2OF5
                "LABEL-TYPE-MAILMARK" -> MAILMARK
                "LABEL-TYPE-MAXICODE" -> MAXICODE
                "LABEL-TYPE-PDF417" -> PDF417
                "LABEL-TYPE-QRCODE" -> QRCODE
                "LABEL-TYPE-UPCA" -> UPCA
                "LABEL-TYPE-UPCE0" -> UPCE0
                else -> UNKNOWN
            }
        }
        fun fromDatalogicCode(codeId: String?):BarcodeTypes{
            return when(codeId){
                    "Aztec" -> AZTEC
                "Codabar" -> CODABAR
                    "Code 128" -> CODE128
                    "Code 39" -> CODE39
                    "Code 93" -> CODE93
                    "Data Matrix" -> DATAMATRIX
                    "EAN-8" -> EAN8
                    "GS1-128" -> EAN128
                    "EAN-13" -> EAN13
                    "GS1 DataBar-14" -> GS1_DATABAR
                    "GS1 DataBar Limited" -> GS1_DATABAR
                    "GS1 DataBar Expanded" -> GS1_DATABAR_EXPANDED
                    "Interleaved 2 of 5" -> I2OF5
                    "PDF417" -> PDF417
                    "QR Code" -> QRCODE
                    "UPC-A" -> UPCA
                    "UPC-E" -> UPCE0
                else -> UNKNOWN
            }
        }
/*
                "D2OF5" -> BarcodeTypes.D2OF5
                "IATA2OF5" -> BarcodeTypes.IATA2OF5
                "UPCE1" -> BarcodeTypes.UPCE1
                "MSI" -> BarcodeTypes.MSI
                "TRIOPTIC39" -> BarcodeTypes.TRIOPTIC39
                "BOOKLAND" -> BarcodeTypes.BOOKLAND
                "COUPON" -> BarcodeTypes.COUPON
                "DATABAR-COUPON" -> BarcodeTypes.DATABAR-COUPON
                "ISBT128" -> BarcodeTypes.ISBT128
                "CODE32" -> BarcodeTypes.CODE32
                "MICROPDF" -> BarcodeTypes.MICROPDF
                "TLC39" -> BarcodeTypes.TLC39
                "CODE11" -> BarcodeTypes.CODE11
                "GS1-DATABAR-LIM" -> BarcodeTypes.GS1-DATABAR-LIM
                "USPOSTNET" -> BarcodeTypes.USPOSTNET
                "USPLANET" -> BarcodeTypes.USPLANET
                "UKPOSTAL" -> BarcodeTypes.UKPOSTAL
                "JAPPOSTAL" -> BarcodeTypes.JAPPOSTAL
                "AUSPOSTAL" -> BarcodeTypes.AUSPOSTAL
                "DUTCHPOSTAL" -> BarcodeTypes.DUTCHPOSTAL
                "FINNISHPOSTAL-4S" -> BarcodeTypes.FINNISHPOSTAL-4S
                "CANPOSTAL" -> BarcodeTypes.CANPOSTAL
                "CHINESE-2OF5" -> BarcodeTypes.CHINESE-2OF5
                "MICROQR" -> BarcodeTypes.MICROQR
                "US4STATE" -> BarcodeTypes.US4STATE
                "US4STATE-FICS" -> BarcodeTypes.US4STATE-FICS
                "COMPOSITE-AB" -> BarcodeTypes.COMPOSITE-AB
                "COMPOSITE-C" -> BarcodeTypes.COMPOSITE-C
                "WEBCODE" -> BarcodeTypes.WEBCODE
                "SIGNATURE" -> BarcodeTypes.SIGNATURE
                "KOREAN-3OF5" -> BarcodeTypes.KOREAN-3OF5
                "MATRIX-2OF5" -> BarcodeTypes.MATRIX-2OF5
                "OCR" -> BarcodeTypes.OCR
                "HANXIN" -> BarcodeTypes.HANXIN
                "MULTICODE-DATA-FORMAT" -> BarcodeTypes.MULTICODE-DATA-FORMAT
                "GS1-DATAMATRIX" -> BarcodeTypes.GS1-DATAMATRIX
                "GS1-QRCODE" -> BarcodeTypes.GS1-QRCODE
                "DOTCODE" -> BarcodeTypes.DOTCODE
                "GRIDMATRIX" -> BarcodeTypes.GRIDMATRIX
                "UNDEFINED" -> BarcodeTypes.UNDEFINED
*/
        fun fromHoneywellCode(codeId: String?):BarcodeTypes{
            return when(codeId){
                "z" -> AZTEC
                "a" -> CODABAR
                "j" -> CODE128
                "b" -> CODE39
                "i" -> CODE93
                "w" -> DATAMATRIX
                "D" -> EAN8
                "`" -> EAN13          // "EAN13_ISBN"
                "d" -> EAN13
                "I" -> EAN128
                "y" -> GS1_DATABAR
                "}" -> GS1_DATABAR_EXPANDED
                "e" -> I2OF5
                "x" -> MAXICODE
                "r" -> PDF417
                "s" -> QRCODE
                "c" -> UPCA
                "E" -> UPCE0
                else -> UNKNOWN
/*               "." -> "DOTCODE"
              "1" -> "CODE1"
              ";" -> "MERGED_COUPON"
              "<" -> "ITALIAN PHARMACODE"
              ">" -> "LABELCODE_V"
              "=" -> "TRIOPTIC"
              "?" -> "KOREA_POST"
              "," -> "INFOMAIL"
              "[" -> "SWEEDISH_POST"
              "|" -> "RM_MAILMARK"
              "]" -> "BRAZIL_POST"
              "A" -> "AUS_POST"
              "B" -> "BRITISH_POST"
              "C" -> "CANADIAN_POST"
              "G" -> "BC412"
              "H" -> "HAN_XIN_CODE"
              "J" -> "JAPAN_POST"
              "K" -> "KIX_CODE"
              "L" -> "PLANET_CODE"
              "M" -> "USPS_4_STATE"
              "N" -> "UPU_4_STATE"
              "O" -> "OCR"
              "P" -> "POSTNET"
              "Q" -> "HK25"
              "R" -> "MICROPDF"
              "S" -> "SECURE_CODE"
              "T" -> "TLC39"
              "U" -> "ULTRACODE"
              "V" -> "CODABLOCK_A"
              "W" -> "POSICODE"
              "X" -> "GRID_MATRIX"
              "Y" -> "NEC25"
              "Z" -> "MESA"
              "f" -> "S25"
              "g" -> "MSI"
              "h" -> "CODE11"
              "l" -> "CODE49"
              "m" -> "M25"
              "n" -> "PLESSEY"
              "o" -> "CODE16K"
              "p" -> "CHANNELCODE"
              "q" -> "CODABLOCK_F"
              "t" -> "TELEPEN"
              "u" -> "CODEZ"
              "v" -> "VERICODE"
              "{" -> "GS1_DATABAR_LIM"*/
            }
          }

        fun fromNewlandCode(codeId: String?):BarcodeTypes{
            return when(codeId){
                // Linear
                "CODE128" -> CODE128
                "CODE39" -> CODE39
                "UCCEAN128" -> EAN128
                // EAN / UPC
                "EAN8" -> EAN8
                "EAN13" -> EAN13
                "UPCA" -> UPCA
                "UPCE" -> UPCE0
                // 2D
                "QRCode" -> QRCODE
                "DATAMATRIX" -> DATAMATRIX
                "PDF417" -> PDF417
                "MICROQR" -> MICROQR
                else -> UNKNOWN
            }
        }
    }

    // A function that returns the zebra implementation name of the barcode type
    // See https://techdocs.zebra.com/datawedge/latest/guide/decoders/ for more information
    fun zebraDecoderName(): String? {
        return when(this) {
            AZTEC -> "decoder_aztec"
            CODABAR -> "decoder_codabar"
            CODE128 -> "decoder_code128"
            CODE39 -> "decoder_code39"
            CODE93 -> "decoder_code93"
            DATAMATRIX -> "decoder_datamatrix"
            EAN8 -> "decoder_ean8"
            EAN13 -> "decoder_ean13"
            GS1_DATABAR -> "decoder_gs1_databar"
            GS1_DATABAR_EXPANDED -> "decoder_gs1_databar_exp"
            I2OF5 -> "decoder_i2of5"
            MAILMARK -> "decoder_mailmark"
            MAXICODE -> "decoder_maxicode"
            PDF417 -> "decoder_pdf417"
            QRCODE -> "decoder_qrcode"
            UPCA -> "decoder_upca"
            UPCE0 -> "decoder_upce0"
            else -> null
        }
    }
    fun honeywellDecoderName(): String?{
        return when(this) {
            AZTEC -> "DEC_AZTEC"
            CODABAR -> "DEC_CODABAR"
            CODE128 -> "DEC_CODE128"
            CODE39 -> "DEC_CODE39"
            DATAMATRIX -> "DEC_DATAMATRIX"
            EAN8 -> "DEC_EAN8"
            EAN13 -> "DEC_EAN13"
            EAN128 -> "DEC_EAN128"
            GS1_DATABAR -> "DEC_EAN128"
            I2OF5 -> "DEC_I25"
            MAXICODE -> "DEC_MAXICODE"
            PDF417 -> "DEC_PDF417"
            QRCODE -> "DEC_QR"
            UPCA -> "DEC_UPCA"
            UPCE0 -> "DEC_UPCE0"
            else -> null
        }
    }
    fun datalogicDecoderName(): String?{
        return when(this) {
            AZTEC -> "AZTEC"
            CODABAR -> "CODABAR"
            CODE128 -> "CODE128"
            CODE39 -> "CODE39"
            CODE93 -> "CODE93"
            DATAMATRIX -> "DATAMATRIX"
            EAN8 -> "EAN8"
            EAN13 -> "EAN13"
            EAN128 -> "CODE128_GS1"
            GS1_DATABAR -> "GS1_14"
            I2OF5 -> "I25"
            MAXICODE -> "MAXICODE"
            PDF417 -> "PDF417"
            QRCODE -> "QRCODE"
            UPCA -> "UPCA"
            UPCE0 -> "UPCE"
            else -> null
        }
    }
    fun zebraDisableBarcode(bundle: Bundle) {
        val decoderName = zebraDecoderName()
        if(decoderName != null) {
            Log.d("BarcodeTypes", "zebraDisableBarcode: ${decoderName}=false")
            bundle.putString(decoderName, "false")
        }else{
            Log.w("BarcodeTypes", "zebraDisableBarcode: Invalid barcode type: $this")
        }
    }
    fun honeywellDisableBarcode(bundle: Bundle) {
        Log.d("BarcodeTypes", "honeywellDisableBarcode: ${honeywellDecoderName()}=false")
        val decoderName = honeywellDecoderName()
        if(decoderName != null) {
            bundle.putBoolean("${decoderName}_ENABLED", false)
        }else{
            Log.w("BarcodeTypes", "honeywellDisableBarcode: Invalid barcode type: $this")
        }
    }
    fun datalogicDisableBarcode(lst: ArrayList<String>) {
        Log.d("BarcodeTypes", "datalogicDisableBarcode: ${datalogicDecoderName()}=false")
        val decoderName = datalogicDecoderName()
        if(decoderName != null) {
            lst.add("${decoderName}_ENABLE=false")
        }else{
            Log.w("BarcodeTypes", "datalogicDisableBarcode: Invalid barcode type: $this")
        }
    }
}