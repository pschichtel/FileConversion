package tel.schich.fileconversion

import java.nio.file.{Files, Path}

class FileCopier(targetPath: Path) extends RuleProcessor {
    override def process(path: Path) = {
        Files.copy(path, targetPath.resolve(path.getFileName))
        (Some(path), Seq.empty)
    }
}
