package cc.unmi.sqlshell

import com.bethecoder.ascii_table.ASCIITable
import com.bethecoder.ascii_table.ASCIITableHeader
import getRecords
import getTableHeader
import java.sql.Connection
import java.sql.ResultSet

fun parseHandler(sql: String, conn: Connection): CommandHandler {
    val match = sql.trim().toUpperCase()
    return when {
        match.startsWith("SELECT") -> SelectHandler(sql, conn)
        match.startsWith("UPDATE")
          || match.startsWith("DELETE")
          || match.startsWith("INSERT")-> UpdateHandler(sql, conn)
        else -> SelectHandler(sql, conn)
    }
}

open class CommandHandler(val sql: String, val conn: Connection) {
    open fun execute(): Any?{
        return null
    }
    open fun postExecute(result: Any?){}
}

class SelectHandler(sql: String, conn: Connection): CommandHandler(sql, conn) {
    override fun postExecute(result: Any?) {
        result?.let {
            val resultSet = it as ResultSet
            val tableHeader = getTableHeader(resultSet.metaData).toTypedArray()
            val records = getRecords(resultSet)

            showAsciiTable(tableHeader, records)
        }
    }

    override fun execute(): Any? {
        val statement = conn.createStatement()

        return try {
            statement.executeQuery(sql)
        } catch (e: Exception) {
            println("Error: " + e.localizedMessage)
            null
        }
    }

    fun showAsciiTable(header: Array<ASCIITableHeader>, dataRows: Array<Array<String>>) {
        ASCIITable.getInstance().printTable(header, dataRows)
    }

}

class UpdateHandler(sql: String, conn: Connection): CommandHandler(sql, conn) {
    override fun execute(): Any? {
        val statement = conn.createStatement()
        return statement.executeUpdate(sql)
    }

    override fun postExecute(result: Any?) {
        result?.let {
            println("Records effected: ${result}")
        }
    }
}
