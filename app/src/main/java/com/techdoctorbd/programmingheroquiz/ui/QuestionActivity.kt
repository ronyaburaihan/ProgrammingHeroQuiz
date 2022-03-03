package com.techdoctorbd.programmingheroquiz.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.techdoctorbd.programmingheroquiz.R
import com.techdoctorbd.programmingheroquiz.data.model.QuestionItem
import com.techdoctorbd.programmingheroquiz.data.network.QuizApi
import com.techdoctorbd.programmingheroquiz.data.network.RetrofitClient
import com.techdoctorbd.programmingheroquiz.data.repository.RemoteRepository
import com.techdoctorbd.programmingheroquiz.databinding.ActivityQuestionBinding
import com.techdoctorbd.programmingheroquiz.util.*
import com.techdoctorbd.programmingheroquiz.viewmodels.QuizViewModel


class QuestionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuestionBinding
    private lateinit var quizViewModel: QuizViewModel

    private lateinit var sharedPref: SharedPreferences

    private lateinit var quizApi: QuizApi
    private lateinit var repository: RemoteRepository

    private lateinit var progressDialog: CustomProgressDialog

    private var allQuestionsList: ArrayList<QuestionItem> = ArrayList()
    private var totalQuestionsToAnswer: Long = 0

    private lateinit var countdownTimer: CountDownTimer
    private var canAnswer = false
    private var currentQuestion = 0
    private var currentScore = 0
    private var maxScore = 0
    private var scoreOfThisQues = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init
        progressDialog = CustomProgressDialog(this)
        quizApi = RetrofitClient.client.create(QuizApi::class.java)
        repository = RemoteRepository(quizApi)
        quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        sharedPref = getSharedPreferences("ScorePref", Context.MODE_PRIVATE)

        maxScore = sharedPref.getInt(getString(R.string.saved_high_score_key), 0)

        if (checkConnection(this)) {
            loadQuestionList()
        } else {
            toast("No Internet Connection")
        }

    }

    private fun loadQuestionList() {
        quizViewModel.getQuestionList(repository)

        quizViewModel.questionListResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    progressDialog.dismiss()
                    toast(it.message!!)
                    Log.d("data", it.message)
                }
                is NetworkResult.Loading -> {
                    progressDialog.showLoadingBar("Please wait")
                }
                is NetworkResult.Success -> {
                    progressDialog.dismiss()
                    it.data?.also {
                        allQuestionsList = it.questions as ArrayList<QuestionItem>
                        startQuiz()
                    }
                }
            }
        }
    }

    private fun startQuiz() {
        totalQuestionsToAnswer = allQuestionsList.size.toLong()

        binding.btnOptionOne.setOnClickListener(this)
        binding.btnOptionTwo.setOnClickListener(this)
        binding.btnOptionThree.setOnClickListener(this)
        binding.btnOptionFour.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)

        loadUI()
    }

    private fun loadUI() {
        enableOptions(true)
        loadQuestions(0)
    }

    private fun enableOptions(enable: Boolean) {
        binding.btnOptionOne.isEnabled = enable
        binding.btnOptionTwo.isEnabled = enable
        binding.btnOptionThree.isEnabled = enable
        binding.btnOptionFour.isEnabled = enable
        if (!enable) {
            if (currentQuestion.toLong() == totalQuestionsToAnswer - 1) {
                binding.btnBack.visibility = View.VISIBLE
            }
        }
    }

    private fun getRandomNumber(minimum: Int, maximum: Int): Int {
        return (Math.random() * (maximum - minimum)).toInt() + minimum
    }

    private fun resetOptions() {
        binding.progressBar.progress = 0
        binding.btnOptionOne.setBackgroundResource(R.drawable.default_button_background)
        binding.btnOptionTwo.setBackgroundResource(R.drawable.default_button_background)
        binding.btnOptionThree.setBackgroundResource(R.drawable.default_button_background)
        binding.btnOptionFour.setBackgroundResource(R.drawable.default_button_background)
        enableOptions(true)
    }

    private fun verifyAnswer(optionButton: Button) {
        countdownTimer.cancel()
        if (canAnswer) {
            canAnswer = false
            if (getCorrectAnswer(
                    allQuestionsList[currentQuestion].correctAnswer.lowercase(),
                    allQuestionsList[currentQuestion]
                ) == optionButton.text
            ) {
                optionButton.setBackgroundResource(R.drawable.correct_answer_background)
                currentScore += scoreOfThisQues
                val scoreText = "Score:$currentScore"
                binding.tvCurrentScore.text = scoreText
            } else {
                optionButton.setBackgroundResource(R.drawable.wrong_answer_background)
                showCorrectAnswer(
                    allQuestionsList[currentQuestion].correctAnswer.lowercase(),
                    allQuestionsList[currentQuestion]
                )
            }
        }
        enableOptions(false)

        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (currentQuestion < totalQuestionsToAnswer - 1) {
                    currentQuestion++
                    loadQuestions(currentQuestion)
                    resetOptions()
                    enableOptions(true)
                }
            }
        }.start()
    }

    private fun showCorrectAnswer(correctOption: String, questionItem: QuestionItem) {
        when (getCorrectAnswer(correctOption, questionItem)) {
            binding.btnOptionOne.text -> binding.btnOptionOne.setBackgroundResource(R.drawable.correct_answer_background)
            binding.btnOptionTwo.text -> binding.btnOptionTwo.setBackgroundResource(R.drawable.correct_answer_background)
            binding.btnOptionThree.text -> binding.btnOptionThree.setBackgroundResource(R.drawable.correct_answer_background)
            binding.btnOptionFour.text -> binding.btnOptionFour.setBackgroundResource(R.drawable.correct_answer_background)
        }
    }

    private fun getCorrectAnswer(correctAnswer: String, questionItem: QuestionItem): String {
        return when (correctAnswer) {
            "a" -> questionItem.answers.a
            "b" -> questionItem.answers.b
            "c" -> questionItem.answers.c
            "d" -> questionItem.answers.d
            else -> questionItem.answers.a
        }
    }

    private fun loadQuestions(i: Int) {
        val questionItem = allQuestionsList[i]
        scoreOfThisQues = questionItem.score
        binding.tvQuestion.text = questionItem.question
        val questionNo = "Question:${i + 1}/$totalQuestionsToAnswer"
        binding.tvQuestionNo.text = questionNo
        if (!questionItem.questionImageUrl.isNullOrEmpty()) {
            binding.questionImage.visibility = View.VISIBLE
            binding.questionImage.loadImageFromUrl(this, questionItem.questionImageUrl)
        } else {
            binding.questionImage.visibility = View.GONE
        }
        if (questionItem.answers.a.isNotEmpty()) {
            binding.btnOptionOne.visibility = View.VISIBLE
            binding.btnOptionOne.text = questionItem.answers.a
        } else {
            binding.btnOptionOne.visibility = View.GONE
        }
        if (questionItem.answers.b.isNotEmpty()) {
            binding.btnOptionTwo.visibility = View.VISIBLE
            binding.btnOptionTwo.text = questionItem.answers.b
        } else {
            binding.btnOptionTwo.visibility = View.GONE
        }

        if (questionItem.answers.c.isNotEmpty()) {
            binding.btnOptionThree.visibility = View.VISIBLE
            binding.btnOptionThree.text = questionItem.answers.c
        } else {
            binding.btnOptionThree.visibility = View.GONE
        }

        if (questionItem.answers.d.isNotEmpty()) {
            binding.btnOptionFour.visibility = View.VISIBLE
            binding.btnOptionFour.text = questionItem.answers.d
        } else {
            binding.btnOptionFour.visibility = View.GONE
        }

        canAnswer = true
        currentQuestion = i
        startTimer()
    }

    private fun startTimer() {
        countdownTimer = object : CountDownTimer(10000, 10) {
            override fun onTick(millisUntilFinished: Long) {
                binding.progressBar.progress = (millisUntilFinished / 10).toInt()
            }

            override fun onFinish() {
                if (currentQuestion < totalQuestionsToAnswer - 1) {
                    currentQuestion++
                    loadQuestions(currentQuestion)
                    resetOptions()
                    enableOptions(true)
                }
            }
        }
        countdownTimer.start()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_option_one -> verifyAnswer(binding.btnOptionOne)
            R.id.btn_option_two -> verifyAnswer(binding.btnOptionTwo)
            R.id.btn_option_three -> verifyAnswer(binding.btnOptionThree)
            R.id.btn_option_four -> verifyAnswer(binding.btnOptionFour)
            R.id.btn_back -> {
                if (currentScore > maxScore) {
                    with(sharedPref.edit()) {
                        putInt(getString(R.string.saved_high_score_key), currentScore)
                        apply()
                    }
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this@QuestionActivity.finish()
            }
        }
    }
}