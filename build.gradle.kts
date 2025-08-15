plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library)     apply false
    alias(libs.plugins.kotlin.android)      apply false

    id("com.gradleup.nmcp.aggregation") version "1.0.3"

    signing
}

nmcpAggregation {
    centralPortal {
        username = providers.environmentVariable("CENTRAL_USERNAME")
        password = providers.environmentVariable("CENTRAL_PASSWORD")

        publishingType = "AUTOMATIC"
    }
}

dependencies {
    nmcpAggregation(project(":core"))
    nmcpAggregation(project(":compose"))
}


allprojects {
    group = providers.gradleProperty("GROUP").get()
    version = providers.gradleProperty("VERSION_NAME").get()
}