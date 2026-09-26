package com.ekwabia.rhcalc.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ekwabia.rhcalc.R
import com.ekwabia.rhcalc.data.CalculationRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<CalculationRecord, HistoryAdapter.RecordViewHolder>(DIFF_CALLBACK) {

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val summary: TextView = view.findViewById(R.id.textSummary)
        val timestamp: TextView = view.findViewById(R.id.textTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = getItem(position)
        holder.summary.text = holder.itemView.context.getString(
            R.string.history_item_summary, record.dryBulb, record.wetBulb, record.relativeHumidity
        )
        holder.timestamp.text = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
            .format(Date(record.timestamp))
    }

    fun recordAt(position: Int): CalculationRecord = getItem(position)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CalculationRecord>() {
            override fun areItemsTheSame(oldItem: CalculationRecord, newItem: CalculationRecord) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CalculationRecord, newItem: CalculationRecord) =
                oldItem == newItem
        }
    }
}
