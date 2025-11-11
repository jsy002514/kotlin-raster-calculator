plugins {
    kotlin("jvm") version "2.2.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.osgeo.org/repository/release/")
    maven("https://repo.osgeo.org/repository/thirdparty/")
    maven("https://maven.geo-solutions.it/")
}

dependencies {
    // ✅ Core GeoTools modules
    implementation("org.geotools:gt-main:28.2")
    implementation("org.geotools:gt-coverage:28.2")
    implementation("org.geotools:gt-geotiff:28.2")
    implementation("org.geotools:gt-referencing:28.2")

    // ✅ Replacement for legacy JAI (modern maintained libs)
    implementation("it.geosolutions.jaiext.affine:jt-affine:1.1.24")
    implementation("it.geosolutions.jaiext.algebra:jt-algebra:1.1.24")
    implementation("it.geosolutions.jaiext.bandmerge:jt-bandmerge:1.1.24")
    implementation("it.geosolutions.imageio-ext:imageio-ext-tiff:1.4.7")
    implementation("com.github.jai-imageio:jai-imageio-core:1.4.0")

    // ✅ Tell Gradle to ignore broken JAI dependencies
    configurations.all {
        resolutionStrategy {
            force("com.github.jai-imageio:jai-imageio-core:1.4.0")
        }
        exclude(group = "javax.media", module = "jai_core")
        exclude(group = "com.sun.media", module = "jai_codec")
        exclude(group = "javax.media", module = "jai_imageio")
    }

    // ✅ JUnit5 + AssertJ
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
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
