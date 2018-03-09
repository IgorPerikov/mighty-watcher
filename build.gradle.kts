group = "com.github.igorperikov"
version = "0.0.1"

apply {
    plugin("java")
}

repositories {
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}