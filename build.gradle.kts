plugins {
    kotlin("jvm") version "1.9.25"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    // 1. JAR 제공 저장소 (OSGeo) – 반드시 앞에!
    maven { url = uri("https://repo.osgeo.org/repository/geotools/") }
    maven { url = uri("https://repo.osgeo.org/repository/release/") }
    maven { url = uri("https://download.osgeo.org/webdav/geotools/") }

    // 2. POM 제공 저장소
    mavenCentral()

    // 3. JAI-EXT, ImageIO-Ext
    maven { url = uri("https://maven.geo-solutions.it/") }
}

dependencies {
    // GeoTools
    implementation("org.geotools:gt-main:30.1")
    implementation("org.geotools:gt-geotiff:30.1")
    implementation("org.geotools:gt-referencing:30.1")
    implementation("org.geotools:gt-epsg-hsql:30.1")
    implementation("org.geotools:gt-jp2k:30.1")
    implementation("org.geotools:gt-opengis:27.4.01")

    // gt-coverage – 자동 jai_core 차단
    implementation("org.geotools:gt-coverage:30.1") {
        exclude(group = "javax.media", module = "jai_core")
    }

    // jai_core – JAR은 OSGeo, POM은 Maven Central
    implementation("javax.media:jai_core:1.1.3")

    // (선택) jai_imageio
    implementation("javax.media:jai_imageio:1.1")

    // JAI-EXT
    implementation("it.geosolutions.jaiext.affine:jt-affine:1.1.29")
    implementation("it.geosolutions.jaiext.algebra:jt-algebra:1.1.29")

    // ImageIO-Ext
    implementation("it.geosolutions.imageio-ext:imageio-ext-tiff:1.4.9")
    implementation("com.github.jai-imageio:jai-imageio-core:1.4.0")

    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.12")

    // Test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.assertj:assertj-core:3.25.1")
}

application {
    mainClass.set("cli.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
