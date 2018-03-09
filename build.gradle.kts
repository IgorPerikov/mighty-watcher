group = "com.github.igorperikov"
version = "0.0.1"

apply {
    plugin("java")
}

repositories {
    mavenCentral()
}

dependencies {

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}