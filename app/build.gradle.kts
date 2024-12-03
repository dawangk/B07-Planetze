plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.b07projectfall2024"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.b07projectfall2024"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets {
        getByName("main") {
            res {
                srcDirs("src\\main\\res", "src\\main\\res\\2",
                    "src\\main\\res",
                    "src\\main\\res\\layout\\questionnaire", "src\\main\\res", "src\\main\\res\\layout\\user-activity",
                    "src\\main\\res",
                    "src\\main\\res\\layout\\new-entry", "src\\main\\res", "src\\main\\res\\layout\\menu"
                )
            }
        }
    }
}

dependencies {

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.appcompat)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}
