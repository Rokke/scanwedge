package no.talgoe.scanwedge.scanwedge

import kotlin.test.Test
import kotlin.test.assertEquals

/*
* Newland takes one ACTION_BARCODE_CFG broadcast per symbology per property, so a profile is a list
* of triples rather than one bundle. These pin what goes out.
*
* Run with `./gradlew testDebugUnitTest` in `example/android/`.
*/
internal class NewlandProfileTest {
  private val log = TestLogger()
  private fun plugin(type: BarcodeTypes) = BarcodePlugin(type, null, null, log)

  @Test
  fun aFullSweepSendsExactlyThese() {
    // A misspelt CODE_ID is dropped by the scanner without a word, so every string is spelt out here
    val settings = newlandBarcodeSettings(listOf(BarcodePlugin(BarcodeTypes.CODE128, 10, 15, log)), keepDefaults = false)

    val off = listOf(
      "AZTEC", "CODABAR", "CODE39", "CODE93", "DM", "EAN13", "EAN8", "UCCEAN128", "RSS", "ITF", "MAXIC", "MICROQR",
      "PDF417", "QR", "UPCA", "UPCE",
      "AIM128", "CODE11", "CODE16K", "CODE49", "COMPOSITE", "CSC", "DOTCODE", "IND25", "ISBN", "ISSN",
      "ITF14", "ITF6", "MATRIX25", "MICROPDF", "MSIPLSY", "PLSY", "STD25",
    ).map { NewlandBarcodeSetting(it, "Enable", "0") }
    assertEquals(
      listOf(
        NewlandBarcodeSetting("CODE128", "Enable", "1"),
        NewlandBarcodeSetting("CODE128", "Minlen", "10"),
        NewlandBarcodeSetting("CODE128", "Maxlen", "15"),
      ) + off,
      settings,
    )
  }

  @Test
  fun keepingDefaultsOnlyEnablesWhatWasAskedFor() {
    val settings = newlandBarcodeSettings(listOf(plugin(BarcodeTypes.CODE128)), keepDefaults = true)

    assertEquals(listOf(NewlandBarcodeSetting("CODE128", "Enable", "1")), settings)
  }

  @Test
  fun twoTypesSharingACodeIdDoNotCancelEachOther() {
    val shared = BarcodeTypes.GS1_DATABAR.newlandDecoderName()!!
    val settings = newlandBarcodeSettings(listOf(plugin(BarcodeTypes.GS1_DATABAR)), keepDefaults = false)

    assertEquals(
      listOf(NewlandBarcodeSetting(shared, "Enable", "1")),
      settings.filter { it.codeId == shared },
    )
  }

  @Test
  fun lengthsGoOutAsMinlenAndMaxlen() {
    val settings = newlandBarcodeSettings(listOf(BarcodePlugin(BarcodeTypes.CODE128, 10, 15, log)), keepDefaults = true)

    assertEquals(
      listOf(
        NewlandBarcodeSetting("CODE128", "Enable", "1"),
        NewlandBarcodeSetting("CODE128", "Minlen", "10"),
        NewlandBarcodeSetting("CODE128", "Maxlen", "15"),
      ),
      settings,
    )
  }

  @Test
  fun onlyTheLengthAskedForIsSent() {
    val settings = newlandBarcodeSettings(listOf(BarcodePlugin(BarcodeTypes.CODE39, 4, null, log)), keepDefaults = true)

    assertEquals(listOf(NewlandBarcodeSetting("CODE39", "Enable", "1"), NewlandBarcodeSetting("CODE39", "Minlen", "4")), settings)
  }

  @Test
  fun fixedLengthSymbologiesGetNoLengths() {
    val settings = newlandBarcodeSettings(listOf(BarcodePlugin(BarcodeTypes.EAN13, 10, 15, log)), keepDefaults = true)

    assertEquals(listOf(NewlandBarcodeSetting("EAN13", "Enable", "1")), settings)
  }

  @Test
  fun bothDataBarTypesEnableRssOnce() {
    val settings = newlandBarcodeSettings(listOf(plugin(BarcodeTypes.GS1_DATABAR), plugin(BarcodeTypes.GS1_DATABAR_EXPANDED)), keepDefaults = true)

    assertEquals(listOf(NewlandBarcodeSetting("RSS", "Enable", "1")), settings)
  }

  @Test
  fun aTypeNewlandCannotNameIsSkipped() {
    val settings = newlandBarcodeSettings(listOf(plugin(BarcodeTypes.MAILMARK)), keepDefaults = true)

    assertEquals(emptyList(), settings)
  }
}
