package no.talgoe.scanwedge.scanwedge

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Intent
import android.os.Bundle
import kotlin.test.Test
import org.mockito.Mockito
// import no.talgoe.scanwedge.scanwedge.ScanwedgePlugin // Add this import statement


/*
* This demonstrates a simple unit test of the Kotlin portion of this plugin's implementation.
*
* Once you have built the plugin's example app, you can run these tests from the command
* line by running `./gradlew testDebugUnitTest` in the `example/android/` directory, or
* you can run them directly from IDEs that support JUnit such as Android Studio.
*/
import no.talgoe.scanwedge.scanwedge.Logger

internal class ScanwedgePluginTest {
  private val ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER"
  private val EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER"
  private val EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE"
  private val EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES"
@Test
  fun onMethodCall_getPlatformVersion_returnsExpectedValue() {
    val testLogger=TestLogger()
    val plugin = ScanwedgePlugin(testLogger)

    val mockResult: MethodChannel.Result = Mockito.mock(MethodChannel.Result::class.java)
    plugin.onMethodCall(MethodCall("initializeDataWedge", "HONEYWELL"), mockResult)

    Mockito.verify(mockResult).success("HONEYWELL")
    val json = hashMapOf("name" to "DemoProfile")
    val properties = Bundle()
      properties.putBoolean("DPR_DATA_INTENT", true)
      // properties.putString("DPR_DATA_INTENT_ACTION", ACTION_BARCODE_DATA)
      properties.putString("DPR_DATA_INTENT_ACTION", ScanwedgePlugin.SCANWEDGE_ACTION)
      val res=Intent(ACTION_CLAIM_SCANNER)
        .setPackage("com.intermec.datacollectionservice")
        .putExtra(EXTRA_SCANNER, "dcs.scanner.imager")
        .putExtra(EXTRA_PROFILE, "DemoProfile")
        .putExtra(EXTRA_PROPERTIES, properties)
    plugin.onMethodCall(MethodCall("createProfile", json), mockResult)
    Mockito.verify(mockResult).success(res)
    // testLogger.i("ScanwedgePluginTest", "createProfile: ${hashMapToJsonString(argumentCaptor)}")
    
    Mockito.verify(mockResult).success("HONEYWELL")
  }
}

// ...
/*
{
  "name": "ScanwedgePluginTest",
  "test": "onMethodCall_getPlatformVersion_returnsExpectedValue",
  "result": "success"
}
*/