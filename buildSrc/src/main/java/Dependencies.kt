object ApplicationId {
    val id = "com.developers.rxanime"
}

object Releases {
    val versionCode = 1
    val versionName = "1.0"
}

object Versions {
    val compileSdk = 29
    val minSdk = 19
    val targetSdk = 29

    val kotlin = "1.3.61"
    val kotlinPoet = "1.4.3"

    val appcompat = "1.1.0"
    val cardview = "1.0.0"
    val preference = "1.1.0"

    val constraintLayout = "1.1.3"
    // Rx
    val rxJava = "2.2.10"
    val rxBinding = "2.1.1"
    val rxBindingSupport = "2.1.1"
    val rxkotlin = "2.4.0"
    val rxPreferences = "2.0.0"

    val rxAndroid = "2.1.1"
    val lifeCycle = "1.1.1"

    val lifeCycleKtx = "2.2.0-rc03"
    val coroutines = "1.3.1"
    val moshi = "1.8.0"

    val junit = "4.12"
    val androidTestRunner = "1.2.0"
    val espressoCore = "3.2.0"
}

object Libraries {
    // Kotlin
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    val kotlinPoet = "com.squareup:kotlinpoet:${Versions.kotlinPoet}"

    // Rx
    val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    val rxBinding = "com.jakewharton.rxbinding2:rxbinding-kotlin:${Versions.rxBinding}"
    val rxBindingSupport = "com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${Versions.rxBindingSupport}"
    val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}"
    val rxPreferences = "com.f2prateek.rx.preferences2:rx-preferences:${Versions.rxPreferences}"

    val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    // Lifecycle
    val lifeCycleExtensions = "android.arch.lifecycle:extensions:${Versions.lifeCycle}"
    val lifeCycleViewModel = "android.arch.lifecycle:viewmodel:${Versions.lifeCycle}"

    val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifeCycleKtx}"
    // Coroutines
    val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"

    val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    // Moshi
    val moshiCodeGen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"

    val moshiCore = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
}

object SupportLibraries {
    val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    val cardView = "androidx.cardview:cardview:${Versions.cardview}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    val preferences = "androidx.preference:preference-ktx:${Versions.preference}"
}

object TestLibraries {
    val junit = "junit:junit:${Versions.junit}"
    val androidTestRunner = "androidx.test:runner:${Versions.androidTestRunner}"
    val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
}
