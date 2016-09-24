package tel.schich.fileconversion

import java.lang.ProcessBuilder.Redirect
import java.nio.file.{Files, Path, Paths}

/**
  * Created by Alex on 24.09.2016.
  */
class InkscapeConverter(to: String, deleteOriginal: Boolean = true, binary: String = "inkscape") extends RuleProcessor {

    override def process(path: Path) = {
        val target = Paths.get(removeExtension(path) + "." + to)
        InkscapeConverter.runCommand(binary, path.toString, "--export-" + to, target.toString)
        if (deleteOriginal) {
            Files.deleteIfExists(path)
        }
        (Some(path), Seq(target))
    }

    private def removeExtension(path: Path): String = {
        val s = path.toString
        val lastIndex = s.lastIndexOf('.')
        if (lastIndex == -1) s
        else s.substring(0, lastIndex)
    }
}

object InkscapeConverter {
    def runCommand(parts: String*): Int = {
        val builder = new ProcessBuilder
        builder.command(parts:_*)
        builder.redirectOutput(Redirect.INHERIT)
        builder.redirectInput(Redirect.INHERIT)
        builder.redirectError(Redirect.INHERIT)
        val process = builder.start()
        process.waitFor()
    }
}
