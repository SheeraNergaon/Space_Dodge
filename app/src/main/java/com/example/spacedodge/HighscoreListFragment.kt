package com.example.spacedodge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HighscoreListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HighscoreAdapter
    private var highscores: List<HighscoreEntry> = listOf()
    private var itemClickListener: ((HighscoreEntry) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_highscore_list, container, false)
        recyclerView = view.findViewById(R.id.highscore_recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = HighscoreAdapter(highscores) { entry ->
            itemClickListener?.invoke(entry)
        }

        recyclerView.adapter = adapter

        return view
    }

    fun setHighscores(highscores: List<HighscoreEntry>) {
        this.highscores = highscores
        if (::adapter.isInitialized) {
            adapter.updateData(highscores)
        }
    }

    fun setOnItemClickListener(listener: (HighscoreEntry) -> Unit) {
        this.itemClickListener = listener
    }
}
