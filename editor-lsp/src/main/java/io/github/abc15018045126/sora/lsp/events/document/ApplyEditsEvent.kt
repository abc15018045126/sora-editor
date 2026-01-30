/*******************************************************************************
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/abc15018045126/sora-editor
 *    Copyright (C) 2020-2023  abc15018045126
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact abc15018045126 by email 2073412493@qq.com if you need
 *     additional information or have any questions
 ******************************************************************************/

package io.github.abc15018045126.sora.lsp.events.document

import io.github.abc15018045126.sora.lsp.events.EventContext
import io.github.abc15018045126.sora.lsp.events.EventListener
import io.github.abc15018045126.sora.lsp.events.EventType
import io.github.abc15018045126.sora.lsp.events.getByClass
import io.github.abc15018045126.sora.text.Content
import io.github.abc15018045126.sora.text.batchEdit
import io.github.abc15018045126.sora.util.Logger
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.TextEdit


class ApplyEditsEvent : EventListener {
    override val eventName: String = EventType.applyEdits

    override fun handle(context: EventContext) {
        val editList: List<TextEdit> = context.get("edits")
        val content = context.getByClass<Content>() ?: return

        if (editList.isEmpty()) {
            return
        }

        val sortedEdits = editList
            .withIndex()
            .sortedWith { a, b ->
                // Apply from the end so earlier replacements don't shift later ranges
                compareBy<IndexedValue<TextEdit>> { it.value.range.end.line }
                    .thenBy { it.value.range.end.character }
                    .thenBy { it.value.range.start.line }
                    .thenBy { it.value.range.start.character }
                    .thenBy { it.index }
                    .compare(b, a)
            }

        content.batchEdit {
            sortedEdits.forEach { indexedEdit ->
                val range = indexedEdit.value.range
                val (startIndex, endIndex) = calculateIndices(it, range)
                it.replace(startIndex, endIndex, indexedEdit.value.newText)
            }
        }
    }

    fun calculateIndices(content: Content, range: Range): Pair<Int, Int> {
        var startIndex = content.getCharIndex(range.start.line, range.start.character)
        val endLine = range.end.line.coerceAtMost(content.lineCount - 1)
        var endIndex = content.getCharIndex(endLine, range.end.character)

        if (endIndex < startIndex) {
            Logger.instance(this.javaClass.name).w(
                "Invalid location information found applying edits from %s to %s",
                range.start,
                range.end
            )
            val diff = startIndex - endIndex
            endIndex = startIndex
            startIndex = endIndex - diff
        }

        return startIndex to endIndex
    }

}

val EventType.applyEdits: String
    get() = "textDocument/applyEdits"

