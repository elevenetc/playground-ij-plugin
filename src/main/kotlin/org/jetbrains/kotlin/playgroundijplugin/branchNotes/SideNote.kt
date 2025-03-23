package org.jetbrains.kotlin.playgroundijplugin.branchNotes

data class SideNote(
    val note: String,
    val key: Key
) {
    data class Key(val value: String)
}

data class Branch(val name: String)