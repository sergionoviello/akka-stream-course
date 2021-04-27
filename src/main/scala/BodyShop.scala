import java.time.Duration

import akka.actor.Cancellable
import akka.stream.scaladsl.Source

import scala.concurrent.duration.{FiniteDuration}

trait BodyShoppable {
  def cars: Source[UnfinishedCar, Cancellable]
}

class BodyShop(val buildTime: FiniteDuration) extends BodyShoppable{
    def cars:Source[UnfinishedCar, Cancellable] = Source.tick(buildTime, buildTime, new UnfinishedCar)
}
