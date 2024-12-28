package com.xvwilliam.simpledictv2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val queryInput = findViewById<EditText>(R.id.queryInput)
        val queryButton = findViewById<Button>(R.id.queryButton)
        val resultText = findViewById<TextView>(R.id.resultText)
        val queryTypeRadioGroup = findViewById<RadioGroup>(R.id.queryTypeRadioGroup)
        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        val dbHelper = EcdictDatabaseHelper(this)

        queryButton.setOnClickListener {
            val query = queryInput.text.toString().trim()

            if (TextUtils.isEmpty(query)) {
                resultText.setText(R.string.prompt_enter_query)
                return@setOnClickListener
            }

            val selectedRadioButtonId = queryTypeRadioGroup.checkedRadioButtonId
            val queryTypeRadioButton: RadioButton = findViewById(selectedRadioButtonId)
            val queryType = queryTypeRadioButton.text.toString()

            if (queryType == "Word") {
                val results = dbHelper.getWordData(query)
                if (results.isNotEmpty()) {
                    val result = results.joinToString("\n\n") {
                        getString(R.string.word_result_format, it.word, it.phonetic, it.definition, it.translation)
                    }
                    resultText.text = if (result.length > 500) result.substring(0, 500) + "..." else result
                } else {
                    resultText.setText(R.string.no_results_found)
                }
            } else {
                val results = dbHelper.getTranslationData(query)
                resultText.text = if (results.isNotEmpty()) results.joinToString("\n") else getString(R.string.no_results_found)
            }
        }

        // Set up the menu
        bottomAppBar.replaceMenu(R.menu.bottom_menu)
        Log.d("MainActivity", "Menu set up successfully")

        // Handle menu item clicks
        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            Log.d("MainActivity", "Menu item clicked: ${menuItem.title}")
            when (menuItem.itemId) {
                R.id.menu_about -> {
                    Log.d("MainActivity", "About clicked")
                    openAbout()
                    true
                }
                R.id.menu_update_history -> {
                    Log.d("MainActivity", "Update History clicked")
                    openUpdateHistory()
                    true
                }
                else -> false
            }
        }

        // Handle FAB click to show the menu
        fab.setOnClickListener {
            Log.d("MainActivity", "FAB clicked")
            bottomAppBar.performShow()
        }
    }

    private fun openAbout() {
        val intent = Intent(this, MarkdownViewerActivity::class.java).apply {
            putExtra("file", "about.md")
        }
        startActivity(intent)
    }

    private fun openUpdateHistory() {
        val intent = Intent(this, MarkdownViewerActivity::class.java).apply {
            putExtra("file", "updatehistory.md")
        }
        startActivity(intent)
    }
}