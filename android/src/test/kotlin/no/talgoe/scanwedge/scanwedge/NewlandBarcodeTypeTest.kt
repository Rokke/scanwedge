package no.talgoe.scanwedge.scanwedge

import kotlin.test.Test
import kotlin.test.assertEquals

internal class NewlandBarcodeTypeTest {
  @Test
  fun everySymbologyAProfileCanEnableIsNamed() {
    val named = mapOf(
      "CODE39" to BarcodeTypes.CODE39,
      "CODE93" to BarcodeTypes.CODE93,
      "CODE128" to BarcodeTypes.CODE128,
      "CODABAR" to BarcodeTypes.CODABAR,
      "EAN8" to BarcodeTypes.EAN8,
      "EAN13" to BarcodeTypes.EAN13,
      "UCCEAN128" to BarcodeTypes.EAN128,
      "GS1_128" to BarcodeTypes.EAN128,
      "ITF" to BarcodeTypes.I2OF5,
      "ITF14" to BarcodeTypes.I2OF5,
      "UPCA" to BarcodeTypes.UPCA,
      "UPCE" to BarcodeTypes.UPCE0,
      "RSS14" to BarcodeTypes.GS1_DATABAR,
      "RSSLIMITED" to BarcodeTypes.GS1_DATABAR,
      "RSSEXPANDED" to BarcodeTypes.GS1_DATABAR_EXPANDED,
      "RSSFAMILY" to BarcodeTypes.GS1_DATABAR,
      "QRCODE" to BarcodeTypes.QRCODE,
      "DATAMATRIX" to BarcodeTypes.DATAMATRIX,
      "PDF417" to BarcodeTypes.PDF417,
      "AZTEC" to BarcodeTypes.AZTEC,
      "MAXICODE" to BarcodeTypes.MAXICODE,
    )

    for ((codeId, expected) in named) {
      assertEquals(expected, BarcodeTypes.fromNewlandCode(codeId), "$codeId came back wrong")
    }
  }

  @Test
  fun aNameWeDoNotHandleIsUnknown() {
    assertEquals(BarcodeTypes.UNKNOWN, BarcodeTypes.fromNewlandCode("TELEPEN"))
    assertEquals(BarcodeTypes.UNKNOWN, BarcodeTypes.fromNewlandCode(null))
  }
}
