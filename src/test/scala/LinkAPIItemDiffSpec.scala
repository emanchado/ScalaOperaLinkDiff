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
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").diffType should equal("identical")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").addedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").updatedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").diffType should equal("identical")
    diffObjects("E22F0BF0524511E08C69D46C240865CA").addedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").removedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").updatedProperties should equal(Set())
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
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").diffType should equal("identical")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").addedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").updatedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").diffType should equal("identical")
    diffObjects("E22F0BF0524511E08C69D46C240865CA").addedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").removedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").updatedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CB").diffType should equal("add")
    diffObjects("E22F0BF0524511E08C69D46C240865CB").addedProperties should equal(bms2(2).propertyHash.keys.toSet)
    diffObjects("E22F0BF0524511E08C69D46C240865CB").removedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CB").updatedProperties should equal(Set())

    // Now, the reverse diff
    val diffObjectsReverse = diff.calculateDiff(bms2, bms1)

    diffObjectsReverse.size should equal(3)
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").diffType should equal("identical")
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").addedProperties should equal(Set())
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").removedProperties should equal(Set())
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").updatedProperties should equal(Set())
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CA").diffType should equal("identical")
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CA").addedProperties should equal(Set())
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CA").removedProperties should equal(Set())
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CA").updatedProperties should equal(Set())
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CB").diffType should equal("remove")
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CB").addedProperties should equal(Set())
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CB").removedProperties should equal(bms2(2).propertyHash.keys.toSet)
    diffObjectsReverse("E22F0BF0524511E08C69D46C240865CB").updatedProperties should equal(Set())
  }

  it should "calculate a single element diff correctly" in {
    api.serverProxy = new TestLinkServerProxy(fakeConsumer,
                                              fakeAccessToken,
                                              "bookmarkWithWithoutIcon")
    val bmWithoutIcon = api.getBookmarks()
    val bmWithIcon = api.getBookmarks()
    val diff = new Diff
    val diffObjects = diff.calculateDiff(bmWithoutIcon, bmWithIcon)

    diffObjects.size should equal(1)
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").diffType should equal("update")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").addedProperties should equal(Set("icon"))
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").updatedProperties should equal(Set("title"))
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").oldItem.asInstanceOf[Bookmark].title should equal("Wikipedia")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").newItem.asInstanceOf[Bookmark].title should equal("Wikipedia: now with icon!")

    // Reverse diff
    val diffObjectsReverse = diff.calculateDiff(bmWithIcon, bmWithoutIcon)

    diffObjectsReverse.size should equal(1)
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").diffType should equal("update")
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").addedProperties should equal(Set())
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").removedProperties should equal(Set("icon"))
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").updatedProperties should equal(Set("title"))
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").oldItem.asInstanceOf[Bookmark].title should equal("Wikipedia: now with icon!")
    diffObjectsReverse("E22EE4E0524511E08C68A6DDBCB261C0").newItem.asInstanceOf[Bookmark].title should equal("Wikipedia")
  }

  it should "calculate out of order element diff correctly" in {
    api.serverProxy = new TestLinkServerProxy(fakeConsumer,
                                              fakeAccessToken,
                                              "differentOrderBookmarks")
    val bms1 = api.getBookmarks()
    val bms2 = api.getBookmarks()
    val diff = new Diff
    val diffObjects = diff.calculateDiff(bms1, bms2)

    diffObjects.size should equal(4)
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").diffType should equal("identical")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").addedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").updatedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").oldItem.asInstanceOf[Bookmark].title should equal("Wikipedia")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").newItem.asInstanceOf[Bookmark].title should equal("Wikipedia")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C1").diffType should equal("update")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C1").addedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C1").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C1").updatedProperties should equal(Set("title"))
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C1").oldItem.asInstanceOf[Bookmark].title should equal("HCoder")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C1").newItem.asInstanceOf[Bookmark].title should equal("HCoder.org")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").diffType should equal("remove")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").addedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").removedProperties should equal(Set("created", "title", "uri"))
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").updatedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C3").diffType should equal("add")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C3").addedProperties should equal(Set("created", "title", "uri"))
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C3").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C3").updatedProperties should equal(Set())
  }

  it should "take into account folders and the elements inside them" in {
    api.serverProxy = new TestLinkServerProxy(fakeConsumer,
                                              fakeAccessToken,
                                              "simpleBookmarkFolders")
    val bms1 = api.getBookmarks()
    val bms2 = api.getBookmarks()
    val diff = new Diff
    val diffObjects = diff.calculateDiff(bms1, bms2)

    diffObjects.size should equal(4)
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").diffType should equal("identical")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").addedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C0").updatedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").diffType should equal("update")
    diffObjects("E22F0BF0524511E08C69D46C240865CA").addedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").removedProperties should equal(Set())
    diffObjects("E22F0BF0524511E08C69D46C240865CA").updatedProperties should equal(Set("title"))
    diffObjects("E22F0BF0524511E08C69D46C240865CA").oldItem.asInstanceOf[BookmarkFolder].title should equal("Folder")
    diffObjects("E22F0BF0524511E08C69D46C240865CA").newItem.asInstanceOf[BookmarkFolder].title should equal("My Folder")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C1").diffType should equal("identical")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C1").addedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C1").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C1").updatedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").diffType should equal("update")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").addedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").removedProperties should equal(Set())
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").updatedProperties should equal(Set("title", "parent"))
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").oldItem.asInstanceOf[Bookmark].title should equal("Second inside folder")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").oldParentId should equal(Some("E22F0BF0524511E08C69D46C240865CA"))
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").newItem.asInstanceOf[Bookmark].title should equal("Now outside folder")
    diffObjects("E22EE4E0524511E08C68A6DDBCB261C2").newParentId should equal(None)
  }
}
