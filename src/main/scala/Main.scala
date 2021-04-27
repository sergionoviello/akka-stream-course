import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{ActorMaterializer, Attributes, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akkassembly.TimedFuture

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

package object akkassembly {
  /**
   * Keeps the CPU busy for the given appoximate duration.
   */
  def busy(duration: FiniteDuration): Unit =
    pi(System.nanoTime() + duration.toNanos)

  /**
   * Calculate pi until System.nanoTime is later than `endNanos`
   */
  private def pi(endNanos: Long) = {
    def gregoryLeibnitz(n: Long) = 4.0 * (1 - (n % 2) * 2) / (n * 2 + 1)
    var n = 0
    var acc = BigDecimal(0.0)
    while (System.nanoTime() < endNanos) {
      acc += gregoryLeibnitz(n)
      n += 1
    }
    acc
  }

  /**
   * Allows a timer to be attached to a future providing the approximate amount
   * of time it takes to complete.
   * @param future The future to put the timer on.
   * @tparam A The type contained in the future.
   */
  implicit class TimedFuture[A](future: Future[A]) {
    def withTimer(name: String)
                 (implicit ec: ExecutionContext, log: LoggingAdapter): Future[A] = {
      val startTime = System.currentTimeMillis()

      future.andThen {
        case _ =>
          val endTime = System.currentTimeMillis()
          log.info(s"$name completed in ${endTime - startTime}ms")
      }
    }
  }
}

object Main extends App{
  implicit val system = ActorSystem("akkassembly")
  implicit val materializer = ActorMaterializer()
  implicit val loggingAdapter = system.log
  import system.dispatcher

  val buildTime = 1.millis
  val bodyShop = new BodyShop(buildTime)

  val paintColors = Set(Color("FFFFFF"), Color("000000"), Color("FF00FF"))
  val paintShop = new PaintShop(paintColors)
  val wheelShop = new WheelShop

  val shipmentSize = 10
  val engineShop = new EngineShop(shipmentSize)

  val upgradeShop = new UpgradeShop
  val qualityAssurance = new QualityAssurance

  val factory = new Factory(bodyShop, paintShop, engineShop, wheelShop, qualityAssurance, upgradeShop)
  val startTime = System.currentTimeMillis()


  factory.orderCars(1000).withTimer("Order Cars").andThen {
    case _ => system.terminate()
  }
}

