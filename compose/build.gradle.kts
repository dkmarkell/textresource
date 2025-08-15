plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
    signing
}

android {
    namespace = "io.github.dkmarkell.textresource.compose"
    compileSdk = 36

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("proguard-rules.pro") // create at compose/proguard-rules.pro
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
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

kotlin {
    jvmToolchain(17)
    explicitApi()
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = project.findProperty("GROUP") as String
                artifactId = "textresource-compose"
                version = project.findProperty("VERSION_NAME") as String

                artifact(tasks.register("javadocJar", Jar::class) {
                    archiveClassifier.set("javadoc")
                })

                pom {
                    name.set("TextResource Compose")
                    description.set("Jetpack Compose integration for the TextResource library.")
                    url.set("https://github.com/dkmarkell/textresource")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/dkmarkell/textresource/blob/main/LICENSE")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("dkmarkell")
                            name.set("Derek Markell")
                            url.set("https://github.com/dkmarkell")
                        }
                    }
                    scm {
                        url.set("https://github.com/dkmarkell/textresource")
                        connection.set("scm:git:https://github.com/dkmarkell/textresource.git")
                        developerConnection.set("scm:git:ssh://git@github.com/dkmarkell/textresource.git")
                    }
                }
            }
        }
    }

    signing {
        useInMemoryPgpKeys(
            System.getenv("SIGNING_KEY"),
            System.getenv("SIGNING_KEY_PASSWORD").takeUnless { it.isNullOrBlank() }
        )
        sign(publishing.publications)
    }
}

dependencies {
    api(project(":core"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.androidx.runner)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(platform(libs.compose.bom))
    debugImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(libs.compose.m3)
}
