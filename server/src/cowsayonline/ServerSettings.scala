package cowsayonline

import com.typesafe.config.Config

final class ServerSettings(val config: Config) {

  object http {
    val interface: String = config.getString("http.interface")
    val port: Int = config.getInt("http.port")
  }

  object database {
    val jdbcUrl: String = config.getString("database.jdbcUrl")
  }
}
