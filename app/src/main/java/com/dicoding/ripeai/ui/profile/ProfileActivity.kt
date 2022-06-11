package com.dicoding.ripeai.ui.profile

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.ripeai.R
import com.dicoding.ripeai.databinding.ActivityProfileBinding
import com.dicoding.ripeai.datastore.api.ApiConfig
import com.dicoding.ripeai.datastore.response.*
import com.dicoding.ripeai.ui.UserViewModelFactory
import com.dicoding.ripeai.ui.about.AboutActivity
import com.dicoding.ripeai.ui.history.HistoryActivity
import com.dicoding.ripeai.ui.history.HistoryViewModel
import com.dicoding.ripeai.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var tvText: TextView
    private lateinit var navigation: BottomNavigationView
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        navigationListener()

        findUser()

    }

    private fun findUser() {
        val factory: UserViewModelFactory = UserViewModelFactory.getInstance(this)
        profileViewModel = ViewModelProvider(
            this,
            factory
        )[ProfileViewModel::class.java]

        profileViewModel.getToken().observe(this){ token ->
            val client = ApiConfig.getApiService().getUser(token)
            client.enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val data = responseBody.data
                            setData(data as ArrayList<DataUser>)
                        }
                    } else {
                        Log.e(this@ProfileActivity.toString(), "response succesfull is:  ${response.isSuccessful}")
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e(this@ProfileActivity.toString(), "onFailure dimana:  ${call.isExecuted}")
                }

            })
        }
    }

    private fun setData(data: ArrayList<DataUser>) {
        for (bind in data) {
            binding.apply {
                binding.phone.text = bind.phone
                binding.email.text = bind.email
                binding.name.text = bind.firstname+bind.lastname
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)

        return true
    }
    private fun init() {
        tvText = findViewById(R.id.textView)
        navigation = findViewById(R.id.navigation)
        navigation.selectedItemId = R.id.button_profile
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
                    intent.putExtra(EXTRA_TITLE, item.title.toString())
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_TITLE = "extra_title"
        const val TAG = "ProfileActivity"
    }
}