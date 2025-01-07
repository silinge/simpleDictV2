package com.xvwilliam.simpledictv2

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: EcdictDatabaseHelper
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var queryInput: EditText
    private lateinit var resultText: TextView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 启用边缘到边缘显示
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)

        // 初始化数据库
        dbHelper = EcdictDatabaseHelper(this)

        // 初始化视图
        initializeViews()

        // 设置底部应用栏
        setSupportActionBar(bottomAppBar)

        // 设置事件监听器
        setupClickListeners()

        // 设置系统UI和导航栏处理
        setupSystemUI()

        // 设置窗口插入监听
        setupWindowInsets()
    }

    private fun initializeViews() {
        queryInput = findViewById(R.id.queryInput)
        resultText = findViewById(R.id.resultText)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        fab = findViewById(R.id.menuFab)
    }

    private fun setupClickListeners() {
        // FAB菜单点击事件
        fab.setOnClickListener { view ->
            showPopupMenu(view)
        }

        // 搜索按钮点击事件
        findViewById<Button>(R.id.wordSearchButton).setOnClickListener {
            performSearch("Word")
        }

        findViewById<Button>(R.id.translationSearchButton).setOnClickListener {
            performSearch("Translation")
        }

        // 复制按钮点击事件
        findViewById<Button>(R.id.copyButton).setOnClickListener {
            copyResultToClipboard(it)
        }
    }

    private fun showPopupMenu(view: View) {
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

    private fun setupSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(WindowInsetsCompat.Type.navigationBars())
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // 更新BottomAppBar的padding
            bottomAppBar.setPadding(
                bottomAppBar.paddingLeft,
                bottomAppBar.paddingTop,
                bottomAppBar.paddingRight,
                0  // 移除底部padding以确保FAB与BottomAppBar对齐
            )

            // 更新内容区域的padding
            findViewById<View>(android.R.id.content).setPadding(
                0, 0, 0, insets.bottom
            )

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun copyResultToClipboard(view: View) {
        val text = resultText.text.toString()
        if (text.isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("查询结果", text)
            clipboard.setPrimaryClip(clip)
            Snackbar.make(view, "已复制到剪贴板", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun performSearch(queryType: String) {
        val query = queryInput.text.toString().trim()

        if (TextUtils.isEmpty(query)) {
            resultText.text = "请输入查询内容"
            return
        }

        if (queryType == "Word") {
            performWordSearch(query)
        } else {
            performTranslationSearch(query)
        }
    }

    private fun performWordSearch(query: String) {
        val results = dbHelper.getWordData(query)
        if (results.isNotEmpty()) {
            val result = results.joinToString("\n\n") {
                "Word: ${it.word}\nPhonetic: ${it.phonetic}\nDefinition: ${it.definition}\nTranslation: ${it.translation}"
            }
            resultText.text = if (result.length > 500) result.substring(0, 500) + "..." else result
        } else {
            resultText.text = "没有找到结果"
        }
    }

    private fun performTranslationSearch(query: String) {
        val results = dbHelper.getTranslationData(query)
        if (results.isNotEmpty()) {
            val result = results.joinToString("\n") { it }
            resultText.text = if (result.length > 500) result.substring(0, 500) + "..." else result
        } else {
            resultText.text = "没有找到结果"
        }
    }

    private fun openMarkdownFile(fileName: String) {
        val intent = Intent(this, MarkdownViewerActivity::class.java).apply {
            putExtra("file", fileName)
        }
        startActivity(intent)
    }
}