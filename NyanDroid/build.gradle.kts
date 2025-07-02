plugins {
    id("com.android.application")
    kotlin("android")
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

android {
    compileSdk = 35
    defaultConfig {
        targetSdk = 35
        minSdk = 30
        versionCode = 17
        applicationId = "com.powerje.nyan"
        versionName = "2.0.6"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    namespace = "com.powerje.nyan"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildToolsVersion = "35.0.0"
}

dependencies {
    implementation( "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.23")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference:1.2.1")
}
