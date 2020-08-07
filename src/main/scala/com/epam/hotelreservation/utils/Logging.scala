package com.epam.hotelreservation.utils

import org.apache.logging.log4j.{LogManager, Logger}

trait Logging {
  lazy val log: Logger = LogManager.getLogger(this.getClass)
}
