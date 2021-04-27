import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{ActorMaterializer, Attributes, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

object Stream2 extends App {
  implicit val system = ActorSystem("stream1")
  implicit val materializer = ActorMaterializer()
  implicit val loggingAdapter = system.log
  val maxSubstreams = 2

  val q = Source
    .queue[Int](1, OverflowStrategy.backpressure)
    .groupBy(
      maxSubstreams,
      e => math.abs(e % maxSubstreams), // 1 % 2 = 1, 2 % 2 = 0, 3 % 1 = 1
      allowClosedSubstreamRecreation = true
    )
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
    .mergeSubstreams
    .toMat(Sink.foreach(x => loggingAdapter.info(s"completed $x")))(Keep.both)
    //.toMat(Sink.seq)(Keep.both)

    .run()

  q._1.offer(1)
  q._1.offer(2)
  q._1.offer(3)

  q._1.complete()
}
