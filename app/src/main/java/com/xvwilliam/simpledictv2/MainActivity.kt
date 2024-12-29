package com.xvwilliam.simpledictv2

import android.database.Cursor
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: EcdictDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = EcdictDatabaseHelper(this)

        val queryInput = findViewById<EditText>(R.id.queryInput)
        val queryButton = findViewById<Button>(R.id.queryButton)
        val resultText = findViewById<TextView>(R.id.resultText)
        val queryTypeRadioGroup = findViewById<RadioGroup>(R.id.queryTypeRadioGroup)

        queryButton.setOnClickListener {
            val query = queryInput.text.toString().trim()

            if (TextUtils.isEmpty(query)) {
                resultText.text = "请输入查询内容"
                return@setOnClickListener
            }

            val selectedRadioButtonId = queryTypeRadioGroup.checkedRadioButtonId
            val queryTypeRadioButton: RadioButton = findViewById(selectedRadioButtonId)
            val queryType = queryTypeRadioButton.text.toString()

            if (queryType == "Word") {
                val results = dbHelper.getWordData(query)
                if (results.isNotEmpty()) {
                    val result = results.joinToString("\n\n") {
                        "Word: ${it.word}\nPhonetic: ${it.phonetic}\nDefinition: ${it.definition}\nTranslation: ${it.translation}"
                    }
                    resultText.text = if (result.length > 500) result.substring(0, 500) + "..." else result
                } else {
                    resultText.text = "没有找到结果"
                }
            } else {
                val results = dbHelper.getTranslationData(query)
                if (results.isNotEmpty()) {
                    val result = results.joinToString("\n") { it }
                    resultText.text = if (result.length > 500) result.substring(0, 500) + "..." else result
                } else {
                    resultText.text = "没有找到结果"
                }
            }
        }
    }
}