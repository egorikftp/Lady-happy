import com.egoriku.versions.LibrariesVersion

object GradlePluginsVersion {
    const val firebaseCrashlytics = "2.4.1"
    const val firebasePerformance = "1.3.4"
    const val detekt = "1.15.0"
    const val googleServices = "4.3.4"
    const val gradleTools = "4.1.2"
    const val ossLicenses = "0.10.2"
    const val versionPlugin = "0.36.0"
}

object GradlePlugins {
    const val detektFormatting = "io.gitlab.arturbosch.detekt:detekt-formatting:${GradlePluginsVersion.detekt}"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-gradle:${GradlePluginsVersion.firebaseCrashlytics}"
    const val firebasePerformance = "com.google.firebase:perf-plugin:${GradlePluginsVersion.firebasePerformance}"
    const val googleOssLicenses = "com.google.android.gms:oss-licenses-plugin:${GradlePluginsVersion.ossLicenses}"
    const val googleServices = "com.google.gms:google-services:${GradlePluginsVersion.googleServices}"
    const val gradleTools = "com.android.tools.build:gradle:${GradlePluginsVersion.gradleTools}"
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${LibrariesVersion.kotlin}"
}