plugins {
    id("com.android.application")
}

android {
    namespace = "io.github.vvb2060.ims"
    defaultConfig {
        applicationId = "io.github.turboims.pixel"
        versionCode = project.findProperty("ciVersionCode")?.toString()?.toInt() ?: 6
        versionName = project.findProperty("ciVersionName")?.toString() ?: "3.1"
    }
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            proguardFiles("proguard-rules.pro")
            if (System.getenv("KEYSTORE_FILE") != null) {
                signingConfig = signingConfigs["release"]
            } else {
                signingConfig = signingConfigs["debug"]
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "**"
        }
    }
    lint {
        checkReleaseBuilds = false
    }
    dependenciesInfo {
        includeInApk = false
    }
}

dependencies {
    compileOnly(project(":stub"))
    implementation(libs.shizuku.provider)
    implementation(libs.shizuku.api)
    implementation(libs.hiddenapibypass)
    implementation(libs.material)
}
