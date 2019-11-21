/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    data class Question(
            val text: String,
            val answers: List<String>)

    // The first answer is the correct one.  We randomize the answers before showing the text.
    // All questions must have four answers.  We'd want these to contain references to string
    // resources so we could internationalize. (Or better yet, don't define the questions in code...)
    private val questions: MutableList<Question> = mutableListOf(
            Question(text = "ช่วงสองสัปดาห์นี้คุณ...เบื่อ ไม่สนใจอยากทำอะไร?",
                    answers = listOf("เป็นทุกวัน", "เป็นบ่อย แต่ไม่ทุกวัน", "เป็นบางวัน", "ไม่เป็นเลย")),
            Question(text = "ช่วงสองสัปดาห์นี้คุณ...ไม่สบายใจ ท้อแท้?",
                    answers = listOf("เป็นทุกวัน", "เป็นบ่อย แต่ไม่ทุกวัน", "เป็นบางวัน", "ไม่เป็นเลย")),
            Question(text = "ช่วงสองสัปดาห์นี้คุณ...หลับยาก หลับๆ ตื่นๆ",
                    answers = listOf("เป็นทุกวัน", "เป็นบ่อย แต่ไม่ทุกวัน", "เป็นบางวัน", "ไม่เป็นเลย")),
            Question(text = "ช่วงสองสัปดาห์นี้คุณ...เหนื่อยง่าย ไม่ค่อยมีแรง?",
                    answers = listOf("เป็นทุกวัน", "เป็นบ่อย แต่ไม่ทุกวัน", "เป็นบางวัน", "ไม่เป็นเลย")),
            Question(text = "ช่วงสองสัปดาห์นี้คุณ...เบื่ออาหาร หรือกินมากเกินไป?",
                    answers = listOf("เป็นทุกวัน", "เป็นบ่อย แต่ไม่ทุกวัน", "เป็นบางวัน", "ไม่เป็นเลย")),
            Question(text = "ช่วงสองสัปดาห์นี้คุณ...รู้สึกไม่ดีกับตัวเอง (คิดว่าตัวเองล้มเหลว หรือครอบครัวผิดหวัง)?",
                    answers = listOf("เป็นทุกวัน", "เป็นบ่อย แต่ไม่ทุกวัน", "เป็นบางวัน", "ไม่เป็นเลย")),
            Question(text = "ช่วงสองสัปดาห์นี้คุณ...สมาธิไม่ดี? (ทำงานหรือเรียนแล้วไม่ค่อยมีสมาธิ)",
                    answers = listOf("เป็นทุกวัน", "เป็นบ่อย แต่ไม่ทุกวัน", "เป็นบางวัน", "ไม่เป็นเลย")),
            Question(text = "ช่วงสองสัปดาห์นี้คุณ...พูดช้า ทำอะไรช้าลงจนคนอื่นสังเกตเห็นได้ หรือกระสับกระส่าย อยู่นิ่งไม่ได้?",
                    answers = listOf("เป็นทุกวัน", "เป็นบ่อย แต่ไม่ทุกวัน", "เป็นบางวัน", "ไม่เป็นเลย")),
            Question(text = "ช่วงสองสัปดาห์นี้คุณ...คิดทำร้ายตัวเอง หรือคิดว่าถ้าตายไปคงจะดี?",
                    answers = listOf("เป็นทุกวัน", "เป็นบ่อย แต่ไม่ทุกวัน", "เป็นบางวัน", "ไม่เป็นเลย"))
    )

    private lateinit var viewModel: GameViewModel
    var score: Int = 0
    var text: String = ""
    lateinit var currentQuestion: Question
    lateinit var answers: MutableList<String>
    private var questionIndex = 0
    private val numQuestions = 9

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentGameBinding>(
                inflater, R.layout.fragment_game, container, false)

        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)
        // Shuffles the questions and sets the question index to the first question.

        randomizeQuestions()

        // Bind this fragment class to the layout
        binding.game = this

        // Set the onClickListener for the submitButton
        binding.submitButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            val checkedId : Int = binding.questionRadioGroup.checkedRadioButtonId
            // Do nothing if nothing is checked (id == -1)
            if (-1 != checkedId) {
                var answerIndex = 0
                when (checkedId) {
                    R.id.secondAnswerRadioButton -> answerIndex = 1
                    R.id.thirdAnswerRadioButton -> answerIndex = 2
                    R.id.fourthAnswerRadioButton -> answerIndex = 3
                }

                // The first answer in the original question is always the correct one, so if our
                // answer matches, we have the correct answer.
                when(answers[answerIndex]) {
                    "เป็นทุกวัน" -> score = score + 3
                    "เป็นบ่อย แต่ไม่ทุกวัน" -> score = score + 2
                    "เป็นบางวัน" -> score = score + 1
                    "ไม่เป็นเลย" -> score = score + 0
                }
                Log.d("testingxd","numb : "+ score)
                questionIndex++
                if (questionIndex < numQuestions) {
                    currentQuestion = questions[questionIndex]
                    setQuestion()
                    binding.invalidateAll()
                } else {
                    // We've won!  Navigate to the gameWonFragment.
                    calculate()
                    view.findNavController()
                            .navigate(GameFragmentDirections
                                    .actionGameFragmentToGameWonFragment(score,text))
                }
            }
        }
        return binding.root
    }

    fun calculate(){
        if(score >= 20)
            text = "ควรพบจิตแพทย์อย่างเร่งด่วน"
        if(score > 10 && score <= 19)
            text = "ลองปรึกษาแพทย์ดูก็ไม่เสียหาย"
        if(score <= 10)
            text = "ท่านมีสุขภาพจิตที่ดี"
    }

    // randomize the questions and set the first question
    private fun randomizeQuestions() {
        questions.shuffle()
        questionIndex = 0
        setQuestion()
    }

    // Sets the question and randomizes the answers.  This only changes the data, not the UI.
    // Calling invalidateAll on the FragmentGameBinding updates the data.
    private fun setQuestion() {
        currentQuestion = questions[questionIndex]
        // randomize the answers into a copy of the array
        answers = currentQuestion.answers.toMutableList()
        // and shuffle them
        //answers.shuffle()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_android_trivia_question, questionIndex + 1, numQuestions)
    }
}
