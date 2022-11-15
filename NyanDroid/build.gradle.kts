plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

android {

    compileSdk = Versions.Build.compileSdkVersion
    defaultConfig {
        targetSdk = Versions.Build.targetSdkVersion
        minSdk = Versions.Build.minSdkVersion
        versionCode = 16
        applicationId = "com.powerje.nyan"
        versionName = "2.0.5"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    namespace = "com.powerje.nyan"
}

dependencies {
    implementation(Libs.Kotlin.standardLibrary)
    implementation(Libs.AndroidX.appCompat)
    implementation(Libs.AndroidX.preference)
}
