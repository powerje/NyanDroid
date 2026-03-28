plugins {
    id("com.android.application")
}

// Added GitHub Actions CI workflow for automated builds and testing

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

android {
    compileSdk = 36
    defaultConfig {
        targetSdk = 36
        minSdk = 30
        versionCode = 19
        applicationId = "com.powerje.nyan"
        versionName = "2.1.0"
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
    buildToolsVersion = "36.0.0"
}

dependencies {
    implementation( "org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.3.20")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.preference:preference:1.2.1")
}
