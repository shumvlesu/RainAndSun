package com.shumikhin.rainandsun.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.get
import com.shumikhin.rainandsun.R
import com.shumikhin.rainandsun.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    //Использую ViewBinding
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                //.replace(R.id.container, MainFragment.newInstance())
                .replace(binding.container.id, MainFragment.newInstance())
                .commitNow()
        }
    }
}

