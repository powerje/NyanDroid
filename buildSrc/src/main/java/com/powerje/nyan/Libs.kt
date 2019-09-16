object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:3.5.0"

    object AndroidX {
        private const val version = "1.1.0"
        const val appCompat = "androidx.appcompat:appcompat:$version"
        const val preference = "androidx.preference:preference:$version"
    }

    object Kotlin {
        private const val version = "1.3.50"
        const val standardLibrary = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
    }

}
