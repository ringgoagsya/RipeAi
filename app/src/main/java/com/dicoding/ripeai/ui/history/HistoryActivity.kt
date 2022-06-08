package com.dicoding.ripeai.ui.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.ripeai.R
import com.dicoding.ripeai.adapter.ListHistoryAdapter
import com.dicoding.ripeai.databinding.ActivityHistoryBinding
import com.dicoding.ripeai.datastore.api.ApiConfig
import com.dicoding.ripeai.datastore.response.DataItem
import com.dicoding.ripeai.datastore.response.HistoryResponse
import com.dicoding.ripeai.ui.about.AboutActivity
import com.dicoding.ripeai.ui.main.MainActivity
import com.dicoding.ripeai.ui.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HistoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHistoryBinding
    private lateinit var tvText: TextView
    private lateinit var navigation: BottomNavigationView
    companion object {
        private const val TAG = "HistoryActivity"
        private const val EMAIL = "john@email.com"
        const val EXTRA_TITLE = "extra_title"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutManager = LinearLayoutManager(this)

        binding.rvHistory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvHistory.addItemDecoration(itemDecoration)
        init()
        navigationListener()

        findHistory()
    }

    private fun findHistory() {
        val client = ApiConfig.getApiService().getHistory(EMAIL)
        client.enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
//                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setRestaurantData(responseBody.data)
                        setReviewData(responseBody.data)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
//                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
    private fun setRestaurantData(data: List<DataItem>) {
//        binding.textView.text = data.
//        binding.des.text = data.ripeness
//        Glide.with(this@MainActivity)
//            .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
//            .into(binding.ivPicture)
    }
    private fun setReviewData(data: List<DataItem>) {
        val listHistory = ArrayList<String>()
        for (data in data) {
            listHistory.add(
                """
                ${data.fruit}
                - ${data.ripeness}
                """.trimIndent()
            )
        }
        val adapter = ListHistoryAdapter(listHistory)
        binding.rvHistory.adapter = adapter

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)

        return true
    }
    private fun init() {
        tvText = findViewById(R.id.textView)
        navigation = findViewById(R.id.navigation)
        navigation.selectedItemId = R.id.button_history
        val title = intent.getStringExtra(EXTRA_TITLE)
        supportActionBar?.title = title
    }
    private fun navigationListener() {
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.button_home -> {
                    tvText.text = item.title
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_TITLE, item.title.toString())
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.button_about -> {
                    tvText.text = item.title
                    val intent = Intent(this, AboutActivity::class.java)
                    intent.putExtra(AboutActivity.EXTRA_TITLE, item.title.toString())
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.button_history -> {
                    tvText.text = item.title
                    val intent = Intent(this, HistoryActivity::class.java)
                    intent.putExtra(HistoryActivity.EXTRA_TITLE, item.title.toString())
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.button_profile -> {
                    tvText.text = item.title
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra(ProfileActivity.EXTRA_TITLE, item.title.toString())
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}