package darwin

import com.typesafe.config.{Config, ConfigObject}

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
  * Created by gael on 22/10/17.
  */
class Configuration(wrapped: Config) {

  import darwin.Configuration._

  val enabled: Boolean = wrapped.getBoolean("darwin.enabled")

  object db {
    val default = new DbConfig(wrapped, "default")
    val all: Map[String, DbConfig] = wrapped.getConfig("darwin.db").root().toMap map { case (key, content: ConfigObject) =>
      key -> new DbConfig(content.toConfig, key)
    }
    def named(name: String): DbConfig = all(name)
  }

}

object Configuration {

  class DbConfig(wrapped: Config, name: String) {
    val driver: String = wrapped.getString(s"darwin.db.$name.driver")
    val url: String = wrapped.getString(s"darwin.db.$name.url")
    val user: String = wrapped.getString(s"darwin.db.$name.username")
    val password: String = wrapped.getString(s"darwin.db.$name.password")
  }

}