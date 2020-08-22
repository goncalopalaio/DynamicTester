package com.gplio.dynamictester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PlaceholderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log(getString(R.string.help_am_instrument))
    }
}