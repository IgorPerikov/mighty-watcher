object ResourceFilesUtils {
    fun readResourceFile(name: String): String = ResourceFilesUtils::class.java.classLoader.getResource(name).readText()
}
