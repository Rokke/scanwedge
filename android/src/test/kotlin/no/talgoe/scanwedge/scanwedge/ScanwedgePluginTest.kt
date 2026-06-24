package no.talgoe.scanwedge.scanwedge

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.test.Test
import org.mockito.Mockito

/*
* This demonstrates a simple unit test of the Kotlin portion of this plugin's implementation.
*
* Once you have built the plugin's example app, you can run these tests from the command
* line by running `./gradlew testDebugUnitTest` in the `example/android/` directory, or
* you can run them directly from IDEs that support JUnit such as Android Studio.
*/

internal class ScanwedgePluginTest {
  // An unknown method must route to notImplemented(). This path doesn't touch the
  // Android framework (Build/Settings/Context), so it runs under plain JUnit without
  // Robolectric. The previous assertions checked values that depend on Build/context,
  // which are null stubs in unit tests, so they never actually held.
  @Test
  fun onMethodCall_unknownMethod_callsNotImplemented() {
    val plugin = ScanwedgePlugin(TestLogger())

    val mockResult: MethodChannel.Result = Mockito.mock(MethodChannel.Result::class.java)
    plugin.onMethodCall(MethodCall("someUnknownMethod", null), mockResult)

    Mockito.verify(mockResult).notImplemented()
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