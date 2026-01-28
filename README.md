<div align="center">

![Banner](/images/editor_banner.jpg)
----
[![CI](https://github.com/abc15018045126/sora-editor/actions/workflows/gradle.yml/badge.svg?event=push)](https://github.com/abc15018045126/sora-editor/actions/workflows/gradle.yml)
[![GitHub license](https://img.shields.io/github/license/abc15018045126/sora-editor)](https://github.com/abc15018045126/sora-editor/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.abc15018045126/editor.svg?label=Maven%20Central)]((https://search.maven.org/search?q=io.github.abc15018045126%20editor))   
[![Telegram](https://img.shields.io/badge/Join-Telegram-blue)](https://t.me/abc15018045126_code_editor)
[![QQ](https://img.shields.io/badge/Join-QQ_Group-ff69b4)](https://jq.qq.com/?_wv=1027&k=n68uxQws)

sora-editor is a cool and optimized code editor on Android platform

</div>

Read this in other languages: [English](README.md), [简体中文](README.zh-cn.md), [Español](README.es.md), [日本語](README.jp.md).

Note that statements in other languages may not be up-to-date.

***Work In Progress*** This project is still developing slowly.   

**Issues and pull requests are welcome.**

## ⚠️ Independent Development & Major Changes

This repository is now being developed independently from the original author [Rosemoe/sora-editor](https://github.com/Rosemoe/sora-editor). Significant changes have been made to the underlying architecture and package structure.

**Major Changes:**
*   **Package Renaming**: To avoid confusion and facilitate independent distribution, the package name has been globally changed to `io.github.abc15018045126.sora`.
*   **Sync Advisory**: Due to massive changes in package names and file paths, **DO NOT sync with the upstream repository via direct Git pull/merge**. This will lead to unresolvable conflicts. If you wish to incorporate upstream features, manual porting of logic is highly recommended.
*   **How to Migrate/Revert**: If you want to use features from the original project while keeping our changes, you will need to manually handle package mapping or cherry-pick the logic from this repository into the original package structure.

**New Features (as of v0.0.2):**
*   **Independent Wrap Line Spacing**: Use `wrapLineSpacingMultiplier` and `wrapLineSpacingExtra` to configure line spacing for wrapped lines independently from regular line spacing.
*   **Current Line Highlighting**: Toggle current line background highlighting and customize its color (including transparent support).
*   **Highly Customizable Cursor (Caret)**:
    *   Custom cursor color and width.
    *   Custom selection handle styles: Side Drop, Center Drop, or **completely hide handles** (classic caret only).

## Features

- [x] Incremental syntax highlight
- [x] Auto-completion (with [code snippets](https://macromates.com/manual/en/snippets))
- [x] Auto indent
- [x] Code block lines
- [x] Scale text
- [x] Undo/redo
- [x] Search and replace
- [x] Auto wordwrap
- [x] Show non-printable characters
- [x] Diagnostic markers
- [x] Text magnifier
- [x] Sticky Scroll
- [x] Highlight bracket pairs
- [x] Event System
- [x] TextMate and TreeSitter support

## Documentation

To quickly get started, please view
this [Quickstart Guide](https://project-sora.github.io/sora-editor-docs/guide/getting-started).

To check out all docs, please
visit [Documentation Site](https://project-sora.github.io/sora-editor-docs/).

* [Editor Overview](https://project-sora.github.io/sora-editor-docs/guide/editor-overview)
* [Reference](https://project-sora.github.io/sora-editor-docs/reference/xml-attributes)
* [Documentation Repository](https://github.com/project-sora/sora-editor-docs)

## Screenshots

<div style="overflow: hidden">
<img src="/images/general.jpg" alt="GeneralAppearance" width="40%" align="bottom" />
<img src="/images/problem_indicators.jpg" alt="ProblemIndicator" width="40%" align="bottom" />
</div>

## Discuss

* Official QQ Group:[734652304](https://qm.qq.com/q/kKBqRsVrQ4)
* Official [Telegram Group](https://t.me/abc15018045126_code_editor)

## Contributors

<a href="https://github.com/abc15018045126/sora-editor/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=abc15018045126/sora-editor" />
</a>

## License

```
sora-editor - the awesome code editor for Android
https://github.com/abc15018045126/sora-editor
Copyright (C) 2020-2026  abc15018045126

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
USA

Please contact abc15018045126 by email 2073412493@qq.com if you need
additional information or have any questions
```

## Acknowledgements

Thanks to [JetBrains](https://www.jetbrains.com/?from=CodeEditor) for allocating free open-source
licences for IDEs such as [IntelliJ IDEA](https://www.jetbrains.com/idea/?from=CodeEditor).   
[<img src=".github/jetbrains-variant-3.png" width="200"/>](https://www.jetbrains.com/?from=CodeEditor)


