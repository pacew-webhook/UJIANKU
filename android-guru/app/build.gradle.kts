plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.ujianku"
    compileSdk = 36
    defaultConfig {
        buildConfigField("String", "SUPABASE_URL", "\"https://aaupbsxavpidifmnanbj.supabase.co\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"${project.findProperty("SUPABASE_PUBLISHABLE_KEY") ?: ""}\"")

        applicationId = "com.example.ujianku"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0-A"
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: "https://aaupbsxavpidifmnanbj.supabase.co"
        val supabasePublishableKey = (project.findProperty("SUPABASE_PUBLISHABLE_KEY") as String?)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: "sb_publishable_QrLiHQFdpaN0f-D8kW-xqA_1egG73mA"
buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"$supabasePublishableKey\"")
    }

    buildFeatures { buildConfig = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.3")
    implementation(platform("io.github.jan-tennert.supabase:bom:3.5.0"))
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.ktor:ktor-client-android:3.0.3")
}
