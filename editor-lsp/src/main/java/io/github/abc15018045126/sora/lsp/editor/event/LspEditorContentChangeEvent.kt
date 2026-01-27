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

package io.github.abc15018045126.sora.lsp.editor.event

import io.github.abc15018045126.sora.event.ContentChangeEvent
import io.github.abc15018045126.sora.event.EventReceiver
import io.github.abc15018045126.sora.event.Unsubscribe
import io.github.abc15018045126.sora.lsp.editor.LspEditor
import io.github.abc15018045126.sora.lsp.editor.requestDocumentColor
import io.github.abc15018045126.sora.lsp.editor.requestInlayHint
import io.github.abc15018045126.sora.lsp.events.EventType
import io.github.abc15018045126.sora.lsp.events.diagnostics.queryDocumentDiagnostics
import io.github.abc15018045126.sora.lsp.events.document.documentChange
import io.github.abc15018045126.sora.lsp.events.highlight.DocumentHighlightEvent
import io.github.abc15018045126.sora.lsp.events.highlight.documentHighlight
import io.github.abc15018045126.sora.lsp.events.hover.hover
import io.github.abc15018045126.sora.lsp.events.inlayhint.inlayHint
import io.github.abc15018045126.sora.lsp.events.signature.signatureHelp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.lsp4j.DocumentDiagnosticReport

private const val DIAGNOSTIC_QUERY_SOURCE = "sora.lsp.query"


class LspEditorContentChangeEvent(private val editor: LspEditor) :
    EventReceiver<ContentChangeEvent> {
    override fun onReceive(event: ContentChangeEvent, unsubscribe: Unsubscribe) {
        if (!editor.isConnected) {
            return
        }


        editor.coroutineScope.launch(Dispatchers.IO) {
            // send to server
            editor.eventManager.emitAsync(EventType.documentChange, event)

            if (editor.hitReTrigger(event.changedText)) {
                editor.showSignatureHelp(null)
            } else {
                editor.eventManager.emitAsync(EventType.signatureHelp, event.changeStart)
            }

            editor.eventManager.emitAsync(EventType.hover, event.changeStart)

            editor.eventManager.emitAsync(EventType.documentHighlight) {
                put(
                    DocumentHighlightEvent.DocumentHighlightRequest(event.changeStart)
                )
            }

            // request inlay hint
            editor.requestInlayHint(event.changeStart)
            editor.requestDocumentColor()

            val diagnostics =
                editor.eventManager.emitAsync(EventType.queryDocumentDiagnostics)
                    .getOrNull<DocumentDiagnosticReport>("diagnostics") ?: return@launch

            if (diagnostics.isRelatedUnchangedDocumentDiagnosticReport) {
                // no-op
                return@launch
            }

            if (diagnostics.isRelatedFullDocumentDiagnosticReport) {
                val diagnosticsContainer = editor.project.diagnosticsContainer
                val fileUri = editor.uri

                diagnosticsContainer.clearDiagnostics(fileUri)
                diagnosticsContainer.addDiagnostics(
                    fileUri,
                    diagnostics.left.items
                )

                editor.onDiagnosticsUpdate()
            }
        }


    }
}


