plugins {
    `java-library`
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.5")
    api("org.slf4j:slf4j-api:2.0.6")
}
