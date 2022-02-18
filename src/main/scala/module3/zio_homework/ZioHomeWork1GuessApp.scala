package module3.zio_homework

import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ExitCode, URIO}

object HomeWork1GuessApp extends App {
    zio.Runtime.default.unsafeRun(guessProgram)
}

object ZioHomeWork1GuessApp extends zio.App {
  override def run(args: List[String]) =
    guessProgram.exitCode
}





