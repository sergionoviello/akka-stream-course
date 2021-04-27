import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{ActorMaterializer, Attributes, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}

object Stream3 extends App {
  implicit val system = ActorSystem("stream1")
  implicit val materializer = ActorMaterializer()
  implicit val loggingAdapter = system.log
  val maxSubstreams = 4


  def addB: Flow[(Int, Int), String, NotUsed] = {
    Flow[(Int, Int)].map(i1 => i1._1.toString + "b")
  }

  val q = Source
    .queue[Int](1, OverflowStrategy.backpressure)
    .groupBy(
      maxSubstreams,
      e => math.abs(e % maxSubstreams),
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
    //.via(addB)
    .mapAsync(1) { e =>
      Source.single(e).runWith(Sink.last)
    }
    .log(">>> Item", e => e)

    .withAttributes(
      Attributes.logLevels(
        onElement = Logging.DebugLevel,
        onFinish = Logging.DebugLevel,
        onFailure = Logging.ErrorLevel
      )
    )
    /*.mapAsync(1) { e =>
      Source
        .single(e)
        .via(addB)
        .log(
          ">> Item", e => e
        )
        .withAttributes(
          Attributes.logLevels(
            onElement = Logging.DebugLevel,
            onFinish = Logging.DebugLevel,
            onFailure = Logging.ErrorLevel
          )
        )
        .runWith(Sink.lastOption)

    }*/
    .to(Sink.ignore)
    .run()

  q.offer(1)
  q.offer(2)
  q.offer(3)
}
