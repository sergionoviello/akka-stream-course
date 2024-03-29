import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{ActorMaterializer, Attributes, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

object Stream3 extends App {
  implicit val system = ActorSystem("stream1")
  implicit val materializer = ActorMaterializer()
  implicit val loggingAdapter = system.log
  implicit val ec: ExecutionContext = ExecutionContext.global
  val maxSubstreams = 2


  def addB: Flow[(Int, Int), String, NotUsed] = {
    Flow[(Int, Int)].map(i1 => i1._1.toString + "b")
  }

  val q = Source
    .queue[Int](3, OverflowStrategy.backpressure)
    .groupBy(
      maxSubstreams,
      e => math.abs(e % maxSubstreams),
      allowClosedSubstreamRecreation = true
    )
    .async
    .addAttributes(Attributes.inputBuffer(initial = maxSubstreams, max = maxSubstreams))
    .log("> Item", e => e)
    .withAttributes(
      Attributes.logLevels(
        onElement = Logging.DebugLevel,
        onFinish = Logging.DebugLevel,
        onFailure = Logging.ErrorLevel
      )
    )
    .map{ e =>
      (e, 0)
    }

    .log(">> Item", e => e)

    .withAttributes(
      Attributes.logLevels(
        onElement = Logging.DebugLevel,
        onFinish = Logging.DebugLevel,
        onFailure = Logging.ErrorLevel
      )
    )
    //.async
    //.via(addB)
    .mapAsync(1) { e =>
      if (e._1 == 1) {
        Source.single(e).delay(10.seconds).runWith(Sink.last)
      } else {
        Source.single(e).runWith(Sink.last)
      }

    }
    .log(">>> Item", e => e)

    .withAttributes(
      Attributes.logLevels(
        onElement = Logging.DebugLevel,
        onFinish = Logging.DebugLevel,
        onFailure = Logging.ErrorLevel
      )
    )
    .to(Sink.ignore)
    .run()

  q.offer(1)
  q.offer(2)
  q.offer(3)
  q.offer(4)
  q.offer(5)
  q.offer(6)
}
