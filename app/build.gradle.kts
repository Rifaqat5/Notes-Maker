plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.rifaqat.notesmaker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rifaqat.notesmaker"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures{
        dataBinding=true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.room:room-ktx:2.5.2")
    implementation("androidx.room:room-runtime:2.5.2")
    annotationProcessor("androidx.room:room-compiler:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")

    //    library for setting size and font for all types of screens.
    implementation("com.github.MrNouri:DynamicSizes:1.0")

    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")


    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // RecyclerView Animator
    implementation("jp.wasabeef:recyclerview-animators:4.0.2")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")


    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Annotation processor
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")


    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // Navigation Components
    implementation("androidx.fragment:fragment-ktx:1.7.0-alpha02")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    //material Components
    implementation("com.google.android.material:material:1.11.0-alpha01")

    //color picker library
    implementation("com.thebluealliance:spectrum:0.7.1")

    implementation("io.github.yahiaangelo.markdownedittext:markdownedittext:1.1.3")
    implementation("io.noties.markwon:core:4.6.1")
    implementation("io.noties.markwon:ext-strikethrough:4.6.1")
    implementation("io.noties.markwon:ext-tasklist:4.6.1")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
}