package SRC.schultetable

import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import android.view.View

class SchulteAdapter(
    private val items: List<Int>,
    private val gridSize: Int,
    private val onClick: (Int, TextView) -> Unit
) : RecyclerView.Adapter<SchulteAdapter.ViewHolder>() {

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val displayMetrics = context.resources.displayMetrics
        
        // table
        val paddingPx = (32 * displayMetrics.density).toInt()
        val cellSize = (displayMetrics.widthPixels - paddingPx) / gridSize
        val marginPx = (3 * displayMetrics.density).toInt()
        
        val tv = TextView(context).apply {
            val params = RecyclerView.LayoutParams(cellSize, cellSize)
            params.setMargins(marginPx, marginPx, marginPx, marginPx)
            layoutParams = params
            
            gravity = Gravity.CENTER
            textSize = (cellSize / 7f)
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#334155"))
            
            // VERY IMPORTANT
            isHapticFeedbackEnabled = true
        }
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val value = items[position]
        holder.textView.text = value.toString()
        holder.textView.setOnClickListener { onClick(value, holder.textView) }
    }

    override fun getItemCount() = items.size
}
