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

package io.github.abc15018045126.sora.lsp.editor

import androidx.annotation.WorkerThread
import io.github.abc15018045126.sora.annotations.Experimental
import io.github.abc15018045126.sora.event.ContentChangeEvent
import io.github.abc15018045126.sora.event.Event
import io.github.abc15018045126.sora.event.HoverEvent
import io.github.abc15018045126.sora.event.ScrollEvent
import io.github.abc15018045126.sora.event.SelectionChangeEvent
import io.github.abc15018045126.sora.event.SubscriptionReceipt
import io.github.abc15018045126.sora.lang.Language
import io.github.abc15018045126.sora.lsp.client.languageserver.requestmanager.RequestManager
import io.github.abc15018045126.sora.lsp.client.languageserver.serverdefinition.LanguageServerDefinition
import io.github.abc15018045126.sora.lsp.client.languageserver.wrapper.LanguageServerWrapper
import io.github.abc15018045126.sora.lsp.editor.event.LspEditorContentChangeEvent
import io.github.abc15018045126.sora.lsp.editor.event.LspEditorHoverEvent
import io.github.abc15018045126.sora.lsp.editor.event.LspEditorScrollEvent
import io.github.abc15018045126.sora.lsp.editor.event.LspEditorSelectionChangeEvent
import io.github.abc15018045126.sora.lsp.editor.format.LspFormatter
import io.github.abc15018045126.sora.lsp.events.EventType
import io.github.abc15018045126.sora.lsp.events.diagnostics.publishDiagnostics
import io.github.abc15018045126.sora.lsp.events.document.documentClose
import io.github.abc15018045126.sora.lsp.events.document.documentOpen
import io.github.abc15018045126.sora.lsp.events.document.documentSave
import io.github.abc15018045126.sora.lsp.requests.Timeout
import io.github.abc15018045126.sora.lsp.requests.Timeouts
import io.github.abc15018045126.sora.lsp.utils.FileUri
import io.github.abc15018045126.sora.lsp.utils.clearVersions
import io.github.abc15018045126.sora.text.CharPosition
import io.github.abc15018045126.sora.widget.CodeEditor
import io.github.abc15018045126.sora.widget.subscribeEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.eclipse.lsp4j.CodeAction
import org.eclipse.lsp4j.ColorInformation
import org.eclipse.lsp4j.Command
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.DocumentHighlight
import org.eclipse.lsp4j.DocumentHighlightKind
import org.eclipse.lsp4j.Hover
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.SignatureHelp
import org.eclipse.lsp4j.TextDocumentSyncKind
import org.eclipse.lsp4j.jsonrpc.messages.Either
import java.lang.ref.WeakReference
import java.util.concurrent.TimeoutException

class LspEditor(
    val project: LspProject,
    val uri: FileUri,
) {

    private val serverDefinition: LanguageServerDefinition

    private val delegate = LspEditorDelegate(this)
    private val uiDelegate = LspEditorUIDelegate(this)

    private var _currentEditor: WeakReference<CodeEditor?> = WeakReference(null)

    private var currentLanguage: LspLanguage? = null

    private var subscriptionReceipts: MutableList<SubscriptionReceipt<out Event>> = mutableListOf()

    @Volatile
    private var isClosed = false

    private val disposeLock = Any()

    val eventManager = LspEventManager(project, this)

    val fileExt = uri.path.substringAfterLast('.')

    var textDocumentSyncKind = TextDocumentSyncKind.Incremental

    var completionTriggers = mutableSetOf<String>()

    var signatureHelpTriggers = mutableSetOf<String>()

    var signatureHelpReTriggers = mutableSetOf<String>()

    val coroutineScope = project.coroutineScope

    var editor: CodeEditor?
        set(currentEditor) {
            if (currentEditor == null) {
                throw IllegalArgumentException("Editor cannot be null")
            }

            uiDelegate.detachEditor()
            _currentEditor = WeakReference(currentEditor)

            clearSubscriptions()

            currentEditor.setEditorLanguage(currentLanguage)
            uiDelegate.attachEditor(currentEditor)

            if (isEnableInlayHint) {
                coroutineScope.launch {
                    this@LspEditor.requestInlayHint(CharPosition(0, 0))
                    this@LspEditor.requestDocumentColor()
                }
            }

            clearSubscriptions()
            subscriptionReceipts =
                mutableListOf(
                    currentEditor.subscribeEvent<ContentChangeEvent>(
                        LspEditorContentChangeEvent(this)
                    ),
                    currentEditor.subscribeEvent<SelectionChangeEvent>(
                        LspEditorSelectionChangeEvent(this)
                    ),
                    currentEditor.subscribeEvent<HoverEvent>(
                        LspEditorHoverEvent(this)
                    ),
                    currentEditor.subscribeEvent<ScrollEvent>(
                        LspEditorScrollEvent(this)
                    )
                )
        }
        get() {
            return _currentEditor.get()
        }

    var editorContent: String
        get() = editor?.text?.toString() ?: ""
        set(content) {
            editor?.setText(content)
        }

    var wrapperLanguage: Language? = null
        set(language) {
            field = language
            currentLanguage?.wrapperLanguage = wrapperLanguage
            val editor = _currentEditor.get()
            if (editor != null) {
                this.editor = editor
            }
        }

    var isConnected = false
        private set
    
    val languageServerWrapper: LanguageServerWrapper
        get() = delegate.getPrimaryWrapper()
            ?: throw IllegalStateException("No language server wrapper for extension $fileExt")

    var diagnostics
        get() = project.diagnosticsContainer.getDiagnostics(uri)
        set(value) {
            publishDiagnostics(value)
        }

    val diagnosticsContainer
        get() = project.diagnosticsContainer

    val isShowSignatureHelp
        get() = uiDelegate.isShowSignatureHelp

    val isShowHover
        get() = uiDelegate.isShowHover

    val isShowCodeActions
        get() = uiDelegate.isShowCodeActions

    var isEnableHover: Boolean
        get() = uiDelegate.isEnableHover
        set(value) {
            uiDelegate.isEnableHover = value
        }

    var isEnableSignatureHelp: Boolean
        get() = uiDelegate.isEnableSignatureHelp
        set(value) {
            uiDelegate.isEnableSignatureHelp = value
        }

    @get:Experimental
    @set:Experimental
    var isEnableInlayHint: Boolean
        get() = uiDelegate.isEnableInlayHint
        set(value) {
            uiDelegate.isEnableInlayHint = value
            if (value) {
                coroutineScope.launch {
                    this@LspEditor.requestInlayHint(CharPosition(0, 0))
                    this@LspEditor.requestDocumentColor()
                }
            }
        }

    val hoverWindow
        get() = uiDelegate.hoverWindow

    val codeActionWindow
        get() = uiDelegate.codeActionWindow

    val signatureHelpWindow
        get() = uiDelegate.signatureHelpWindow

    val requestManager
        get() = delegate.aggregatedRequestManager

    val requestManagers: List<RequestManager>
        get() = delegate.aggregatedRequestManager.activeManagers

    init {
        serverDefinition = project.getServerDefinition(fileExt)
            ?: project.getServerDefinitions(fileExt).firstOrNull()
            ?: throw Exception("No server definition for extension $fileExt")
        currentLanguage = LspLanguage(this)
    }


    /**
     * Connect to the language server to provide the capabilities, this will cause threads blocking. Note: An error will be thrown if the language server is not connected after some time.
     *
     * @see io.github.abc15018045126.sora.lsp.requests.Timeouts
     *
     * @see io.github.abc15018045126.sora.lsp.requests.Timeout
     */

    @Throws(TimeoutException::class)
    suspend fun connect(throwException: Boolean = true): Boolean = withContext(Dispatchers.IO) {
        eventManager.init()
        runCatching {
            // Delegate handles multi-server coordination and returns merged capabilities.
            val capabilities = delegate.connectAll()
                ?: throw TimeoutException("Unable to connect language server")

            openDocument()

            currentLanguage?.let { language ->
                if (capabilities.documentFormattingProvider?.left != false || capabilities.documentFormattingProvider?.right != null) {
                    language.formatter = LspFormatter(language)
                }
            }

            if (capabilities.inlayHintProvider?.left != false || capabilities.inlayHintProvider?.right != null) {
                requestInlayHint(CharPosition(0, 0))
            }

            isConnected = true
        }.onFailure {
            if (throwException) {
                throw it
            }
            isConnected = false
        }.isSuccess
    }

    @WorkerThread
    fun connectBlocking(throwException: Boolean = true): Boolean = runBlocking {
        connect(throwException)
    }

    /**
     * Try to connect to the language server repeatedly, this will cause threads blocking. Note: An error will be thrown if the language server is not connected after some time.
     *
     * @see io.github.abc15018045126.sora.lsp.requests.Timeouts
     *
     * @see io.github.abc15018045126.sora.lsp.requests.Timeout
     */
    @Throws(InterruptedException::class, TimeoutException::class)
    suspend fun connectWithTimeout() {
        var isConnected = false

        var start = System.currentTimeMillis()
        val retryTime = Timeout[Timeouts.INIT]
        val maxRetryTime: Long = start + retryTime

        while (start < maxRetryTime) {
            try {
                connect()
                isConnected = true
                break
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace();
            }
            start = System.currentTimeMillis()
            delay((retryTime / 200).toLong())
        }

        if (!isConnected && start > maxRetryTime) {
            throw TimeoutException("Unable to connect language server")
        } else if (!isConnected) {
            connect()
        }

    }

    @WorkerThread
    fun connectWithTimeoutBlocking() = runBlocking {
        connectWithTimeout()
    }

    /**
     * disconnect to the language server
     */
    @WorkerThread
    @Throws(RuntimeException::class)
    fun disconnect() {
        runCatching {
            coroutineScope.future {
                eventManager.emitAsync(EventType.documentClose)
            }.get()

            delegate.disconnectAll()

            isConnected = false
        }.onFailure {
            isConnected = false
            delegate.disconnectAll()
            throw it
        }
    }

    /**
     * Notify the language server to open the document
     */
    suspend fun openDocument() {
        eventManager.emitAsync(EventType.documentOpen)
    }

    @WorkerThread
    fun openDocumentBlocking() = runBlocking {
        openDocument()
    }

    /**
     * Notify language servers the document is saved
     */
    suspend fun saveDocument() {
        eventManager.emitAsync(EventType.documentSave)
    }

    @WorkerThread
    fun saveDocumentBlocking() = runBlocking {
        saveDocument()
    }

    fun onDiagnosticsUpdate() {
        publishDiagnostics(diagnostics)
    }

    private fun publishDiagnostics(diagnostics: List<Diagnostic>) {
        eventManager.emit(EventType.publishDiagnostics) {
            put("data", diagnostics)
        }
    }

    fun showSignatureHelp(signatureHelp: SignatureHelp?) {
        uiDelegate.showSignatureHelp(signatureHelp)
    }

    fun showHover(hover: Hover?) {
        uiDelegate.showHover(hover)
    }

    fun showCodeActions(range: Range?, actions: List<Either<Command, CodeAction>>?) {
        uiDelegate.showCodeActions(range, actions)
    }

    fun showDocumentHighlight(highlights: List<DocumentHighlight>?) {
        uiDelegate.showDocumentHighlight(highlights)
    }

    internal fun showInlayHints(inlayHints: List<org.eclipse.lsp4j.InlayHint>?) {
        uiDelegate.showInlayHints(inlayHints)
    }

    internal fun showDocumentColors(documentColors: List<ColorInformation>?) {
        uiDelegate.showDocumentColors(documentColors)
    }


    fun hitReTrigger(eventText: CharSequence): Boolean {
        for (trigger in signatureHelpReTriggers) {
            if (trigger.contains(eventText)) {
                return true
            }
        }
        return false
    }

    fun hitTrigger(eventText: CharSequence): Boolean {
        for (trigger in signatureHelpTriggers) {
            if (trigger.contains(eventText)) {
                return true
            }
        }
        return false
    }

    private fun clearSubscriptions() {
        val iterator = subscriptionReceipts.iterator()

        while (iterator.hasNext()) {
            iterator.next().unsubscribe()
            iterator.remove()
        }
    }

    @WorkerThread
    fun dispose() {
        clearSubscriptions()
        synchronized(disposeLock) {
            if (isClosed) {
                return
                // throw IllegalStateException("Editor is already closed")
            }
            disconnect()
            uiDelegate.detachEditor()
            _currentEditor.clear()
            clearVersions {
                it == this.uri
            }
            project.removeEditor(this)
            isClosed = true
        }
    }

    suspend fun disposeAsync() = withContext(Dispatchers.IO) {
        dispose()
    }
}

