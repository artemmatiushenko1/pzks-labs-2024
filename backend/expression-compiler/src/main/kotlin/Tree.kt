package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class TreeNode(val value: String?) {
    val children: MutableList<TreeNode> = mutableListOf()

    /**
     * Adds node to children list.
     * @return this.
     */
    fun addNode(node: TreeNode): TreeNode {
        children.add(node)
        return this
    }

    override fun toString(): String {
        return Json.encodeToString(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other is TreeNode) {
            return this.value == other.value && this.children == other.children
        }

        return false
    }

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + children.hashCode()
        return result
    }
}
