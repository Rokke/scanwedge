package no.talgoe.scanwedge.scanwedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class NewlandPlugin(private val scanW: ScanwedgePlugin, private val log: Logger?) : IHardwarePlugin
    {
        override val apiVersion: String
            get() = TODO("Not yet implemented")

        override fun initialize(context: Context?): Boolean {
            TODO("Not yet implemented")
        }

        override fun createProfile(
            name: String,
            enabledBarcodes: List<BarcodePlugin>?,
            hwConfig: HashMap<String, Any>?,
            keepDefaults: Boolean
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun enableScanner(): Boolean {
            TODO("Not yet implemented")
        }

        override fun disableScanner(): Boolean {
            TODO("Not yet implemented")
        }

        override fun toggleScanning(): Boolean {
            TODO("Not yet implemented")
        }

        override fun dispose(context: Context?) {
            TODO("Not yet implemented")
        }
    }
