plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
    signing
    id("com.gradleup.nmcp")
}

android {
    namespace = "io.github.dkmarkell.textresource"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("proguard-rules.pro") // create this file at core/proguard-rules.pro
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

    afterEvaluate {
        publishing {
            publications {
                val releasePub = (findByName("release") as? org.gradle.api.publish.maven.MavenPublication)
                    ?: create("release", org.gradle.api.publish.maven.MavenPublication::class.java) {
                        from(components["release"])
                    }

                releasePub.groupId = (findProperty("GROUP") as String?) ?: project.group.toString()
                releasePub.artifactId = "textresource-core"
                releasePub.version = (findProperty("VERSION_NAME") as String?) ?: project.version.toString()

                releasePub.pom {
                    name.set("TextResource Core")
                    description.set(
                        "Core APIs to represent text independently of Android Context and resolve it at display time. Keeps ViewModel/domain layers Context-free."
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

        val key  = System.getenv("SIGNING_KEY")
        val pass = System.getenv("SIGNING_KEY_PASSWORD") ?: ""
        val hasKey = !key.isNullOrBlank()
        val isCi   = System.getenv("CI") == "true"

        signing {
            isRequired = isCi && hasKey

            if (hasKey) {
                useInMemoryPgpKeys(key, pass)
                sign(publishing.publications)   // runs only when a key is present
            }
        }

        // Skip sign tasks entirely when there's no key
        tasks.withType(Sign::class.java).configureEach {
            onlyIf { hasKey }
        }
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
}
kotlin {
    jvmToolchain(17)
    explicitApi()
}

dependencies {
    implementation(libs.annotation.jvm)
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(kotlin("test"))
}
