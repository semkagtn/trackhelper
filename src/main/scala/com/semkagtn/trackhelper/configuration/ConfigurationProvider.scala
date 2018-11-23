package com.semkagtn.trackhelper.configuration

/**
  * Provides configuration
  *
  * @author semkagtn
  */
trait ConfigurationProvider {

  def get(): Configuration
}
