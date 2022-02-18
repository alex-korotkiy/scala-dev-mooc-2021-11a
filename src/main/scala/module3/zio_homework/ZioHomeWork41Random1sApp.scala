package module3.zio_homework

import module3.zio_homework.config.load
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ExitCode, URIO}

object HomeWork41Random1sApp extends App {
    zio.Runtime.default.unsafeRun(show_effect(eff))
}

object ZioHomeWork41Random1sApp extends zio.App {
  override def run(args: List[String]) =
    show_effect(eff).exitCode
}





