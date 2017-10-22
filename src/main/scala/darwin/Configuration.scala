package darwin

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by gael on 22/10/17.
  */
class Configuration(wrapped: Config) {
  import darwin.Configuration._

  val enabled: Boolean = wrapped.getBoolean("darwin.enabled")

  object db {
    val default = new DbConfig(wrapped, "default")

    def named(name: String) = new DbConfig(wrapped, name)
  }

}

object Configuration {
  def get: Configuration = new Configuration(ConfigFactory.load)

  class DbConfig(wrapped: Config, name: String) {
    val driver: String = wrapped.getString(s"darwin.db.$name.driver")
    val url: String = wrapped.getString(s"darwin.db.$name.url")
    val user: String = wrapped.getString(s"darwin.db.$name.username")
    val password: String = wrapped.getString(s"darwin.db.$name.password")
  }

}