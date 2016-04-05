package commons

import com.typesafe.config.{Config, ConfigValueFactory}

object ConfigObjectExtensions {
  implicit class RichConfig(val config: Config) extends AnyVal {
    def withPair(key: String, value: AnyRef) = config.withValue(key, ConfigValueFactory.fromAnyRef(value))
  }
}
