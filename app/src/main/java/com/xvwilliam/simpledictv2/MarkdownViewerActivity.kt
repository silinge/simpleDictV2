package com.xvwilliam.simpledictv2

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class MarkdownViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 创建WebView
        val webView = WebView(this)
        setContentView(webView)

        // 获取文件名
        val fileName = intent.getStringExtra("file") ?: return

        try {
            // 读取Markdown文件
            val inputStream = assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val markdownContent = reader.useLines { lines ->
                lines.joinToString("\n")
            }

            // 创建HTML内容
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/github-markdown-css/5.1.0/github-markdown.min.css">
                    <style>
                        body {
                            margin: 0;
                            padding: 16px;
                            background: #fff;
                        }
                        .markdown-body {
                            box-sizing: border-box;
                            min-width: 200px;
                            max-width: 980px;
                            margin: 0 auto;
                        }
                    </style>
                </head>
                <body>
                    <div class="markdown-body">
                        $markdownContent
                    </div>
                </body>
                </html>
            """.trimIndent()

            // 加载HTML内容
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

            // 启用JavaScript（如果需要）
            webView.settings.javaScriptEnabled = true

            // 设置WebView可以缩放
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = false // 隐藏缩放控件

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}