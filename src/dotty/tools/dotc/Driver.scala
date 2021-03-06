package dotty.tools.dotc

import config.CompilerCommand
import core.Contexts.{Context, ContextBase}
import util.DotClass
import reporting._
import scala.util.control.NonFatal

abstract class Driver extends DotClass {

  val prompt = "\ndotc> "

  protected def newCompiler(): Compiler

  protected def emptyReporter: Reporter = new StoreReporter

  protected def doCompile(compiler: Compiler, fileNames: List[String])(implicit ctx: Context): Reporter =
    if (fileNames.nonEmpty)
      try {
        val run = compiler.newRun
        run.compile(fileNames)
        run.printSummary()
      }
      catch {
        case ex: FatalError  =>
          ctx.error(ex.getMessage) // signals that we should fail compilation.
          ctx.typerState.reporter
      }
    else emptyReporter

  protected def initCtx = (new ContextBase).initialCtx

  protected def sourcesRequired = true

  def setup(args: Array[String], rootCtx: Context): (List[String], Context) = {
    val summary = CompilerCommand.distill(args)(rootCtx)
    implicit val ctx: Context = initCtx.fresh.setSettings(summary.sstate)
    val fileNames = CompilerCommand.checkUsage(summary, sourcesRequired)
    (fileNames, ctx)
  }

  def process(args: Array[String], rootCtx: Context): Reporter = {
    val (fileNames, ctx) = setup(args, rootCtx)
    doCompile(newCompiler(), fileNames)(ctx)
  }

  def main(args: Array[String]): Unit =
    sys.exit(if (process(args, initCtx).hasErrors) 1 else 0)
}

class FatalError(msg: String) extends Exception

