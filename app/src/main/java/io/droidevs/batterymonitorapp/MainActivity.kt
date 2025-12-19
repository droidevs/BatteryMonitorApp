package io.droidevs.batterymonitorapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.droidevs.batterymonitorapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    private lateinit var tvPercentage : TextView
    private lateinit var tvStatus : TextView


    private val batteryReciever = object : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.i("Charging", "Changed")
            updateBatteryInfo(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvPercentage = binding.tvPercentage
        tvStatus = binding.tvStatus

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReciever, filter)

        updateBatteryInfo(registerReceiver(null, filter))

    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onStart() {
        super.onStart()
    }

    fun updateBatteryInfo(intent : Intent?) {
        Log.i("Charging", "Update Battery Info")
        if(intent == null) return

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        val percentage = if (level != -1 && scale != -1) (level * 100 / scale) else 0

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val chargeType = when(plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            BatteryManager.BATTERY_PLUGGED_DOCK -> "Dock"
            else -> "No Charging"
        }

        if (level == -1 && scale == -1 && status == -1 && plugged == -1)
            return

        Log.i("Charging : ", percentage.toString() + " "+ "chargeType : " + chargeType + " " + "isCharging : " + isCharging.toString() + "")

        tvPercentage.text = "$percentage%"
        tvStatus.text = if (isCharging) "Charging $chargeType" else "Not Charging"
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReciever)
    }
}