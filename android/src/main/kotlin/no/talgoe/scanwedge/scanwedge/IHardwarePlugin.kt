package no.talgoe.scanwedge.scanwedge

import android.content.Context
import android.content.Intent

interface IHardwarePlugin {
    /// This will return the API name together with a possible version number if needed later
    val apiVersion: String
    fun initialize(context: Context?):Boolean
    fun createProfile(name: String, enabledBarcodes: List<BarcodePlugin>?, hwConfig: HashMap<String,Any>?, keepDefaults: Boolean):Boolean
    fun disableScanner():Boolean
    fun dispose(context: Context?)
    fun enableScanner():Boolean
    fun toggleScanning():Boolean
}