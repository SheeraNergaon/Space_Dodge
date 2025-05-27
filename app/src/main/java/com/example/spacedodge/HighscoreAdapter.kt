package com.example.spacedodge

import android.location.Geocoder
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
        val distance: TextView = view.findViewById(R.id.item_distance)
        val location: TextView = view.findViewById(R.id.item_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_highscore, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = highscores[position]

        holder.name.text = entry.name
        holder.score.text = "Score: ${entry.score}"
        holder.distance.text = "Distance: ${entry.distance}m"

        val geocoder = Geocoder(holder.itemView.context)
        val addresses = geocoder.getFromLocation(entry.latitude, entry.longitude, 1)
        val address = if (!addresses.isNullOrEmpty()) {
            addresses[0].getAddressLine(0) ?: "Unknown location"
        } else {
            "Unknown location"
        }

        holder.location.text = "Location: $address"

        holder.itemView.setOnClickListener { listener(entry) }
    }


    fun updateData(newList: List<HighscoreEntry>) {
        highscores = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = highscores.size
}

