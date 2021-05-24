package com.shumikhin.rainandsun.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shumikhin.rainandsun.databinding.MainActivityBinding
import com.shumikhin.rainandsun.view.details.DetailsFragment
import com.shumikhin.rainandsun.view.main.MainFragment

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

