buildscript {

    extra["java_version"] = JavaVersion.VERSION_17

    extra["minSdkVersion"] = 21
    extra["compileSdkVersion"] = 34
    extra["targetSdkVersion"] = 34

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("io.deepmedia.tools:publisher:0.6.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.0")

    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(buildDir)
}