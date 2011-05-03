import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.demiurgo.operalink._
import scalaj.http.Token

class LinkAPIItemDiffSpec extends FlatSpec with ShouldMatchers {
  val fakeConsumer    = Token("foo", "bar")
  val fakeAccessToken = Token("foo", "bar")
  val api = new LinkAPI(fakeConsumer, fakeAccessToken)

  "The diff calculator" should "calculate an empty diff correctly" in {
    api.serverProxy = new TestLinkServerProxy(fakeConsumer,
                                              fakeAccessToken,
                                              "twoBookmarks")
    val bms = api.getBookmarks()
    val diff = new Diff
    val diffObjects = diff.calculateDiff(bms, bms)

    diffObjects.size should equal(2)
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").diffType should equal("update")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").addedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").updatedProperties should equal(bms(0).propertyHash.keys.toSet)
    diffObjects("E22F0BF0524511E08C69D46C240865CA").diffType should equal("update")
    diffObjects("E22F0BF0524511E08C69D46C240865CA").addedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").removedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").updatedProperties should equal(bms(1).propertyHash.keys.toSet)
  }

  it should "calculate a single add item diff correctly" in {
    api.serverProxy = new TestLinkServerProxy(fakeConsumer,
                                              fakeAccessToken,
                                              "twoAndThreeBookmarks")
    val bms1 = api.getBookmarks()
    val bms2 = api.getBookmarks()
    val diff = new Diff
    val diffObjects = diff.calculateDiff(bms1, bms2)

    diffObjects.size should equal(3)
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").diffType should equal("update")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").addedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").updatedProperties should equal(bms2(0).propertyHash.keys.toSet)
    diffObjects("E22F0BF0524511E08C69D46C240865CA").diffType should equal("update")
    diffObjects("E22F0BF0524511E08C69D46C240865CA").addedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").removedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").updatedProperties should equal(bms2(1).propertyHash.keys.toSet)
    diffObjects("E22F0BF0524511E08C69D46C240865CB").diffType should equal("add")
    diffObjects("E22F0BF0524511E08C69D46C240865CB").addedProperties should equal(bms2(2).propertyHash.keys.toSet)
    diffObjects("E22F0BF0524511E08C69D46C240865CB").removedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CB").updatedProperties should equal(Set())

    // Now, the reverse diff
    val diffObjectsReverse = diff.calculateDiff(bms2, bms1)

    diffObjectsReverse.size should equal(3)
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").diffType should equal("update")
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").addedProperties should equal(Set())
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").removedProperties should equal(Set())
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").updatedProperties should equal(bms2(0).propertyHash.keys.toSet)
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CA").diffType should equal("update")
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CA").addedProperties should equal(Set())
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CA").removedProperties should equal(Set())
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CA").updatedProperties should equal(bms2(1).propertyHash.keys.toSet)
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CB").diffType should equal("remove")
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CB").addedProperties should equal(Set())
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CB").removedProperties should equal(bms2(2).propertyHash.keys.toSet)
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CB").updatedProperties should equal(Set())
  }
}
