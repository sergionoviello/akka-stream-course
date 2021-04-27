import akka.NotUsed
import akka.stream.ActorAttributes
import akka.stream.Supervision.{Decider, Resume, Stop}
import akka.stream.scaladsl.Flow

object QualityAssurance{
  case class CarFailedInspection(car: UnfinishedCar)
    extends IllegalStateException(s"Unappropriated car found: ${car}")
}

case class QualityAssurance() {

  private val decider: Decider = {
    case _:QualityAssurance.CarFailedInspection => Resume
    case _ => Stop
  }
  val inspect:Flow[UnfinishedCar, Car,NotUsed] = {
    Flow[UnfinishedCar]
      .map{
        case unfinishedCar: UnfinishedCar if unfinishedCar.isValid => Car(unfinishedCar)
        case unfinishedCar: UnfinishedCar => throw QualityAssurance.CarFailedInspection(unfinishedCar)
      }.withAttributes(ActorAttributes.supervisionStrategy(decider))
  }
}
