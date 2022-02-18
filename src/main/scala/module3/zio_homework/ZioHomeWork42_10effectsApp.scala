package module3.zio_homework

import module3.zio_homework.config.load
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ExitCode, URIO}

object HomeWork42_10effectsApp extends App {
    zio.Runtime.default.unsafeRun(show_effect(effects))
}

object ZioHomeWork42_10effectsApp extends zio.App {
  override def run(args: List[String]) =
    show_effect(effects).exitCode
}





