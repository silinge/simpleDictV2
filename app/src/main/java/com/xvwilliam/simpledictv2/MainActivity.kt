package com.xvwilliam.simpledictv2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: EcdictDatabaseHelper
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var queryInput: EditText
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = EcdictDatabaseHelper(this)

        // 初始化视图
        queryInput = findViewById(R.id.queryInput)
        resultText = findViewById(R.id.resultText)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        val wordSearchButton = findViewById<Button>(R.id.wordSearchButton)
        val translationSearchButton = findViewById<Button>(R.id.translationSearchButton)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        // 设置底部应用栏
        setSupportActionBar(bottomAppBar)

        // Word搜索按钮点击事件
        wordSearchButton.setOnClickListener {
            performSearch("Word")
        }

        // Translation搜索按钮点击事件
        translationSearchButton.setOnClickListener {
            performSearch("Translation")
        }

        // FAB点击事件
        fab.setOnClickListener {
            bottomAppBar.showOverflowMenu()
        }
    }

    private fun performSearch(queryType: String) {
        val query = queryInput.text.toString().trim()

        if (TextUtils.isEmpty(query)) {
            resultText.text = "请输入查询内容"
            return
        }

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_about -> {
                openMarkdownFile("about.md")
                true
            }
            R.id.menu_update_history -> {
                openMarkdownFile("updatehistory.md")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openMarkdownFile(fileName: String) {
        val intent = Intent(this, MarkdownViewerActivity::class.java).apply {
            putExtra("file", fileName)
        }
        startActivity(intent)
    }
}