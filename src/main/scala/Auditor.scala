import akka.{Done, NotUsed}
import akka.event.LoggingAdapter
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration}

case class Auditor()(implicit mat: Materializer) {

  def count:Sink[Car, Future[Int]] = {
    Sink.fold(0)((c, elem: Car) => c + 1)
  }

  def log(loggingAdapter: LoggingAdapter):Sink[Object, Future[Done]] = {
    Sink.foreach(elem => loggingAdapter.debug(elem.toString))
  }

  def sample(sampleSize:FiniteDuration):Flow[Car,Car,NotUsed] = {
      Flow[Car].takeWithin(sampleSize)
  }

  def audit(cars:Source[Car,NotUsed], sampleSize:FiniteDuration):Future[Int] = {
    cars.via(sample(sampleSize)).toMat(count)(Keep.right).run()
  }

}
