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

package io.github.abc15018045126.sora.lsp.events.format

import io.github.abc15018045126.sora.lsp.editor.LspEditor
import io.github.abc15018045126.sora.lsp.editor.getOption
import io.github.abc15018045126.sora.lsp.events.AsyncEventListener
import io.github.abc15018045126.sora.lsp.events.EventContext
import io.github.abc15018045126.sora.lsp.events.EventType
import io.github.abc15018045126.sora.lsp.events.document.applyEdits
import io.github.abc15018045126.sora.lsp.events.getByClass
import io.github.abc15018045126.sora.lsp.requests.Timeout
import io.github.abc15018045126.sora.lsp.requests.Timeouts
import io.github.abc15018045126.sora.lsp.utils.LSPException
import io.github.abc15018045126.sora.lsp.utils.createTextDocumentIdentifier
import io.github.abc15018045126.sora.text.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.eclipse.lsp4j.DocumentFormattingParams
import org.eclipse.lsp4j.FormattingOptions
import org.eclipse.lsp4j.TextEdit


class FullFormattingEvent : AsyncEventListener() {
    override val eventName = EventType.fullFormatting

    override suspend fun handleAsync(context: EventContext) {
        val editor = context.get<LspEditor>("lsp-editor")

        val content = context.getByClass<Content>() ?: return

        val requestManager = editor.requestManager ?: return

        val formattingParams = DocumentFormattingParams()

        formattingParams.options = editor.eventManager.getOption<FormattingOptions>()

        formattingParams.textDocument =
            editor.uri.createTextDocumentIdentifier()

        val formattingFuture = requestManager.formatting(formattingParams) ?: return

        try {
            val textEditList: List<TextEdit>

            withTimeout(Timeout[Timeouts.FORMATTING].toLong()) {
                textEditList = formattingFuture.await() ?: listOf()
            }

            withContext(Dispatchers.Main) {
                editor.eventManager.emit(EventType.applyEdits) {
                    put("edits", textEditList)
                    put("content", content)
                }
            }

        } catch (exception: Exception) {
            throw LSPException("Formatting code timeout", exception)
        }
    }

}

val EventType.fullFormatting: String
    get() = "textDocument/formatting"
