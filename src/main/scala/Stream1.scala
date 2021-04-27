import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{ActorMaterializer, Attributes, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

object Stream1 extends App {

  implicit val system = ActorSystem("stream1")
  implicit val materializer = ActorMaterializer()
  implicit val loggingAdapter = system.log

  val q = Source
    .queue[Int](1, OverflowStrategy.backpressure)
    .log("> Item", e => e)
    .withAttributes(
      Attributes.logLevels(
        onElement = Logging.DebugLevel,
        onFinish = Logging.DebugLevel,
        onFailure = Logging.ErrorLevel
      )
    )
    //.to(Sink.ignore)
    .toMat(Sink.foreach(x => println(s"completed $x")))(Keep.left)
    .run()

  q.offer(3)
  q.offer(1)
  q.offer(2)

  q.complete()

}
