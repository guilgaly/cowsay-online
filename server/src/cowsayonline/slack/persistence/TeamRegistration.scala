package cowsayonline.slack.persistence
import java.time.Instant

sealed trait TeamRegistrationLike {
  def teamId: String
  def accessToken: String
}

final case class NewTeamRegistration(
    teamId: String,
    accessToken: String)
    extends TeamRegistrationLike

final case class TeamRegistration(
    teamId: String,
    createdOn: Instant,
    updatedOn: Instant,
    accessToken: String)
    extends TeamRegistrationLike
