import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import android.content.Context
import android.content.ContextWrapper


class Utilities {
    companion object {
        fun getBatteryStatus(context: Context): String {
            val batteryStatus: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13 (Tiramisu) and higher, use registerReceiver with intent filter and broadcast flag
                val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                context.registerReceiver(null, intentFilter)
            } else {
                // For older versions, use the deprecated method (still works but not recommended for Tiramisu+)
                context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            }

            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

            val batteryPercentage = if (level >= 0 && scale > 0) {
                (level.toFloat() / scale.toFloat() * 100).toInt()
            } else {
                -1
            }

            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

            val batteryHealth = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
            return "$batteryPercentage|$status|$batteryHealth"
        }
    }
}