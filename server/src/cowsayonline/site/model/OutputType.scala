package cowsayonline.site.model

import akka.http.scaladsl.unmarshalling.FromStringUnmarshaller
import cowsay4s.core.EnumWithDefault
import cowsayonline.util.MarshallingUtils
import enumeratum.EnumEntry

import scala.collection.immutable

sealed trait OutputType extends EnumEntry

object OutputType extends EnumWithDefault[OutputType] {

  case object Text extends OutputType
  case object Png extends OutputType

  override def values: immutable.IndexedSeq[OutputType] = findValues

  implicit val unmarshaller: FromStringUnmarshaller[OutputType] =
    MarshallingUtils.enumFromStringUnmarshaller(OutputType, "outputType")

  override def defaultValue: OutputType = Text
}
