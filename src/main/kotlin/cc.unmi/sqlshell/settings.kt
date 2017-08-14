package cc.unmi.sqlshell

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
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

    val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configFile)

    val drivers = dom.selectNodes("//drivers/driver").map { node ->
        Driver(node.selectText("@name"), node.textContent)
    }.toList()

    val databases = dom.selectNodes("//databases/database").map { node ->
        Database(node.selectText("@name"),
                node.selectText("driver"),
                node.selectText("url"),
                node.selectText("user"),
                node.selectText("password"))
    }.toList()

    return Settings(drivers, databases)
}
