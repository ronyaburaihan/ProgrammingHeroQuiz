package com.techdoctorbd.programmingheroquiz.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.techdoctorbd.programmingheroquiz.R
import com.techdoctorbd.programmingheroquiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("ScorePref",Context.MODE_PRIVATE)

        binding.btnPlay.setOnClickListener {
            startActivity(Intent(this, QuestionActivity::class.java))
        }
    }

    private fun setScore() {
        val highScore = sharedPreferences.getInt(getString(R.string.saved_high_score_key), 0)
        val scoreText = "$highScore Point"
        binding.tvScore.text = scoreText
    }

    override fun onResume() {
        super.onResume()
        setScore()
    }
}