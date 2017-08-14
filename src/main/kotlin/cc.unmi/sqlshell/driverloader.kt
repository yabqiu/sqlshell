import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.DriverPropertyInfo
import java.sql.SQLException
import java.util.*
import java.util.logging.Logger
import kotlin.streams.toList

fun registerDriver(driverClass: String) {

    val userDir = System.getProperty("user.dir") + "/drivers"
    val urls= Files.list(Paths.get(userDir)).map { t -> t.toUri().toURL()!! }.toList().toTypedArray()

    @Suppress("UNCHECKED_CAST")
    val clazz = URLClassLoader(urls).loadClass(driverClass) as Class<Driver>?

    DriverManager.registerDriver(DriverShim(clazz!!.newInstance()))
}

internal class DriverShim(private val driver: Driver) : Driver {
    override fun getParentLogger(): Logger {
        TODO("not implemented")
    }

    @Throws(SQLException::class)
    override fun acceptsURL(u: String): Boolean {
        return this.driver.acceptsURL(u)
    }

    @Throws(SQLException::class)
    override fun connect(u: String, p: Properties): Connection {
        return this.driver.connect(u, p)
    }

    override fun getMajorVersion(): Int {
        return this.driver.majorVersion
    }

    override fun getMinorVersion(): Int {
        return this.driver.minorVersion
    }

    @Throws(SQLException::class)
    override fun getPropertyInfo(u: String, p: Properties): Array<DriverPropertyInfo> {
        return this.driver.getPropertyInfo(u, p)
    }

    override fun jdbcCompliant(): Boolean {
        return this.driver.jdbcCompliant()
    }
}


