package tel.schich.fileconversion

import java.nio.file.{StandardCopyOption, Files, Path}

class FileMover(targetPath: Path) extends RuleProcessor {
    override def process(path: Path) = {
        Files.move(path, targetPath.resolve(path.getFileName), StandardCopyOption.ATOMIC_MOVE)
        (None, Seq.empty)
    }
}
