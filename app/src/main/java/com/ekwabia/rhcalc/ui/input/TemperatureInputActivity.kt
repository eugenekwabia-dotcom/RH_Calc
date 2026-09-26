package com.ekwabia.rhcalc.ui.input

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ekwabia.rhcalc.R
import com.ekwabia.rhcalc.calc.Humidity
import com.ekwabia.rhcalc.databinding.ActivityTemperatureInputBinding
import com.ekwabia.rhcalc.sensors.BarometerController
import com.ekwabia.rhcalc.sensors.ShakeDetector
import com.ekwabia.rhcalc.ui.report.WeatherReportActivity
import com.ekwabia.rhcalc.util.IntentKeys

class TemperatureInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTemperatureInputBinding
    private lateinit var barometer: BarometerController
    private lateinit var shakeDetector: ShakeDetector
    private var lastResult: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemperatureInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editPressure.setText(Humidity.STANDARD_PRESSURE_HPA.toString())

        barometer = BarometerController(this) { pressure ->
            binding.editPressure.setText(pressure.toString())
        }
        shakeDetector = ShakeDetector(this) { clearInputs() }

        binding.buttonCalculate.setOnClickListener { calculate() }
        binding.buttonSendToReport.setOnClickListener { sendToReport() }
    }

    override fun onResume() {
        super.onResume()
        barometer.start()
        shakeDetector.start()
    }

    override fun onPause() {
        super.onPause()
        barometer.stop()
        shakeDetector.stop()
    }

    private fun clearInputs() {
        binding.editWetBulb.text?.clear()
        binding.editDryBulb.text?.clear()
        binding.editPressure.setText(Humidity.STANDARD_PRESSURE_HPA.toString())
        binding.textResult.text = ""
        lastResult = null
    }

    private fun calculate() {
        val wetBulb = binding.editWetBulb.text?.toString()?.toDoubleOrNull()
        val dryBulb = binding.editDryBulb.text?.toString()?.toDoubleOrNull()
        val pressure = binding.editPressure.text?.toString()?.toDoubleOrNull()

        if (wetBulb == null) {
            binding.editWetBulb.error = getString(R.string.error_required)
            return
        }
        if (dryBulb == null) {
            binding.editDryBulb.error = getString(R.string.error_required)
            return
        }
        if (pressure == null) {
            binding.editPressure.error = getString(R.string.error_required)
            return
        }
        if (wetBulb > dryBulb) {
            binding.editWetBulb.error = getString(R.string.error_wetbulb_exceeds_drybulb)
            return
        }
        if (pressure < 800.0 || pressure > 1100.0) {
            Toast.makeText(this, R.string.warning_pressure_out_of_range, Toast.LENGTH_LONG).show()
        }

        val rh = Humidity.relativeHumidity(wetBulb, dryBulb, pressure)
        lastResult = rh
        binding.textResult.text = getString(R.string.result_format, wetBulb, dryBulb, pressure, rh)
    }

    private fun sendToReport() {
        val rh = lastResult
        if (rh == null) {
            Toast.makeText(this, R.string.error_calculate_first, Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, WeatherReportActivity::class.java).apply {
            putExtra(IntentKeys.EXTRA_WET_BULB, binding.editWetBulb.text.toString().toDouble())
            putExtra(IntentKeys.EXTRA_DRY_BULB, binding.editDryBulb.text.toString().toDouble())
            putExtra(IntentKeys.EXTRA_PRESSURE, binding.editPressure.text.toString().toDouble())
            putExtra(IntentKeys.EXTRA_RH, rh)
        }
        startActivity(intent)
    }
}
