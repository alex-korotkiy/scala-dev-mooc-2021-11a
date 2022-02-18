package module3.zio_homework

import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.random.Random
import zio.{ExitCode, Task, URIO, ZIO}
import DoWhileEffects._

object DoWhileEffects {

  val printRandom: URIO[Console with Random, Double] = for {
    v <- zio.random.nextDouble
    _ <- putStrLn(v.toString)
  } yield v

  val condition: Double => Boolean = v => v >= 0.9
}

object ZioHomeWork2DoWhileApp extends zio.App {
  override def run(args: List[String]): URIO[Console with Random, ExitCode] =
    doWhile(printRandom, condition).exitCode
}

object HomeWork2DoWhileApp extends App {
  zio.Runtime.default.unsafeRun(doWhile(printRandom, condition))
}


