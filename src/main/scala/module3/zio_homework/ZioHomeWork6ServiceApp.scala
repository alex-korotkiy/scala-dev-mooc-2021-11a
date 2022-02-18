package module3.zio_homework

import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ExitCode, URIO}

object HomeWork6ServiceApp extends zio.App {
  override def run(args: List[String]) =
    runApp.exitCode
}

object ZioHomeWork6ServiceApp extends App {
  zio.Runtime.default.unsafeRun(runApp)
}


