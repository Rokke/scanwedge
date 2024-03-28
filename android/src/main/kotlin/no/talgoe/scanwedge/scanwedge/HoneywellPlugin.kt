package no.talgoe.scanwedge.scanwedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.os.Bundle

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

import no.talgoe.scanwedge.scanwedge.Logger

// Hardware plugin for Zebra devices that extends the IHardwarePlugin interface.
class HoneywellPlugin(private val context: Context?, private val channel: MethodChannel?, private val log: Logger?) : IHardwarePlugin {
    // private val ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA"
    // private val SCANWEDGE_ACTION="no.talgoe.scanwedge.SCAN"
    private val DATAWEDGE_SEND_ACTION = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER"
    private val ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER"
    private val ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER"
    private val EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER"
    private val EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE"
    private val EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES"
    private val TAG="HoneywellPlugin"
    private val barcodeDataReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        log?.i(TAG, "onReceive: ${intent.toUri(0)}, ${intent.action}")
        if (ScanwedgePlugin.SCANWEDGE_ACTION == intent.action) {
        // if (ACTION_BARCODE_DATA == intent.action) {
          val version = intent.getIntExtra("version", 0)
          if (version >= 1) {
            val aimId = intent.getStringExtra("aimId")
            val charset = intent.getStringExtra("charset")
            val codeId = intent.getStringExtra("codeId")
            val data = intent.getStringExtra("data")
            // val dataBytes = intent.getByteArrayExtra("dataBytes")
            val timestamp = intent.getStringExtra("timestamp")
            log?.i(TAG, "Barcode Data: $data, $charset, $codeId, $aimId, $timestamp")
            channel!!.invokeMethod("scan", mapOf("barcode" to intent.getStringExtra("data"),"labelType" to symbologyName(codeId!!)))
          }
        }
      }
    }
    // Constructor
    init {
      log?.i(TAG, "HoneywellPlugin")
      context?.registerReceiver(barcodeDataReceiver, IntentFilter(ScanwedgePlugin.SCANWEDGE_ACTION))
      // context.registerReceiver(barcodeDataReceiver, IntentFilter(ACTION_BARCODE_DATA))
    }
    // deconstructor
    fun dispose() {
      context?.sendBroadcast(Intent(ACTION_RELEASE_SCANNER).setPackage("com.intermec.datacollectionservice"))
      context?.unregisterReceiver(barcodeDataReceiver)
    }
    override fun toggleScanning():Boolean {
      log?.i(TAG, "toggleScanning")
      return true
    }
    override fun createProfileTest(config: HashMap<String,Any>):Intent {
      log?.i(TAG, "createProfileTest($config)")
      val properties = Bundle()
      properties.putBoolean("DPR_DATA_INTENT", true)
      // properties.putString("DPR_DATA_INTENT_ACTION", ACTION_BARCODE_DATA)
      properties.putString("DPR_DATA_INTENT_ACTION", ScanwedgePlugin.SCANWEDGE_ACTION)
      return Intent(ACTION_CLAIM_SCANNER)
        .setPackage("com.intermec.datacollectionservice")
        .putExtra(EXTRA_SCANNER, "dcs.scanner.imager")
        .putExtra(EXTRA_PROFILE, config["name"] as String)
        .putExtra(EXTRA_PROPERTIES, properties)
    }
    override fun createProfile(config: HashMap<String,Any>):Boolean {
      context?.sendBroadcast(createProfileTest(config))
      // val dwIntent = Intent()
      // dwIntent.action = DATAWEDGE_SEND_ACTION
      // dwIntent.putExtra(command, parameter)
      // context.sendBroadcast(dwIntent)
      return true
    }
    // Replace the symbology value to the approriate name
    fun symbologyName(codeId: String):String{
      return when(codeId){
        "." -> "DOTCODE"
        "1" -> "CODE1"
        ";" -> "MERGED_COUPON"
        "<" -> "ITALIAN PHARMACODE"
        ">" -> "LABELCODE_V"
        "=" -> "TRIOPTIC"
        "?" -> "KOREA_POST"
        "," -> "INFOMAIL"
        "`" -> "EAN13_ISBN"
        "[" -> "SWEEDISH_POST"
        "|" -> "RM_MAILMARK"
        "]" -> "BRAZIL_POST"
        "A" -> "AUS_POST"
        "B" -> "BRITISH_POST"
        "C" -> "CANADIAN_POST"
        "D" -> "EAN8"
        "E" -> "UPCE"
        "G" -> "BC412"
        "H" -> "HAN_XIN_CODE"
        "I" -> "GS1_128"
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
        "a" -> "CODABAR"
        "b" -> "CODE39"
        "c" -> "UPCA"
        "d" -> "EAN13"
        "e" -> "I25"
        "f" -> "S25"
        "g" -> "MSI"
        "h" -> "CODE11"
        "i" -> "CODE93"
        "j" -> "CODE128"
        "l" -> "CODE49"
        "m" -> "M25"
        "n" -> "PLESSEY"
        "o" -> "CODE16K"
        "p" -> "CHANNELCODE"
        "q" -> "CODABLOCK_F"
        "r" -> "PDF417"
        "s" -> "QRCODE"
        "t" -> "TELEPEN"
        "u" -> "CODEZ"
        "v" -> "VERICODE"
        "w" -> "DATAMATRIX"
        "x" -> "MAXICODE"
        "y" -> "GS1_DATABAR"
        "{" -> "GS1_DATABAR_LIM"
        "}" -> "GS1_DATABAR_EXP"
        "z" -> "AZTEC_CODE"
        else -> "UNKNOWN"
      }
    }
  }