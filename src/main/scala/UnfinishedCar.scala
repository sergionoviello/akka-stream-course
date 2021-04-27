import akkassembly.busy

import scala.collection.immutable.Seq
import scala.concurrent.duration.DurationInt

case class UnfinishedCar(color: Option[Color] = None, wheels: Seq[Wheel] = Seq.empty, engine: Option[Engine] = None, upgrade: Option[Upgrades] = None) {
  def paint(color: Color) = {
    busy(2.millis)
    copy(color = Some(color))
  }

  def installWheels(wheels: Seq[Wheel]) = {
    busy(2.millis)
    copy(wheels = wheels)
  }

  def installEngine(engine: Engine) = {
    busy(10.millis)
    copy(engine = Some(engine))
  }

  def installUpgrades(upgrade: Upgrades) ={
    busy(2.millis)
    copy(upgrade = Some(upgrade))
  }


  def isValid:Boolean = color.isDefined && engine.isDefined && wheels.size == 4
}
