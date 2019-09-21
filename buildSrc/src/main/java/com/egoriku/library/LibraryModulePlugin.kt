package com.egoriku.library

import Libs
import ProjectVersion
import com.android.build.gradle.BaseExtension
import com.egoriku.ext.implementation
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

open class LibraryModulePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            configurePlugins()
            configureAndroidSection()
            configureDependencies()
        }
    }
}

fun Project.configurePlugins() {
    with(plugins) {
        apply("com.android.library")
        apply("kotlin-android")
    }
}

fun Project.configureAndroidSection() = extensions.getByType<BaseExtension>().run {
    defaultConfig {
        minSdkVersion(ProjectVersion.minSdkVersion)
        compileSdkVersion(ProjectVersion.compileSdkVersion)
        versionCode = 1
        versionName = "1.0"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

fun Project.configureDependencies() = dependencies {
    implementation(Libs.kotlin)
}