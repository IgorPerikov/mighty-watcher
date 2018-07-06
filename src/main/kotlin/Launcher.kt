import external.Client
import external.RestClient
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime

object Launcher {
    private val client: Client = RestClient()

    private val username: String by lazy {
        ResourceFilesUtils.readResourceFile("username")
    }

    private val recreateFile: (File) -> Unit = {
        it.delete()
        it.createNewFile()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println("start at  ${ZonedDateTime.now(ZoneId.of("Europe/Moscow"))}")

        println("importing repositories")
        val languages = readLines("languages")
            .map { it.toLowerCase() }
            .toSet()

        val ignoredRepos = readLines("ignored-repos")

        val file = File("src/main/resources/tracked-repos").also(recreateFile)

        client.getStarredRepositories(username)
            .filter { it.hasIssues }
            .filter { it.fullName !in ignoredRepos }
            .filter { it.language?.toLowerCase() in languages }
            .forEach { file.appendText(it.fullName + "\r\n") }

        val labelsSet = readLines("labels")
        val ignoredIssues = readLines("ignored-issues")

        val resultFile = File("src/main/resources/result").also(recreateFile)

        readLines("tracked-repos")
            .filter { it.isNotBlank() }
            .flatMap { repoFullName ->
                labelsSet.map { label ->
                    Pair(repoFullName, label)
                }
            }
            .flatMap { client.getIssues(it.first, it.second) }
            .filter { it.htmlUrl !in ignoredIssues }
            .distinctBy { it.htmlUrl }
            .sortedByDescending { it.createdAt }
            .forEach { resultFile.appendText(it.toString() + "\r\n") }
        println("finish at ${ZonedDateTime.now(ZoneId.of("Europe/Moscow"))}")
    }

    private fun readLines(name: String) = ResourceFilesUtils.readResourceFile(name)
        .lines()
        .filter { it.isNotBlank() }
        .toSet()
}
