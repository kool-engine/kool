import java.io.FileInputStream
import java.io.IOException
import java.util.*

class PublishingCredentials(propsPath: String) {

    val repoUrl: String
    val username: String
    val password: String

    val isAvailable: Boolean
        get() = repoUrl.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()

    init {
        var url = ""
        var user = ""
        var passwd = ""

        try {
            FileInputStream(propsPath).use {
                val props = Properties()
                props.load(it)
                url = props.getProperty("publishRepoUrl") ?: ""
                user = props.getProperty("publishUser") ?: ""
                passwd = props.getProperty("publishPassword") ?: ""
            }
        } catch (e: IOException) {
            // silently ignored, fall back to empty default values
        }

        this.repoUrl = url
        this.username = user
        this.password = passwd
    }

}