package com.xvwilliam.simpledictv2

//import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import java.io.IOException

class MarkdownViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_viewer)

        val markdownView = findViewById<TextView>(R.id.markdownView)
        val fileName = intent.getStringExtra("file")

        if (fileName != null) {
            val markdownText = readMarkdownFile(fileName)
            markdownView.text = markdownText
        }
    }

    private fun readMarkdownFile(fileName: String): String {
        return try {
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer)
        } catch (e: IOException) {
            "无法加载文件: $fileName"
        }
    }
}