import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application) apply false
    id("com.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "mx.utng.smarthealthmonitor_shared_ccdm"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        buildConfig = true
    }
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android.defaultConfig {
    buildConfigField("String", "MQTT_BROKER", "\"${localProperties.getProperty("MQTT_BROKER") ?: ""}\"")
    buildConfigField("String", "MQTT_USERNAME", "\"${localProperties.getProperty("MQTT_USERNAME") ?: ""}\"")
    buildConfigField("String", "MQTT_PASSWORD", "\"${localProperties.getProperty("MQTT_PASSWORD") ?: ""}\"")
}
dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    // Room
    val roomVersion = "2.8.4"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    
    // MQTT & Serialization
    api(libs.paho.mqtt)
    api(libs.paho.mqtt.service)
    api(libs.kotlinx.serialization.json)
}