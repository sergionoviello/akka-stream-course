object Car{
  def apply(car:UnfinishedCar) = {
   new Car(car.color.get,car.engine.get, car.wheels)
  }
}
case class Car(color: Color, engine: Engine, wheels:Seq[Wheel]) {
  require(wheels.size == 4)
}
