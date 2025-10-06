plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlinx.serialization)
}

android {
  namespace = "io.middlepoint.mcponandroid"
  compileSdk = 36

  defaultConfig {
    applicationId = "io.middlepoint.mcponandroid"
    minSdk = 26
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }

  packaging {
    resources {
      excludes += "META-INF/*"
    }

    jniLibs {
      useLegacyPackaging = true
    }
  }
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.tv.foundation)
  implementation(libs.androidx.tv.material)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.preference.ktx)
  implementation(libs.kermit)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.smoothmotion)

  implementation(libs.mcp)
  implementation(libs.mcp.kotlin.server)
  implementation(libs.ktor.server.cio)
  implementation(libs.ktor.server.cors)
  implementation(libs.ktor.server.core)
  implementation(libs.ktor.server.sse)
  implementation(libs.ktor.client.content.negotiation)
  implementation(libs.ktor.serialization.kotlinx.json)

  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}