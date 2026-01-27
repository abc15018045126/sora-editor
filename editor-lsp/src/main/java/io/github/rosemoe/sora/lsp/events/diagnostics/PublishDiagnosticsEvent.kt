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

package io.github.abc15018045126.sora.lsp.events.diagnostics

import io.github.abc15018045126.sora.lang.diagnostic.DiagnosticsContainer
import io.github.abc15018045126.sora.lsp.editor.LspEditor
import io.github.abc15018045126.sora.lsp.events.EventContext
import io.github.abc15018045126.sora.lsp.events.EventListener
import io.github.abc15018045126.sora.lsp.events.EventType
import io.github.abc15018045126.sora.lsp.utils.transformToEditorDiagnostics
import io.github.abc15018045126.sora.widget.component.EditorDiagnosticTooltipWindow
import io.github.abc15018045126.sora.widget.getComponent
import org.eclipse.lsp4j.Diagnostic


class PublishDiagnosticsEvent : EventListener {
    override val eventName: String = EventType.publishDiagnostics

    override fun handle(context: EventContext) {
        val lspEditor = context.get<LspEditor>("lsp-editor")
        val originEditor = lspEditor.editor ?: return
        val data = context.getOrNull<List<Diagnostic>>("data") ?: return

        val diagnosticsContainer =
            originEditor.diagnostics ?: DiagnosticsContainer()

        diagnosticsContainer.reset()

        diagnosticsContainer.addDiagnostics(
            data.transformToEditorDiagnostics(originEditor)
        )

        // run on ui thread
        originEditor.postOnAnimation {
            if (data.isEmpty()) {
                originEditor.diagnostics = null
                originEditor.getComponent<EditorDiagnosticTooltipWindow>().dismiss()
                return@postOnAnimation
            }
            originEditor.diagnostics = diagnosticsContainer
        }
    }


}

val EventType.publishDiagnostics: String
    get() = "editor/publishDiagnostics"
