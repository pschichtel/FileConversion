package tel.schich.fileconversion

import java.nio.file.Path

/**
  * Created by Alex on 24.09.2016.
  */
trait RuleProcessor {
    def process(path: Path): (Option[Path], Seq[Path])
}
