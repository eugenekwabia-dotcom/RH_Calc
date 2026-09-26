package com.ekwabia.rhcalc.ui.report

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ekwabia.rhcalc.R
import com.ekwabia.rhcalc.calc.Altitude
import com.ekwabia.rhcalc.calc.Humidity
import com.ekwabia.rhcalc.data.AppDatabase
import com.ekwabia.rhcalc.data.CalculationRecord
import com.ekwabia.rhcalc.databinding.ActivityWeatherReportBinding
import com.ekwabia.rhcalc.sensors.HumiditySensorController
import com.ekwabia.rhcalc.sensors.ShakeDetector
import com.ekwabia.rhcalc.util.IntentKeys
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherReportBinding
    private lateinit var humiditySensor: HumiditySensorController
    private lateinit var shakeDetector: ShakeDetector
    private var alreadySaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alreadySaved = savedInstanceState?.getBoolean(KEY_ALREADY_SAVED)
            ?: intent.getBooleanExtra(IntentKeys.EXTRA_SKIP_SAVE, false)

        val wetBulb = intent.getDoubleExtra(IntentKeys.EXTRA_WET_BULB, 0.0)
        val dryBulb = intent.getDoubleExtra(IntentKeys.EXTRA_DRY_BULB, 0.0)
        val pressure = intent.getDoubleExtra(IntentKeys.EXTRA_PRESSURE, Humidity.STANDARD_PRESSURE_HPA)
        val rh = intent.getDoubleExtra(IntentKeys.EXTRA_RH, 0.0)
        val timestamp = System.currentTimeMillis()
        val dateText = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))

        binding.textReport.text = getString(R.string.report_format, dryBulb, wetBulb, pressure, rh, dateText)
        binding.gaugeHumidity.setHumidity(rh.toFloat())

        binding.textAltitude.text = getString(R.string.altitude_format, Altitude.fromPressure(pressure))

        humiditySensor = HumiditySensorController(this) { deviceRh ->
            binding.textDeviceHumidity.text = getString(R.string.device_humidity_format, deviceRh)
        }
        if (!humiditySensor.isAvailable) {
            binding.textDeviceHumidity.text = getString(R.string.device_humidity_unavailable)
        }

        shakeDetector = ShakeDetector(this) { binding.gaugeHumidity.setHumidity(rh.toFloat()) }

        if (!alreadySaved) {
            val dao = AppDatabase.getInstance(applicationContext).calculationDao()
            lifecycleScope.launch {
                dao.insert(
                    CalculationRecord(
                        wetBulb = wetBulb,
                        dryBulb = dryBulb,
                        pressure = pressure,
                        relativeHumidity = rh,
                        timestamp = timestamp
                    )
                )
            }
            alreadySaved = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ALREADY_SAVED, alreadySaved)
    }

    override fun onResume() {
        super.onResume()
        humiditySensor.start()
        shakeDetector.start()
    }

    override fun onPause() {
        super.onPause()
        humiditySensor.stop()
        shakeDetector.stop()
    }

    companion object {
        private const val KEY_ALREADY_SAVED = "key_already_saved"
    }
}
