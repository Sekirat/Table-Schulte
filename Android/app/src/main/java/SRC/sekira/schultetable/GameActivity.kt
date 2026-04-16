package SRC.schultetable

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioAttributes
import android.os.*
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class GameActivity : AppCompatActivity() {
    private var nextNumber = 1
    private var startTime = 0L
    private lateinit var vibrator: Vibrator
    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var myGridLayout: android.widget.GridLayout 
    
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - startTime
            tvTimer.text = String.format("%02d:%02d.%03d", (millis/60000), (millis/1000)%60, millis%1000)
            handler.postDelayed(this, 10)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("SchultePrefs", Context.MODE_PRIVATE)
        when (prefs.getInt("theme_mode", 0)) {
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        myGridLayout = findViewById(R.id.gridLayout)
        btnStart = findViewById(R.id.btnStart)
        tvTimer = findViewById(R.id.tvTimer)

        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        findViewById<ImageButton>(R.id.btnStats).setOnClickListener {
            showStatsDialog()
        }

        btnStart.setOnClickListener { startGame() }
    }

    override fun onResume() {
        super.onResume()
        setupGrid(false)
    }

    private fun setupGrid(isPlaying: Boolean) {
        val prefs = getSharedPreferences("SchultePrefs", Context.MODE_PRIVATE)
        val size = prefs.getInt("grid_size", 3).coerceIn(3, 10)
        
        myGridLayout.removeAllViews()
        myGridLayout.columnCount = size
        
        val numbers = (1..size*size).toList().shuffled()
        
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        
        val screenPaddingPx = (32 * density).toInt()
        
        val marginDp = 4
        val marginPx = (marginDp * density).toInt()
        
        val availableWidth = displayMetrics.widthPixels - screenPaddingPx
        
        val cellSize = (availableWidth / size) - (marginPx * 2)

        for (num in numbers) {
            val btn = Button(this).apply {
                text = num.toString()
                textSize = if (size > 6) 16f else 22f
                isEnabled = isPlaying
                
                val params = android.widget.GridLayout.LayoutParams()
                params.width = cellSize
                params.height = cellSize
                params.setMargins(marginPx, marginPx, marginPx, marginPx)
                layoutParams = params
                
                setBackgroundResource(R.drawable.cell_bg_normal)
                setTextColor(Color.WHITE)
                setPadding(0, 0, 0, 0)
                setOnClickListener { checkNum(this, num) }
            }
            myGridLayout.addView(btn)
        }

        if (isPlaying) {
            myGridLayout.alpha = 1.0f
            btnStart.visibility = View.INVISIBLE
            tvTimer.text = "00:00.000"
        } else {
            myGridLayout.alpha = 0.3f
            btnStart.visibility = View.VISIBLE
        }
    }

    private fun startGame() {
        nextNumber = 1
        setupGrid(true)
        startTime = System.currentTimeMillis()
        handler.post(timerRunnable)
    }

    private fun checkNum(button: Button, num: Int) {
        val prefs = getSharedPreferences("SchultePrefs", Context.MODE_PRIVATE)
        val vibMode = prefs.getInt("vib_mode", 3)
        val vibMs = prefs.getInt("vib_ms", 15).toLong() 
        val colorType = prefs.getInt("highlight_color", 1)

        if (num == nextNumber) {
            if (vibMode == 1 || vibMode == 3) triggerVib(vibMs)
            
            when(colorType) {
                1 -> button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#81C784"))
                2 -> button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3A465B"))
            }
            
            button.isEnabled = false
            nextNumber++
            if (nextNumber > myGridLayout.childCount) finishGame()
        } else {
            if (vibMode == 2 || vibMode == 3) triggerVib(vibMs + 20)
        }
    }

    private fun triggerVib(ms: Long) {
        if (ms <= 0) return
        val audioAttrs = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE), audioAttrs)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(ms, audioAttrs)
        }
    }

    private fun finishGame() {
        handler.removeCallbacks(timerRunnable)
        val finalTime = tvTimer.text.toString()
        
        val statsPrefs = getSharedPreferences("SchulteStats", Context.MODE_PRIVATE)
        val history = statsPrefs.getString("history", "")
        statsPrefs.edit().putString("history", "$finalTime\n$history").apply()
        
        AlertDialog.Builder(this)
            .setTitle("Finish!")
            .setMessage("Time: $finalTime")
            .setPositiveButton("OK", null)
            .show()
            
        btnStart.text = "TRY AGAIN"
        setupGrid(false)
    }

    private fun showStatsDialog() {
        val statsPrefs = getSharedPreferences("SchulteStats", Context.MODE_PRIVATE)
        val history = statsPrefs.getString("history", "No data yet") ?: "No data yet"
        
        AlertDialog.Builder(this)
            .setTitle("History")
            .setMessage(history)
            .setPositiveButton("OK", null)
            .show()
    }
}
