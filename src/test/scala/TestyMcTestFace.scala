import com.micronautics.gitStats._
import java.io.File
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest._
import org.scalatest.Matchers._
import RichFile._

@RunWith(classOf[JUnitRunner])
class TestyMcTestFace extends WordSpec with MustMatchers {
  implicit val config: ConfigGitStats = ConfigGitStats()

  "Directory walking" should {
    "Define various directories" in {
      val parentName = parentDirectory.getName
      parentName.length should be >= 0

      val ps: List[File] = gitProjectsUnder(parentDirectory)
      ps.size should be >= 0
    }
  }

  "Commits" should {
    implicit val config: ConfigGitStats = ConfigGitStats(ignoredFileTypes = List("sql"), ignoredSubDirectories = List("sub"))
    "filter" in {
      val commitNotIgnored = new Commit(added=1, deleted=2, fileName = "baz/blah.scala")
      assert(!commitNotIgnored.ignoredFiletype)
      assert(!commitNotIgnored.ignoredPath)

      val commitFiletypeIgnored = new Commit(added=10, deleted=20, fileName = "baz/blah.sql")
      assert(commitFiletypeIgnored.ignoredFiletype)
      assert(!commitFiletypeIgnored.ignoredPath)

      val commitSubdirIgnored = new Commit(added=100, deleted=200, fileName = "sub/blah.scala")
      assert(!commitSubdirIgnored.ignoredFiletype)
      assert(commitSubdirIgnored.ignoredPath)

      val commitDoublyIgnored = new Commit(added=1000, deleted=2000, fileName = "sub/blah.sql")
      assert(commitDoublyIgnored.ignoredFiletype)
      assert(commitDoublyIgnored.ignoredPath)

      val commits = List(commitNotIgnored, commitFiletypeIgnored, commitSubdirIgnored, commitDoublyIgnored)
      val huh: List[Commit] =
        commits
          .filterNot(_.ignoredFiletype)
          .filterNot(_.ignoredPath)
      huh mustBe List(commitNotIgnored)
    }
  }

  "Poi" should {
    "work" in {
      val wb = new ExcelOutput("test")
      wb.addSheet(title="Test", contents=List(List("a", "b", "c")):_*)
      wb.save()
    }
  }
}
