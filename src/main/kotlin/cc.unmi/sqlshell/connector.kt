import cc.unmi.sqlshell.Database
import com.bethecoder.ascii_table.ASCIITable
import com.bethecoder.ascii_table.ASCIITableHeader
import java.sql.*

fun createConnection(database: Database): Connection {
    return DriverManager.getConnection(database.url, database.user, database.password)
}

fun showDatabaseMeta(connection: Connection) {
    val metaData = connection.metaData
    println("Connected to ${metaData.databaseProductName}, ${metaData.databaseProductVersion}")
}

fun getTableHeader(rsMetaData: ResultSetMetaData): List<ASCIITableHeader> {
    val columnCount = rsMetaData.columnCount
    return (1..columnCount).map { index ->
        makeHeader(rsMetaData.getColumnLabel(index), rsMetaData.getColumnType(index)) }.toList()
}

fun getRecords(resultSet: ResultSet): Array<Array<String>> {
    val columnCount = resultSet.metaData.columnCount

    val rows = mutableListOf<Array<String>>()

    while (resultSet.next()) {
        val columns = (1..columnCount).map { index -> resultSet.getString(index) ?: "NULL" }.toTypedArray()
        rows.add(columns)
    }

    return rows.toTypedArray()
}

private fun makeHeader(label: String, type: Int): ASCIITableHeader {
    val isNumeric = arrayOf(Types.BIT, Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT, Types.FLOAT,
            Types.REAL, Types.DOUBLE, Types.NUMERIC).contains(type)

    return ASCIITableHeader(label, if(isNumeric) ASCIITable.ALIGN_RIGHT else ASCIITable.ALIGN_LEFT)
}