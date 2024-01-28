plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.github.npcdw.store"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.npcdw.store"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("com.github.bingoogolapple.BGAQRCode-Android:zxing:1.3.8")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("id.zelory:compressor:3.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.minio:minio:8.5.7")
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.12.0")
}