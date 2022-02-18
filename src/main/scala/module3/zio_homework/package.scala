package module3

import module3.zioConcurrency.printEffectRunningTime
import module3.zio_homework.config.{AppConfig, load}
import zio.Exit.Success
import zio.{Exit, Has, Task, ULayer, ZIO, ZLayer}
import zio.clock.{Clock, sleep}
import zio.console._
import zio.duration.durationInt
import zio.macros.accessible
import zio.random._

import java.io.IOException
import java.util.concurrent.TimeUnit
import scala.io.StdIn
import scala.language.postfixOps

package object zio_homework {
  /**
   * 1.
   * Используя сервисы Random и Console, напишите консольную ZIO программу которая будет предлагать пользователю угадать число от 1 до 3
   * и печатать в когнсоль угадал или нет. Подумайте, на какие наиболее простые эффекты ее можно декомпозировать.
   */

  def toIntInRange(s: String, from: Int, to: Int) = {
    val result = s.toInt
    if (result < from || result > to) throw new IndexOutOfBoundsException
    result
  }

  def guessResult(value: Int, guess: Int) =
    if (value == guess) s"Угадали: $value"
    else s"Не угадали. Я загадал: $value"

  def validIntPrompt(from: Int, to: Int) = putStrLn(s"Введите целое число от $from до $to")

  def readIntInRange (from: Int, to: Int) =
    getStrLn.flatMap(str => ZIO.effect(toIntInRange(str, from, to)))

  def promptIntInRange (from: Int, to: Int): ZIO[Console, Exception, Int] =
    validIntPrompt(from, to) *> readIntInRange(from, to).orElse(promptIntInRange(from, to))

  def guessInRange(from: Int, to: Int) = for {
    v <- zio.random.nextIntBetween(from, to)
    g <- promptIntInRange(from, to)
    _ <- putStrLn(guessResult(v, g))
  } yield ()

  //ZioHomeWork1GuessApp
  lazy val guessProgram = guessInRange(1, 3)


  /**
   * 2. реализовать функцию doWhile (общего назначения), которая будет выполнять эффект до тех пор, пока его значение в условии не даст true
   * 
   */

    //ZioHomeWork2DoWhileApp
      def doWhile[R, E, A] (effect: ZIO[R, E, A], condition: A => Boolean): ZIO[R, E, A] =
        effect.flatMap(a =>
          if(condition(a))
            ZIO.succeed(a)
          else
            doWhile(effect, condition))


  /**
   * 3. Реализовать метод, который безопасно прочитает конфиг из файла, а в случае ошибки вернет дефолтный конфиг
   * и выведет его в консоль
   * Используйте эффект "load" из пакета config
   */

  //ZioHomeWork3LoadConfigApp
  def loadConfigOrDefault = for {
    conf <- load.orElse(Task.succeed(AppConfig("ZIO Homework", "about:blank")))
    _ <- zio.console.putStrLn(conf.toString)
  } yield (conf)


  /**
   * 4. Следуйте инструкциям ниже для написания 2-х ZIO программ,
   * обратите внимание на сигнатуры эффектов, которые будут у вас получаться,
   * на изменение этих сигнатур
   */


  /**
   * 4.1 Создайте эффект, который будет возвращать случайеым образом выбранное число от 0 до 10 спустя 1 секунду
   * Используйте сервис zio Random
   */

  //ZioHomeWork41Random1sApp
  lazy val eff = ZIO.sleep(1 second) *> zio.random.nextIntBetween(0, 10)


  def show_effect[R, E, A](effect: ZIO[R, E, A]) = for{
    r <- effect
    _ <- zio.console.putStrLn(r.toString)
  } yield r

  /**
   * 4.2 Создайте коллукцию из 10 выше описанных эффектов (eff)
   */

  //ZioHomeWork42_10effectsApp
  lazy val effects = ZIO.collectAll(List.fill(10)(eff))

  
  /**
   * 4.3 Напишите программу которая вычислит сумму элементов коллекци "effects",
   * напечатает ее в консоль и вернет результат, а также залогирует затраченное время на выполнение,
   * можно использовать ф-цию printEffectRunningTime, которую мы разработали на занятиях
   */

  //ZioHomeWork43SumAndLogApp
  lazy val app = printEffectRunningTime(show_effect(effects.map(list => list.sum)))



  /**
   * 4.4 Усовершенствуйте программу 4.3 так, чтобы минимизировать время ее выполнения
   */

  //ZioHomeWork44FastSumAndLogApp
  lazy val fast_effects = ZIO.collectAllPar(List.fill(10)(eff))
  lazy val appSpeedUp = printEffectRunningTime(show_effect(fast_effects.map(list => list.sum)))


  /**
   * 5. Оформите ф-цию printEffectRunningTime разработанную на занятиях в отдельный сервис, так чтобы ее
   * молжно было использовать аналогично zio.console.putStrLn например
   */

  type PrintEffectService = Has[PrintEffectService.Service]

  @accessible
  object PrintEffectService{
    trait Service{
      def print_effect_running_time[R, E, A](effect: ZIO[R, E, A]): ZIO[Console with Clock with R, E, A]
    }

    class ServiceImpl extends Service{
      def print_effect_running_time[R, E, A](effect: ZIO[R, E, A]) = printEffectRunningTime(effect)

    }

    val live: ZLayer[Console, Nothing, PrintEffectService] = ZLayer.fromService(c => new ServiceImpl)
  }

   /**
     * 6.
     * Воспользуйтесь написанным сервисом, чтобы созадть эффект, который будет логировать время выполнения прогаммы из пункта 4.3
     *
     * 
     */

  lazy val effectFrom43: ZIO[Console with Random with Clock, Nothing, Int] = show_effect(effects.map(list => list.sum))
  lazy val appWithTimeLogg: ZIO[Console with Random with Clock with PrintEffectService, Nothing, Int] = PrintEffectService.print_effect_running_time(effectFrom43)

  /**
    * 
    * Подготовьте его к запуску и затем запустите воспользовавшись ZioHomeWorkApp
    */

  //ZioHomeWork6ServiceApp
  lazy val runApp = appWithTimeLogg.provideSomeLayer[Console with Random with Clock](PrintEffectService.live)
  
}
