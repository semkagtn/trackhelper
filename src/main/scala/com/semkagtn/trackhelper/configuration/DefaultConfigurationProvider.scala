package com.semkagtn.trackhelper.configuration

import java.io.File

import com.semkagtn.trackhelper.Constants
import com.typesafe.config.ConfigFactory

/**
  * Provides default configuration from
  *
  * @author semkagtn
  */
class DefaultConfigurationProvider
  extends ConfigurationProvider {

  override def get(): Configuration =
    Configuration.fromTypeSafeConfig(ConfigFactory.parseFile(new File(Constants.ConfigFile)))
}
