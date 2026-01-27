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
import io.github.abc15018045126.sora.event.ScrollEvent
import io.github.abc15018045126.sora.event.Unsubscribe
import io.github.abc15018045126.sora.lsp.editor.LspEditor
import io.github.abc15018045126.sora.lsp.editor.requestInlayHint
import io.github.abc15018045126.sora.text.CharPosition
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class LspEditorScrollEvent(private val editor: LspEditor) :
    EventReceiver<ScrollEvent> {

    override fun onReceive(event: ScrollEvent, unsubscribe: Unsubscribe) {
        if (!editor.isConnected || editor.isEnableInlayHint) {
            return
        }

        val firstVisibleLine = event.editor.firstVisibleLine

        editor.coroutineScope.launch {
            // request inlay hint
            editor.requestInlayHint(
                CharPosition(firstVisibleLine, 0)
            )
        }

    }
}
