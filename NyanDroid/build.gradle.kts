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
    compileSdkVersion(Versions.Build.compileSdkVersion)

    defaultConfig {
        targetSdkVersion(Versions.Build.targetSdkVersion)
        minSdkVersion(Versions.Build.minSdkVersion)
        versionCode = 15
        applicationId = "com.powerje.nyan"
        versionName = "2.0.4"
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
