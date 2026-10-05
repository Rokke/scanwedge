package no.talgoe.scanwedge.scanwedge

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class NewlandScannerSettingsTest {
  @Test
  fun everySettingGoesOutUnderItsOwnExtra() {
    // One key at a time, so two swapped names can't hide behind each other
    val flags = mapOf(
      "sendScanFailBroadcast" to "SEND_SCAN_FAIL_BROADCAST",
      "soundOnScan" to "EXTRA_SCAN_NOTY_SND",
      "vibrateOnScan" to "EXTRA_SCAN_NOTY_VIB",
      "ledOnScan" to "EXTRA_SCAN_NOTY_LED",
      "mainTriggerKey" to "TRIGGER_MODE_MAIN",
      "leftTriggerKey" to "TRIGGER_MODE_LEFT",
      "rightTriggerKey" to "TRIGGER_MODE_RIGHT",
      "pistolGripTrigger" to "TRIGGER_MODE_BLACK",
    )
    for ((key, extra) in flags) {
      assertEquals(listOf<Pair<String, Any>>(extra to 1), newlandScannerSettings(hashMapOf(key to true)), key)
    }

    val durations = mapOf("scanTimeout" to "SCAN_TIMEOUT", "rereadDelay" to "NON_REPEAT_TIMEOUT", "scanInterval" to "SCAN_INTERVAL")
    for ((key, extra) in durations) {
      assertEquals(listOf<Pair<String, Any>>(extra to 500L), newlandScannerSettings(hashMapOf(key to 500)), key)
    }

    for ((mode, value) in mapOf("level" to 0, "continuous" to 1, "pulse" to 2, "delay" to 4)) {
      assertEquals(listOf<Pair<String, Any>>("EXTRA_TRIG_MODE" to value), newlandScannerSettings(hashMapOf("triggerMode" to mode)), mode)
    }
  }

  @Test
  fun outputModeIsLeftToInitialize() {
    assertEquals(emptyList(), newlandScannerSettings(null))
  }

  @Test
  fun flagsBecomeOneAndZero() {
    val settings = newlandScannerSettings(hashMapOf("soundOnScan" to false, "ledOnScan" to true))

    assertTrue(settings.contains("EXTRA_SCAN_NOTY_SND" to 0))
    assertTrue(settings.contains("EXTRA_SCAN_NOTY_LED" to 1))
  }

  @Test
  fun durationsGoOutAsLongs() {
    val settings = newlandScannerSettings(hashMapOf("scanTimeout" to 7000))

    assertTrue(settings.contains("SCAN_TIMEOUT" to 7000L), "the device wants a long, got $settings")
  }

  @Test
  fun anUnknownTriggerModeIsLeftOut() {
    val settings = newlandScannerSettings(hashMapOf("triggerMode" to "nonsense"))

    assertTrue(settings.none { it.first == "EXTRA_TRIG_MODE" })
  }
}
