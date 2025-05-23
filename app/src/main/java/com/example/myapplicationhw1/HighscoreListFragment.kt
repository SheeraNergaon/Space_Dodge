package com.example.myapplicationhw1

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
    private var highscores: List<HighscoreEntry> = listOf() // Start empty

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_highscore_list, container, false)
        recyclerView = view.findViewById(R.id.highscore_recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // ✅ Load scores here
        highscores = HighscoreStorage.load(requireContext())

        // ✅ Use loaded scores
        adapter = HighscoreAdapter(highscores) { entry ->
            (activity as? OnHighscoreClickListener)?.onHighscoreClicked(entry)
        }

        recyclerView.adapter = adapter

        return view
    }


    fun updateHighscores(highscores: List<HighscoreEntry>) {
        adapter.updateData(highscores)
    }

}
