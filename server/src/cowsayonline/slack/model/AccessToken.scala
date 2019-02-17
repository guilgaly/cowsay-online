package cowsayonline.slack.model

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AccessToken(
    accessToken: String,
    scope: String,
    teamName: String,
    teamId: String)

object AccessToken {
  implicit val reads: Reads[AccessToken] =
    (
      (__ \ 'access_token).read[String] ~
        (__ \ 'scope).read[String] ~
        (__ \ 'team_name).read[String] ~
        (__ \ 'team_id).read[String]
    )(AccessToken.apply _)
}
