plugins {
    kotlin("jvm")
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java-library")

    dependencies {
        implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}
