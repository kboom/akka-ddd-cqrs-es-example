package com.github.j5ik2o.bank.adaptor.util

import java.io.File

import scala.concurrent.duration._
import com.github.j5ik2o.scalatestplus.db._
import com.wix.mysql.distribution.Version._
import org.scalatest.TestSuite

trait FlywayWithMySQLSpecSupport extends FlywayWithMySQLdOneInstancePerSuite with RandomPortSupport {
  this: TestSuite =>

  override protected lazy val mySQLdConfig: MySQLdConfig = MySQLdConfig(
    version = v5_6_21,
    port = Some(temporaryServerPort()),
    userWithPassword = Some(UserWithPassword("bank", "passwd")),
    timeout = Some((30 seconds) * sys.env.getOrElse("SBT_TEST_TIME_FACTOR", "1").toDouble)
  )

  override protected lazy val downloadConfig: DownloadConfig =
    super.downloadConfig.copy(cacheDir = new File(sys.env("HOME") + "/.wixMySQL/downloads"))

  override protected lazy val schemaConfigs: Seq[SchemaConfig] = Seq(SchemaConfig(name = "bank"))

  override protected def flywayConfig(jdbcUrl: String): FlywayConfig =
    FlywayConfig(
      locations = Seq(
        "filesystem:tools/flyway/src/test/resources/db-migration"
      ),
      placeholderConfig = Some(
        PlaceholderConfig(placeholderReplacement = true,
                          placeholders = Map("engineName" -> "MEMORY", "idSequenceNumberEngineName" -> "MyISAM"))
      )
    )

}
