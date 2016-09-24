package tel.schich.fileconversion

import java.io.Closeable
import java.lang.ProcessBuilder.Redirect
import java.nio.file._
import java.nio.file.StandardWatchEventKinds._
import java.nio.file.attribute.BasicFileAttributes

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
  * Created by Alex on 24.09.2016.
  */
class FolderMonitor(path: Path, rules: Seq[ProcessingRule], recursive: Boolean = false) extends Runnable {

    private var stopped = false

    def stop(): Unit = {
        stopped = true
    }

    private def loan[T <: Closeable, U](resource: T)(block: (T) => U): U = {
        try {
            block(resource)
        } finally {
            resource.close()
        }
    }

    private def registerRecursive(path: Path, watchService: WatchService, kinds: WatchEvent.Kind[_]*): Unit = {
        Files.walkFileTree(path, new SimpleFileVisitor[Path] {
            override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
                dir.register(watchService, kinds:_*)
                FileVisitResult.CONTINUE
            }
        })
    }

    @tailrec
    private def process(path: Path, procs: Seq[RuleProcessor], ac: Seq[Path] = Seq.empty): Seq[Path] = {
        if (procs.isEmpty) ac
        else {
            val proc = procs.head
            proc.process(path) match {
                case (Some(continueWith), products) => process(continueWith, procs.tail, ac ++ products)
                case (_, products) => ac ++ products
            }
        }
    }

    override def run(): Unit = {
        loan(path.getFileSystem.newWatchService()) { watcher =>
            if (recursive) registerRecursive(path, watcher, ENTRY_CREATE)
            else path.register(watcher, ENTRY_CREATE)

            val previouslyProduced: ArrayBuffer[Path] = new ArrayBuffer()
            while (!stopped) {
                println("Taking watchkeys")
                val key = watcher.take()
                if (key != null) {
                    val events = key.pollEvents().toSeq
                    val ignorePaths = Set(previouslyProduced:_*)
                    previouslyProduced.clear()
                    for (e <- events) {
                        println(s"Event received: ${e.kind().toString}")
                        e.context() match {
                            case p: Path if Files.exists(p) && !Files.isDirectory(p) =>
                                val real = p.toRealPath()
                                if (!ignorePaths.contains(real))
                                    previouslyProduced ++= process(real, rules.view.filter(_.isApplicable(real))).filter(Files.exists(_)).map(_.toRealPath())
                            case _ => println(s"Weird: ${e.context().getClass.getName}")
                        }
                    }
                    key.reset()
                }
            }
        }
    }
}
