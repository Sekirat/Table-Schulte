package SRC.schultetable

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("SchultePrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // --- Grid size ---
        val seekGrid = findViewById<SeekBar>(R.id.seekGridSize)
        val tvGrid = findViewById<TextView>(R.id.tvGridSize)
        val currentSize = prefs.getInt("grid_size", 3)
        seekGrid.progress = currentSize - 3
        tvGrid.text = "Grid Size: $currentSize x $currentSize"

        seekGrid.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) {
                val size = p + 3
                tvGrid.text = "Grid Size: $size x $size"
                editor.putInt("grid_size", size).apply()
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })

        // THEMES     {[DON'T WORK!!!!!!']}
        val themeGroup = findViewById<RadioGroup>(R.id.themeGroup)
        when (prefs.getInt("theme_mode", 0)) {
            1 -> themeGroup.check(R.id.themeLight)
            2 -> themeGroup.check(R.id.themeDark)
            else -> themeGroup.check(R.id.themeAuto)
        }
        themeGroup.setOnCheckedChangeListener { _, id ->
            val modeCode = when(id) {
                R.id.themeLight -> 1
                R.id.themeDark -> 2
                else -> 0
            }
            editor.putInt("theme_mode", modeCode).apply()
            
            val appMode = when(modeCode) {
                1 -> AppCompatDelegate.MODE_NIGHT_NO
                2 -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            AppCompatDelegate.setDefaultNightMode(appMode)
        }

        // Vibro
        val vibModeGroup = findViewById<RadioGroup>(R.id.vibModeGroup)
        when (prefs.getInt("vib_mode", 3)) {
            0 -> vibModeGroup.check(R.id.vibDisabled)
            1 -> vibModeGroup.check(R.id.vibCorrect)
            2 -> vibModeGroup.check(R.id.vibWrong)
            else -> vibModeGroup.check(R.id.vibAlways)
        }
        vibModeGroup.setOnCheckedChangeListener { _, id ->
            val mode = when(id) {
                R.id.vibDisabled -> 0
                R.id.vibCorrect -> 1
                R.id.vibWrong -> 2
                else -> 3
            }
            editor.putInt("vib_mode", mode).apply()
        }

        val seekVibMs = findViewById<SeekBar>(R.id.seekVibMs)
        val tvVibMs = findViewById<TextView>(R.id.tvVibMs)
        val currentVibMs = prefs.getInt("vib_ms", 15) // По умолчанию теперь 15 мс
        seekVibMs.progress = currentVibMs
        tvVibMs.text = "Vibration Force: ${currentVibMs}ms"

        seekVibMs.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) {
                tvVibMs.text = "Vibration Force: ${p}ms"
                editor.putInt("vib_ms", p).apply()
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })

        // -highlight-
        val colorGroup = findViewById<RadioGroup>(R.id.colorGroup)
        when(prefs.getInt("highlight_color", 1)) {
            1 -> colorGroup.check(R.id.colorGreen)
            2 -> colorGroup.check(R.id.colorGray)
            0 -> colorGroup.check(R.id.colorOff)
        }
        colorGroup.setOnCheckedChangeListener { _, id ->
            val code = when(id) {
                R.id.colorGreen -> 1
                R.id.colorGray -> 2
                else -> 0
            }
            editor.putInt("highlight_color", code).apply()
        }

        // back
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}
