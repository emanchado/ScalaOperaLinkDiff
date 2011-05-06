import org.demiurgo.operalink.LinkAPIItem
import scala.collection.mutable

package org.demiurgo.operalink {
  class LinkAPIItemDiff(val oldItem: LinkAPIItem,
                        val oldParentId: Option[String]) {
    var newItem: LinkAPIItem = _
    var newParentId: Option[String] = _
    var addedProperties: Set[String] = Set()
    var removedProperties: Set[String] =
      if (oldItem == null) Set() else oldItem.propertyHash.keys.toSet
    var updatedProperties: Set[String] = Set()

    def diffAgainst(updatedItem: LinkAPIItem,
                    updatedParentId: Option[String]): LinkAPIItemDiff = {
      newItem = updatedItem
      newParentId = updatedParentId
      val oldItemProperties = if (oldItem == null) Set[String]() else oldItem.propertyHash.keys.toSet
      val newItemProperties = if (newItem == null) Set[String]() else newItem.propertyHash.keys.toSet
      removedProperties = oldItemProperties.diff(newItemProperties)
      addedProperties   = newItemProperties.diff(oldItemProperties)
      updatedProperties = for (p <- (oldItemProperties & newItemProperties)
                             if oldItem.propertyHash(p) !=
                               newItem.propertyHash(p))
                            yield p
      if (oldParentId != updatedParentId) {
        updatedProperties = updatedProperties ++ List("parent")
      }
      return this
    }

    def diffType: String = {
      if (oldItem == null) {
        return "add"
      } else if (newItem == null) {
        return "remove"
      } else if (removedProperties.size == 0 &&
                 addedProperties.size   == 0 &&
                 updatedProperties.size == 0) {
        return "identical"
      } else {
        return "update"
      }
    }
  }

  class Diff {
    protected def flattenItems(items: Seq[LinkAPIItem],
                               parentId: Option[String] = None): Seq[Pair[LinkAPIItem, Option[String]]] = {
      var returnItems: Seq[Pair[LinkAPIItem, Option[String]]] = Seq()
      for (i <- items) {
        val extraItems = i.itemType match {
          case "bookmark_folder" =>
            flattenItems(i.asInstanceOf[BookmarkFolder].contents, Some(i.id))
          case "note_folder" =>
            flattenItems(i.asInstanceOf[NoteFolder].contents, Some(i.id))
          case _ =>
            Seq()
        }
        returnItems = returnItems ++ Seq(Pair(i, parentId)) ++ extraItems
      }
      return returnItems
    }

    def calculateDiff(srcItems: Seq[LinkAPIItem],
                      dstItems: Seq[LinkAPIItem]): Map[String, LinkAPIItemDiff] = {
      var map: mutable.Map[String, LinkAPIItemDiff] = mutable.Map()
      val realSrcItems = flattenItems(srcItems)
      val realDstItems = flattenItems(dstItems)
      for (pair <- realSrcItems) {
        pair match {
          case (item, parentId) =>
            map(item.id) = new LinkAPIItemDiff(item, parentId)
        }
      }
      for (pair <- realDstItems) {
        pair match {
          case (item, parentId) =>
            if (map.contains(item.id)) {
              map(item.id).diffAgainst(item, parentId)
            } else {
              map(item.id) = new LinkAPIItemDiff(null, None).diffAgainst(item, parentId)
            }
        }
      }
      return map.toMap
    }
  }
}
