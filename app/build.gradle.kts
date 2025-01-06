import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.xvwilliam.simpledictv2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.xvwilliam.simpledictv2"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

// 添加重命名任务
tasks.register("renameReleaseApk") {
    group = "custom"
    description = "Rename release APK with date"

    doLast {
        // 修正路径，去掉重复的 app 目录
        val releaseDir = project.projectDir.resolve("release")
        val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val originalFile = releaseDir.resolve("app-release.apk")
        val newFile = releaseDir.resolve("simpleDictV2_${date}.apk")

        println("Looking for APK at: ${originalFile.absolutePath}")

        if (originalFile.exists()) {
            // 先复制文件，而不是直接重命名，以避免可能的文件锁定问题
            originalFile.copyTo(newFile, overwrite = true)
            println("Successfully created APK: ${newFile.name}")

            // 同时更新 output-metadata.json
            val metadataFile = releaseDir.resolve("output-metadata.json")
            if (metadataFile.exists()) {
                val metadata = metadataFile.readText()
                val updatedMetadata = metadata.replace(
                    "\"outputFile\": \"app-release.apk\"",
                    "\"outputFile\": \"${newFile.name}\""
                )
                metadataFile.writeText(updatedMetadata)
                println("Updated output-metadata.json")
            }
        } else {
            println("Original APK not found at: ${originalFile.absolutePath}")
            println("Please make sure you have generated the signed APK first")
            println("Current directory: ${project.projectDir.absolutePath}")
        }
    }
}

// 设置任务依赖
afterEvaluate {
    tasks.named("assembleRelease") {
        finalizedBy("renameReleaseApk")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.core)
    implementation("io.noties.markwon:core:4.6.2") {
        exclude(group = "com.atlassian.commonmark", module = "commonmark")
    }
}

