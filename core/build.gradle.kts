plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
    signing
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
            // We'll add a javadoc jar below (empty is OK if you’re not using Dokka yet)
        }
    }

    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("release") {
                    from(components["release"])
                    groupId = project.findProperty("GROUP") as String
                    artifactId = "textresource-core"
                    version = project.findProperty("VERSION_NAME") as String

                    // Empty javadoc JAR (acceptable for Central if you don’t use Dokka yet)
                    artifact(tasks.register("javadocJar", Jar::class) {
                        archiveClassifier.set("javadoc")
                    })

                    pom {
                        name.set("TextResource Core")
                        description.set("Core module for the TextResource library.")
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
}
