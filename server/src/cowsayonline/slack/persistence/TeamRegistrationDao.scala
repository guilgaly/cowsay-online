package cowsayonline.slack.persistence

import java.sql.{ResultSet, Timestamp}
import java.time.Instant
import java.util.{Calendar, TimeZone}
import scala.concurrent.Future

import cowsayonline.common.db.Database

final class TeamRegistrationDao(database: Database) {

  private val tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

  private val table = "slack_team_registrations"

  private val teamIdCol = "team_id"
  private val createdOnCol = "created_on"
  private val updatedOnCol = "updated_on"
  private val accessTokenCol = "access_token"

  def insertOrUpdate(teamRegistration: TeamRegistrationLike): Future[Unit] =
    database.withTransactionAsync { connection =>
      val sql =
        s"""INSERT INTO $table (
           |  $teamIdCol,
           |  $createdOnCol,
           |  $updatedOnCol,
           |  $accessTokenCol
           |)
           |VALUES (?, ?, ?, ?, ?)
           |ON CONFLICT ($teamIdCol) DO UPDATE SET
           |  $updatedOnCol = EXCLUDED.$updatedOnCol,
           |  $accessTokenCol = EXCLUDED.$accessTokenCol""".stripMargin
      val stmt = connection.prepareStatement(sql)

      stmt.setString(1, teamRegistration.teamId)
      val now = new Timestamp(Instant.now().toEpochMilli)
      stmt.setTimestamp(2, now, tzUTC)
      stmt.setTimestamp(3, now, tzUTC)
      stmt.setString(4, teamRegistration.accessToken)

      stmt.executeUpdate()
      stmt.close()
    }

  def get(teamId: String): Future[Option[TeamRegistration]] =
    database.withConnectionAsync { connection =>
      val sql =
        s"""SELECT
           |  $teamIdCol,
           |  $createdOnCol,
           |  $updatedOnCol,
           |  $accessTokenCol
           |FROM $table
           |WHERE $teamIdCol = ?""".stripMargin
      val stmt = connection.prepareStatement(sql)
      stmt.setString(1, teamId)

      val rs = stmt.executeQuery()
      val res = if (rs.next()) Some(mapRow(rs)) else None
      stmt.close()

      res
    }

  def list(): Future[Seq[TeamRegistration]] =
    database.withConnectionAsync { connection =>
      val sql =
        s"""SELECT
           |  $teamIdCol,
           |  $createdOnCol,
           |  $updatedOnCol,
           |  $accessTokenCol
           |FROM $table""".stripMargin
      val stmt = connection.prepareStatement(sql)

      val rs = stmt.executeQuery()
      var res = List.empty[TeamRegistration]
      while (rs.next()) {
        res = mapRow(rs) +: res
      }
      stmt.close()

      res.reverse
    }

  private def mapRow(rs: ResultSet): TeamRegistration = {
    val teamId = rs.getString(1)
    val createdOn = rs.getTimestamp(2, tzUTC).toInstant
    val updatedOn = rs.getTimestamp(3, tzUTC).toInstant
    val accessToken = rs.getString(4)
    TeamRegistration(teamId, createdOn, updatedOn, accessToken)
  }
}
