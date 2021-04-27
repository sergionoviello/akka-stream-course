import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}

case class WheelShop() {
  val wheels:Source[Wheel, NotUsed]  = {
    Source.repeat(new Wheel)
  }

  private val groupedWheels: Source[Seq[Wheel], NotUsed] = wheels.grouped(4)


  val installWheels:Flow[UnfinishedCar, UnfinishedCar, NotUsed] = {
    Flow[UnfinishedCar].zip(groupedWheels).map {
      case (car: UnfinishedCar, seqWheels) =>
        car.installWheels(seqWheels)
    }
  }
}
