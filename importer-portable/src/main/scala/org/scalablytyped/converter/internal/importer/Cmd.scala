package org.scalablytyped.converter.internal
package importer

import com.olvind.logging.Logger

class Cmd(logger: Logger[_], maxLengthOpt: Option[Int]) {
  def runVerbose(cmd: os.Shellable*)(implicit wd: os.Path): os.CommandResult = execute(verbose = true, wd, cmd)
  def run(cmd:        os.Shellable*)(implicit wd: os.Path): os.CommandResult = execute(verbose = false, wd, cmd)

  private val MaxLength = maxLengthOpt.getOrElse(Int.MaxValue)

  private def execute(verbose: Boolean, wd: os.Path, cmd: Seq[os.Shellable]): os.CommandResult = {
    val proc = os.proc(cmd: _*)

    lazy val cmdStr = s"'$wd: ${proc.commandChunks.mkString(" ")}'" match {
      case tooLong if tooLong.length > MaxLength => tooLong.take(MaxLength - 3) + "..."
      case other                                 => other
    }

    if (verbose) {
      logger.warn(cmdStr)
    }

    try proc.call(cwd = wd, stderr = os.Pipe)
    catch {
      case ex: os.SubprocessException =>
        logger.error(s"$cmdStr ${ex.result}")
        throw ex
    }
  }
}
