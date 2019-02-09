package cowsayonline.api.model

import play.api.libs.json.{Json, OFormat}

case class About(
    apiName: String,
    apiVersion: String
)

object About {
  implicit val format: OFormat[About] = Json.format
}
