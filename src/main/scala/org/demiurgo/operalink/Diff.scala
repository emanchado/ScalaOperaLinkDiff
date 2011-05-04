import org.demiurgo.operalink.LinkAPIItem
import scala.collection.mutable

package org.demiurgo.operalink {
  class LinkAPIItemDiff(val oldItem: LinkAPIItem) {
    var newItem: LinkAPIItem = _
    var addedProperties: Set[String] = Set()
    var removedProperties: Set[String] =
      if (oldItem == null) Set() else oldItem.propertyHash.keys.toSet
    var updatedProperties: Set[String] = Set()

    def diffAgainst(updatedItem: LinkAPIItem): LinkAPIItemDiff = {
      newItem = updatedItem
      val oldItemProperties = if (oldItem == null) Set[String]() else oldItem.propertyHash.keys.toSet
      val newItemProperties = if (newItem == null) Set[String]() else newItem.propertyHash.keys.toSet
      removedProperties = oldItemProperties.diff(newItemProperties)
      addedProperties   = newItemProperties.diff(oldItemProperties)
      updatedProperties = for (p <- (oldItemProperties & newItemProperties)
                             if oldItem.propertyHash(p) !=
                               newItem.propertyHash(p))
                            yield p
      return this
    }

    def diffType: String = {
      if (oldItem == null) {
        return "add"
      } else if (newItem == null) {
        return "remove"
      } else {
        return "update"
      }
    }
  }

  class Diff {
    def calculateDiff(srcItems: Seq[LinkAPIItem],
                      dstItems: Seq[LinkAPIItem]): Map[String, LinkAPIItemDiff] = {
      var map: mutable.Map[String, LinkAPIItemDiff] = mutable.Map()
      for (item <- srcItems) {
        map(item.id) = new LinkAPIItemDiff(item)
      }
      for (item <- dstItems) {
        if (map.contains(item.id)) {
          map(item.id).diffAgainst(item)
        } else {
          map(item.id) = new LinkAPIItemDiff(null).diffAgainst(item)
        }
      }
      return map.toMap
    }
  }
}
