/*******************************************************************************
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/abc15018045126/sora-editor
 *    Copyright (C) 2020-2024  abc15018045126
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

package io.github.abc15018045126.sora.lsp.client

import io.github.abc15018045126.sora.lsp.client.languageserver.requestmanager.RequestManager
import io.github.abc15018045126.sora.lsp.client.languageserver.wrapper.LanguageServerWrapper
import io.github.abc15018045126.sora.lsp.editor.LspEditor
import io.github.abc15018045126.sora.lsp.editor.LspProject
import io.github.abc15018045126.sora.lsp.utils.FileUri
import java.lang.ref.WeakReference


class ServerWrapperBaseClientContext(wrapper: LanguageServerWrapper) :
    ClientContext {
    override fun getEditor(documentUri: FileUri): LspEditor? {
        return project?.getEditor(documentUri)
    }

    private val projectRef = WeakReference(wrapper.project)
    private val requestManagerRef = WeakReference(wrapper.requestManager)

    override val projectPath = wrapper.project.projectUri
    override val project: LspProject?
        get() = projectRef.get()

    override val requestManager: RequestManager?
        get() = requestManagerRef.get()

    override val serverName = wrapper.serverName

    override val eventListener = wrapper.serverDefinition.eventListener

}
