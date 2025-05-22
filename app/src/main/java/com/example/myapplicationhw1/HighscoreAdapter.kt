package com.example.myapplicationhw1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HighscoreAdapter(
    private var highscores: List<HighscoreEntry>,
    private val listener: (HighscoreEntry) -> Unit
) : RecyclerView.Adapter<HighscoreAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        val score: TextView = view.findViewById(R.id.item_score)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_highscore, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = highscores[position]
        holder.name.text = entry.name
        holder.score.text = "Score: ${entry.score}   Distance: ${entry.distance}m"
        holder.itemView.setOnClickListener { listener(entry) }
    }
    fun updateData(newList: List<HighscoreEntry>) {
        highscores = newList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = highscores.size
}
