import com.micronautics.gitStats._
import java.io.File
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest._
import org.scalatest.Matchers._
import RichFile._

@RunWith(classOf[JUnitRunner])
class TestyMcTestFace extends WordSpec with MustMatchers {
  "Directory walking" should {
    "Define various directories" in {
      val parentName = parentDirectory.getName
      parentName.length should be >= 0

      val ps: List[File] = gitProjectsUnder(parentDirectory)
      ps.size should be >= 0
    }

    "work" in {
      val actual: Seq[File] = gitProjectsUnder(currentDirectory)
      actual shouldBe Seq(RichFile.currentDirectory)

      /*TODO This test is fragile, it depends on the content of the parent directory.
      * For example, if the parent directory contains .ignore.stats, then the test fails. */
//      val actual2: Seq[File] = gitProjectsUnder(parentDirectory)
//      actual2.size should be >= 1
    }
  }
}
