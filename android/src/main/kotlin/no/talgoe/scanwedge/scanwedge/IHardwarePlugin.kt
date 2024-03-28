package no.talgoe.scanwedge.scanwedge

import android.content.Intent

interface IHardwarePlugin {
    fun toggleScanning():Boolean
    fun createProfile(config: HashMap<String,Any>):Boolean
    fun createProfileTest(config: HashMap<String,Any>):Intent
}