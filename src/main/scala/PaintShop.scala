import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}


class PaintShop(colorSet: Set[Color]) {

  val colors: Source[Color, NotUsed] = {
    Source.cycle(() => colorSet.iterator)
  }

  val paint:Flow[UnfinishedCar, UnfinishedCar, NotUsed] = {
    Flow[UnfinishedCar].zip(colors).map((carAndColor) => carAndColor._1.paint(carAndColor._2))
  }
}
