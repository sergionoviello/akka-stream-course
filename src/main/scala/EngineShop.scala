import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import java.util.stream._

import scala.jdk.CollectionConverters._

class EngineShop(val shipmentSize: Int) {
  val shipments: Source[Shipment, NotUsed] = {
    Source.fromIterator(() => Stream.generate(() => {
      val engines = (0 to shipmentSize).map(_ => new Engine)
      new Shipment(engines.toSet)
    }).iterator().asScala)
  }

  val engines:Source[Engine,NotUsed] = {
    shipments.flatMapConcat(shipment => Source.fromIterator(() => shipment.engines.iterator))
  }

  val installEngines:Flow[UnfinishedCar, UnfinishedCar, NotUsed] = {
    Flow[UnfinishedCar]
      .zip(engines)
      .map {
      case(car,engine) => car.installEngine(engine)
    }
  }

}
