/*******************************************************************************
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/abc15018045126/sora-editor
 *    Copyright (C) 2020-2025  abc15018045126
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

package io.github.abc15018045126.sora.lsp.editor.event

import io.github.abc15018045126.sora.event.EventReceiver
import io.github.abc15018045126.sora.event.SelectionChangeEvent
import io.github.abc15018045126.sora.event.Unsubscribe
import io.github.abc15018045126.sora.lsp.editor.LspEditor
import io.github.abc15018045126.sora.lsp.events.EventType
import io.github.abc15018045126.sora.lsp.events.highlight.DocumentHighlightEvent
import io.github.abc15018045126.sora.lsp.events.highlight.documentHighlight
import io.github.abc15018045126.sora.lsp.events.hover.hover
import io.github.abc15018045126.sora.widget.component.EditorAutoCompletion
import io.github.abc15018045126.sora.widget.getComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LspEditorSelectionChangeEvent(private val editor: LspEditor) :
    EventReceiver<SelectionChangeEvent> {
    override fun onReceive(event: SelectionChangeEvent, unsubscribe: Unsubscribe) {
        if (!editor.isConnected) {
            return
        }

        editor.showSignatureHelp(null)
        editor.showHover(null)

        editor.coroutineScope.launch(Dispatchers.IO) {
            editor.eventManager.emitAsync(EventType.documentHighlight) {
                put(
                    DocumentHighlightEvent.DocumentHighlightRequest(
                        event.left.fromThis()
                    )
                )
            }
        }

        val originEditor = editor.editor ?: return

        val hoverWindow = editor.hoverWindow ?: return

        val isInCompletion = originEditor.getComponent<EditorAutoCompletion>().isShowing

        if ((!originEditor.hasMouseHovering() && (!hoverWindow.alwaysShowOnTouchHover || event.isSelected)) || isInCompletion) {
            return
        }

        editor.coroutineScope.launch(Dispatchers.IO) {
            editor.eventManager.emitAsync(EventType.hover, event.left)
        }
    }
}

