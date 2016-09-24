package tel.schich.fileconversion

import java.nio.file.{Files, Path}

/**
  * Created by Alex on 24.09.2016.
  */
class NameNormalizer(normalizers: (String => String)*) extends RuleProcessor {
    override def process(path: Path) = {
        val origName = path.getFileName.toString
        val target = path.getParent.resolve(normalizers.foldLeft(origName) {(name, replacer) =>
            replacer(name)
        })
        Files.move(path, target)
        (Some(target), Seq(target))
    }
}
