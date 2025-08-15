plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
    signing
    id("com.gradleup.nmcp")
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
            withJavadocJar()
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
            val releasePub = (findByName("release") as? org.gradle.api.publish.maven.MavenPublication)
                ?: create("release", org.gradle.api.publish.maven.MavenPublication::class.java) {
                    from(components["release"])
                }

            releasePub.groupId = (findProperty("GROUP") as String?) ?: project.group.toString()
            releasePub.artifactId = "textresource-compose"
            releasePub.version = (findProperty("VERSION_NAME") as String?) ?: project.version.toString()

            releasePub.pom {
                name.set("TextResource Compose")
                description.set(
                    "Jetpack Compose extensions for TextResource: resolve strings in composition and remember definitions efficiently, pairing with the core module."
                )
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
                    tag.set("v${releasePub.version}")
                }
            }
        }
    }

    val isCi = (System.getenv("CI") == "true")
    val key  = System.getenv("SIGNING_KEY")
    val pass = System.getenv("SIGNING_KEY_PASSWORD").takeUnless { it.isNullOrBlank() }

    signing {
        isRequired = isCi
        if (!key.isNullOrBlank()) {
            useInMemoryPgpKeys(key, pass)
            sign(publishing.publications)
        }
    }
    tasks.withType(Sign::class.java).configureEach {
        onlyIf { !System.getenv("SIGNING_KEY").isNullOrBlank() }
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
