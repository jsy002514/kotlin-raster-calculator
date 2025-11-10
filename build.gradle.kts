plugins {
    kotlin("jvm") version "2.2.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.geotools:gt-main:28.2")
    implementation("org.geotools:gt-coverage:28.2")
    implementation("org.geotools:gt-geotiff:28.2")

    testImplementation(kotlin("org.junit.jupiter:junit-jupiter-api:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    testImplementation("org.assertj:assertj-core:3.25.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
