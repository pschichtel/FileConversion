package tel.schich.fileconversion

import java.nio.file.Path
import java.util.regex.Pattern


abstract class AbstractRule(proc: RuleProcessor) extends ProcessingRule {
    override def process(path: Path) = proc.process(path)
}

class Rule(pattern: Pattern, proc: RuleProcessor) extends AbstractRule(proc) {
    override def isApplicable(path: Path): Boolean = pattern.matcher(path.toString).find()
}

class MatchAllRule(proc: RuleProcessor) extends AbstractRule(proc) {
    override def isApplicable(path: Path): Boolean = true
}

object Rule {
    def apply(pattern: Pattern, proc: RuleProcessor) = new Rule(pattern, proc)
    def any(proc: RuleProcessor) = {
        new MatchAllRule(proc)
    }
}
