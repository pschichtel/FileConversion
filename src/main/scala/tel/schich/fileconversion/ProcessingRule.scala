package tel.schich.fileconversion

import java.nio.file.Path

trait ProcessingRule extends RuleProcessor {
    def isApplicable(path: Path): Boolean
}
