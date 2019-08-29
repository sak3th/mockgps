package mockgps.core.scope

import android.util.Log
import mockgps.core.Provider

class Scope (
    val name: String,
    var provider: Provider? = null
) {

  var parent: Scope? = null

  private val children = ArrayList<Scope>()

  fun add(child: Scope) {
    child.parent = this
    children.add(child)
  }

  fun getChild(child: String) = children.firstOrNull { it.name == child }

  private fun remove(child: Scope) {
    children.remove(child)
    child.parent = null
  }

  fun finish() {
    parent?.remove(this)
  }

  fun treeout(level: Int = 0) {
    Log.d("devsak", "Level $level : ${parent?.name} -> $name")
    val next = level+1
    children.forEach {
      it.treeout(next)
    }
  }
}

