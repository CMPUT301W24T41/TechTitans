plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.eventsigninapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.eventsigninapp"
        minSdk = 27
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


}

dependencies {
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    val fragment_version = "1.6.2"

    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")
    implementation("androidx.fragment:fragment:$fragment_version")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation ("com.github.yalantis:ucrop:2.2.8")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.squareup.picasso:picasso:2.8")
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation("org.robolectric:robolectric:4.8.1")

}

//secrets {
//    propertiesFileName = "secrets.properties"
//    defaultPropertiesFileName = "local.defaults.properties"
//    ignoreList.add("sdk.*")
//}
