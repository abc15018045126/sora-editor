/*
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
 */
package io.github.abc15018045126.sora.langs.java;

import static io.github.abc15018045126.sora.lang.styling.StylesUtils.checkNoCompletion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.abc15018045126.sora.lang.QuickQuoteHandler;
import io.github.abc15018045126.sora.lang.styling.Styles;
import io.github.abc15018045126.sora.text.Content;
import io.github.abc15018045126.sora.text.TextRange;

public class JavaQuoteHandler implements QuickQuoteHandler {

    @NonNull
    @Override
    public HandleResult onHandleTyping(@NonNull String candidateCharacter, @NonNull Content text, @NonNull TextRange cursor, @Nullable Styles style) {
        if (!checkNoCompletion(style, cursor.getStart()) && !checkNoCompletion(style, cursor.getEnd()) && "\"".equals(candidateCharacter) && cursor.getStart().line == cursor.getEnd().line) {
            text.insert(cursor.getStart().line, cursor.getStart().column, "\"");
            text.insert(cursor.getEnd().line, cursor.getEnd().column + 1, "\"");
            return new HandleResult(true, new TextRange(text.getIndexer().getCharPosition(cursor.getStartIndex() + 1), text.getIndexer().getCharPosition(cursor.getEndIndex() + 1)));
        }
        return HandleResult.NOT_CONSUMED;
    }

}

