package com.kbachtbasi.messaging.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kbachtbasi.messaging.databinding.FragmentHomeBinding
import com.kbachtbasi.messaging.utils.RecentChat

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recentChatsRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        viewModel.recentUserChats.observe(viewLifecycleOwner) { recentChatsList ->
            binding.recentChatsRecyclerView.adapter = RecentChatAdapter(recentChatsList)
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            if (!query.equals(binding.searchLayout.query.toString())) {
                binding.searchLayout.setQuery(query, false)
            }
        }
        viewModel.fetchRecentChatWithUpdatedQuery()

        binding.searchLayout.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.onQueryChanged(newText)
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchRecentChatWithUpdatedQuery()
    }
}
