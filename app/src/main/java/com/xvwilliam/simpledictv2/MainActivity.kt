package com.xvwilliam.simpledictv2

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

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
        val menuFab = findViewById<FloatingActionButton>(R.id.menuFab)
        val copyButton = findViewById<Button>(R.id.copyButton)

        // 设置底部应用栏
        setSupportActionBar(bottomAppBar)

        // 设置菜单FAB点击事件
        menuFab.setOnClickListener { view ->
            val popup = PopupMenu(this, view, Gravity.TOP)
            popup.menuInflater.inflate(R.menu.bottom_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_about -> {
                        openMarkdownFile("about.md")
                        true
                    }
                    R.id.menu_update_history -> {
                        openMarkdownFile("updatehistory.md")
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        // 搜索按钮点击事件
        wordSearchButton.setOnClickListener {
            performSearch("Word")
        }

        translationSearchButton.setOnClickListener {
            performSearch("Translation")
        }

        // 复制按钮点击事件
        copyButton.setOnClickListener {
            val text = resultText.text.toString()
            if (text.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("查询结果", text)
                clipboard.setPrimaryClip(clip)
                Snackbar.make(it, "已复制到剪贴板", Snackbar.LENGTH_SHORT).show()
            }
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

    private fun openMarkdownFile(fileName: String) {
        val intent = Intent(this, MarkdownViewerActivity::class.java).apply {
            putExtra("file", fileName)
        }
        startActivity(intent)
    }
}