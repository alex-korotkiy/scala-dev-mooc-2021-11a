package module3.zio_homework

import module3.zio_homework.config.load
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ExitCode, URIO}

object HomeWork43SumAndLogApp extends App {
    zio.Runtime.default.unsafeRun(app)
}

object ZioHomeWork43SumAndLogApp extends zio.App {
  override def run(args: List[String]) =
    app.exitCode
}





