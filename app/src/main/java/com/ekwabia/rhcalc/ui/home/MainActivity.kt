package com.ekwabia.rhcalc.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.ekwabia.rhcalc.R
import com.ekwabia.rhcalc.data.AppDatabase
import com.ekwabia.rhcalc.databinding.ActivityMainBinding
import com.ekwabia.rhcalc.sensors.BarometerController
import com.ekwabia.rhcalc.sensors.LightSensorController
import com.ekwabia.rhcalc.theme.ThemePreferences
import com.ekwabia.rhcalc.ui.history.HistoryActivity
import com.ekwabia.rhcalc.ui.input.TemperatureInputActivity
import com.ekwabia.rhcalc.ui.report.WeatherReportActivity
import com.ekwabia.rhcalc.util.IntentKeys
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var lightSensor: LightSensorController
    private lateinit var barometer: BarometerController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lightSensor = LightSensorController(this) { lux ->
            binding.textLux.text = getString(R.string.lux_format, lux)
            autoSwitchTheme(lux)
        }
        barometer = BarometerController(this) { pressure ->
            binding.textPressure.text = getString(R.string.pressure_format, pressure)
        }
        if (!barometer.isAvailable) {
            binding.textPressure.text = getString(R.string.barometer_unavailable)
        }

        binding.buttonEnterTemperature.setOnClickListener {
            startActivity(Intent(this, TemperatureInputActivity::class.java))
        }
        binding.buttonHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        binding.buttonViewReport.setOnClickListener { openLatestReport() }
    }

    private fun openLatestReport() {
        lifecycleScope.launch {
            val latest = AppDatabase.getInstance(applicationContext).calculationDao().getLatest()
            if (latest == null) {
                Toast.makeText(this@MainActivity, R.string.error_no_report_yet, Toast.LENGTH_SHORT).show()
                return@launch
            }
            val intent = Intent(this@MainActivity, WeatherReportActivity::class.java).apply {
                putExtra(IntentKeys.EXTRA_WET_BULB, latest.wetBulb)
                putExtra(IntentKeys.EXTRA_DRY_BULB, latest.dryBulb)
                putExtra(IntentKeys.EXTRA_PRESSURE, latest.pressure)
                putExtra(IntentKeys.EXTRA_RH, latest.relativeHumidity)
                putExtra(IntentKeys.EXTRA_SKIP_SAVE, true)
            }
            startActivity(intent)
        }
    }

    /**
     * Dead zone between the thresholds prevents rapid theme flip-flopping when
     * ambient light hovers near a single cutoff.
     */
    private fun autoSwitchTheme(lux: Float) {
        val desiredMode = when {
            lux < DARK_LUX_THRESHOLD -> AppCompatDelegate.MODE_NIGHT_YES
            lux > LIGHT_LUX_THRESHOLD -> AppCompatDelegate.MODE_NIGHT_NO
            else -> return
        }
        if (desiredMode != AppCompatDelegate.getDefaultNightMode()) {
            ThemePreferences.save(this, desiredMode)
            AppCompatDelegate.setDefaultNightMode(desiredMode)
        }
    }

    override fun onResume() {
        super.onResume()
        lightSensor.start()
        barometer.start()
    }

    override fun onPause() {
        super.onPause()
        lightSensor.stop()
        barometer.stop()
    }

    companion object {
        private const val DARK_LUX_THRESHOLD = 10f
        private const val LIGHT_LUX_THRESHOLD = 50f
    }
}
