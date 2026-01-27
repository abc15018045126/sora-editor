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

package io.github.abc15018045126.sora.editor.ts

import android.os.Bundle
import io.github.abc15018045126.sora.lang.EmptyLanguage
import io.github.abc15018045126.sora.lang.Language
import io.github.abc15018045126.sora.lang.QuickQuoteHandler
import io.github.abc15018045126.sora.lang.completion.CompletionPublisher
import io.github.abc15018045126.sora.lang.format.Formatter
import io.github.abc15018045126.sora.lang.smartEnter.NewlineHandler
import io.github.abc15018045126.sora.text.CharPosition
import io.github.abc15018045126.sora.text.ContentReference
import io.github.abc15018045126.sora.widget.SymbolPairMatch

/**
 * Tree-sitter based language.
 *
 * @param languageSpec The language specification for parsing and highlighting
 * @param themeDescription Theme for colorizing nodes
 * @param tab whether tab should be used
 *
 * @see TsTheme
 * @see TsLanguageSpec
 *
 * @author abc15018045126
 */
open class TsLanguage(
    val languageSpec: TsLanguageSpec,
    val tab: Boolean = false,
    themeDescription: TsThemeBuilder.() -> Unit
) : Language {

    init {
        if (languageSpec.closed) {
            throw IllegalStateException("spec is closed")
        }
    }

    protected var tsTheme = TsThemeBuilder(languageSpec.tsQuery).apply { themeDescription() }.theme

    open val analyzer by lazy {
        TsAnalyzeManager(languageSpec, tsTheme)
    }

    /**
     * Update tree-sitter colorizing theme with the given description
     */
    fun updateTheme(themeDescription: TsThemeBuilder.() -> Unit) = languageSpec.let {
        if (it.closed) {
            throw IllegalStateException("spec is closed")
        }
        updateTheme(TsThemeBuilder(languageSpec.tsQuery).apply { themeDescription() }.theme)
    }

    /**
     * Update tree-sitter colorizing theme
     */
    fun updateTheme(theme: TsTheme) {
        this.tsTheme = theme
        analyzer.updateTheme(theme)
    }

    override fun getAnalyzeManager() = analyzer

    override fun getInterruptionLevel() = Language.INTERRUPTION_LEVEL_STRONG

    override fun requireAutoComplete(
        content: ContentReference,
        position: CharPosition,
        publisher: CompletionPublisher,
        extraArguments: Bundle
    ) {
        // Nothing
    }

    override fun getIndentAdvance(content: ContentReference, line: Int, column: Int) = 0

    override fun useTab() = tab

    override fun getFormatter(): Formatter = EmptyLanguage.EmptyFormatter.INSTANCE

    override fun getSymbolPairs(): SymbolPairMatch = EmptyLanguage.EMPTY_SYMBOL_PAIRS

    override fun getNewlineHandlers() = emptyArray<NewlineHandler>()

    override fun getQuickQuoteHandler() = null

    override fun destroy() {
        languageSpec.close()
    }

}
