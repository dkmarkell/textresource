plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "io.github.dkmarkell.textresource"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("proguard-rules.pro") // create this file at core/proguard-rules.pro
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { buildConfig = false }

    testBuildType = "debug"
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}
kotlin {
    jvmToolchain(17)
    explicitApi()
}

publishing {
    publications {
        register<MavenPublication>("release") {
            // afterEvaluate avoids timing issues with AGP component creation
            afterEvaluate { from(components["release"]) }
            artifactId = "textresource-core"
            // group + version come from root via gradle.properties
            pom {
                name.set("TextResource Core")
                description.set("Core module for TextResource (no Compose).")
            }
        }
    }
}

dependencies {
    implementation(libs.annotation.jvm)
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
}
