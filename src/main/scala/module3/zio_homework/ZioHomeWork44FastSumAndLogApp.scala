package module3.zio_homework

import module3.zio_homework.config.load
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ExitCode, URIO}

object HomeWork44FastSumAndLogApp extends App {
    zio.Runtime.default.unsafeRun(appSpeedUp)
}

object ZioHomeWork44FastSumAndLogApp extends zio.App {
  override def run(args: List[String]) =
    appSpeedUp.exitCode
}





