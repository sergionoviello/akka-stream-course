import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge}

case class UpgradeShop() {

  val installUpgrade:Flow[UnfinishedCar, UnfinishedCar, NotUsed] = {
    Flow.fromGraph(GraphDSL.create() {
      implicit builder: GraphDSL.Builder[NotUsed] =>
        import GraphDSL.Implicits._
        val balance = builder.add(Balance[UnfinishedCar](3))
        val merge = builder.add(Merge[UnfinishedCar](3))
        val flowDX = Flow[UnfinishedCar].map(_.installUpgrades(Upgrades.DX))
        val flowSport = Flow[UnfinishedCar].map(_.installUpgrades(Upgrades.Sport))
        val flowStandard = Flow[UnfinishedCar].map(identity)

        balance ~> flowDX ~> merge
        balance ~> flowSport ~> merge
        balance ~> flowStandard ~> merge

        FlowShape(balance.in, merge.out)

    })
  }


}
