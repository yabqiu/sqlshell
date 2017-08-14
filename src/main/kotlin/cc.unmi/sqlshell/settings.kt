package cc.unmi.sqlshell

import org.dom4j.io.SAXReader
import java.io.File
import kotlin.system.exitProcess

data class Driver(val name: String, val driverClass: String)
data class Database(val name: String, val driver: String, val url: String, val user: String, val password: String)

data class Settings(val drivers: List<Driver>, val databases: List<Database>) {
    fun listDatabases() {
        println("Configured databases:")
        databases.forEach { t: Database? -> println(t?.name) }
    }
}

fun loadConfig(): Settings {
    val configFilePath = System.getProperty("user.home") + "/.sqlshell/config.xml"
    val configFile = File(configFilePath)

    if (!configFile.exists()) {
        println("Config file ${configFilePath} does not exist")
        exitProcess(1)
    }

    val dom = SAXReader().read(configFile)

    val drivers = XPath.selectNodes("//drivers/driver", dom).map { node ->
        Driver(XPath.selectText("@name", node), node.text)
    }.toList()

    val databases = XPath.selectNodes("//databases/database", dom).map { node ->
        Database(XPath.selectText("@name", node),
                XPath.selectText("driver", node),
                XPath.selectText("url", node),
                XPath.selectText("user", node),
                XPath.selectText("password", node))
    }.toList()

    return Settings(drivers, databases)
}
