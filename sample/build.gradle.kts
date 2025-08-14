plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.github.dkmarkell.textresource.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.dkmarkell.textresource.sample"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "0.1"
    }

    buildFeatures { compose = true }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin { jvmToolchain(17) }

dependencies {
    implementation(project(":compose")) // pulls in :core
    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.compose.m3)
    implementation(libs.material3)
    implementation(libs.compose.ui.tooling.preview) // needed at compile time for @Preview
    debugImplementation(libs.compose.ui.tooling)    // editor preview tooling in debug only
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
}
