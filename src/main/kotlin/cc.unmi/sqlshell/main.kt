@file:JvmName("Main")

package cc.unmi.sqlshell;

import createConnection
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import registerDriver
import showDatabaseMeta
import java.sql.Connection
import java.time.LocalDateTime
import kotlin.system.exitProcess


fun main(args: Array<String>) {

    val options = Options()
    options.addOption("l", "list", false, "List all configured databases")
    options.addOption("h", "help", false, "Show this help messages")
    val parser = DefaultParser()
    val cmd = parser.parse(options, args)
    if(cmd.hasOption("l")) {
        loadConfig().listDatabases()
    } else if (cmd.hasOption("h")) {
        HelpFormatter().printHelp("sqlshell", options)
    } else {

        val settings = loadConfig()

        val db = if(cmd.argList.isEmpty()) "mysql-local" else cmd.argList[0]

        val database = settings.databases.find { database -> database.name == db }

        database ?: run {
            println("Database ${db} is not configured")
            exitProcess(1)
        }

        val driverClass = settings.drivers.find { driver -> driver.name == database.driver }!!.driverClass

        registerDriver(driverClass)

        val connection = createConnection(database)

        showDatabaseMeta(connection)

        prompt(connection)
    }
}

fun prompt(connection: Connection) {
    val terminal = TerminalBuilder.builder().system(true).build()
    val reader = LineReaderBuilder.builder().terminal(terminal).build();
    reader.getTerminal().puts(InfoCmp.Capability.carriage_return);

    var isRunning = true

    while (isRunning) {
        val line = reader.readLine("sql> ", LocalDateTime.now().toString(), null, null)

        if (line == null || line.isBlank()) {
            continue;
        }

        if(line == "exit") {
            isRunning = false
        } else {
            val commandHandler = parseHandler(line, connection)
            commandHandler.postExecute(commandHandler.execute())
        }
    }
}