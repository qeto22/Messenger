package com.kbachtbasi.messaging.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kbachtbasi.messaging.databinding.ActivitySearchUserBinding

class SearchUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchUserBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userAdapter = UserAdapter(emptyList())

        val recyclerView: RecyclerView = binding.searchedUserRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter

        userViewModel.error.observe(this) {
            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
        }

        userViewModel.usersListLiveData.observe(this) { users ->
            userAdapter.setUserList(users)
            if (users.size == 0) {
                binding.clientNotFoundMessage.visibility = View.VISIBLE
            } else {
                binding.clientNotFoundMessage.visibility = View.GONE
            }
        }

        userViewModel.loadUsers()

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty() || newText.length < 4) {
                    userViewModel.loadUsers()
                } else {
                    userViewModel.searchUsers(newText)

                }
                return true
            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleUserPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleUserPosition == totalItemCount - 1) {
                    userViewModel.loadMoreUsers()
                }
            }
        })

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

}