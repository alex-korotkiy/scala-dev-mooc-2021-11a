package module3.zio_homework

import module3.zio_homework.config.load
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ExitCode, URIO}

object HomeWorkLoad3ConfigApp extends App {
    zio.Runtime.default.unsafeRun(loadConfigOrDefault)
}

object ZioHomeWork3LoadConfigApp extends zio.App {
  override def run(args: List[String]) =
    loadConfigOrDefault.exitCode
}





