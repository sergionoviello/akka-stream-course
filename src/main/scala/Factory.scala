import akka.stream.Materializer
import akka.stream.scaladsl.Sink

import scala.concurrent.Future

case class Factory(
                    bodyShop: BodyShop,
                    paintShop: PaintShop,
                    engineShop: EngineShop,
                    wheelShop: WheelShop,
                    qa: QualityAssurance,
                  upgradeShop: UpgradeShop
                  )(implicit mat: Materializer) {

  def orderCars(quantity: Int):Future[Seq[Car]] = {
    bodyShop.cars
      .via(paintShop.paint.named("paint-stage"))
      .via(engineShop.installEngines.named("engine-stage"))
      //.async
      .via(wheelShop.installWheels.named("wheels stage"))
      //.async
      .via(upgradeShop.installUpgrade.named("upgrade stage"))
      .via(qa.inspect.named("qa stage"))
      .take(quantity)
      .runWith(Sink.collection)
  }
}