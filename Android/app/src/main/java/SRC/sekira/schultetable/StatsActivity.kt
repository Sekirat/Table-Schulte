package SRC.schultetable

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val tvContent = findViewById<TextView>(R.id.tvStatsContent)
        val btnClear = findViewById<Button>(R.id.btnClearStats)

        val prefs = getSharedPreferences("SchulteStats", Context.MODE_PRIVATE)
        
        fun loadStats() {
            val history = prefs.getString("history", "No data yet")
            tvContent.text = history
        }

        loadStats()

        btnClear.setOnClickListener {
            prefs.edit().clear().apply()
            loadStats()
        }
    }
}