<div align="center">

![Banner](/images/editor_banner.jpg)
----
[![CI](https://github.com/abc15018045126/sora-editor/actions/workflows/gradle.yml/badge.svg?event=push)](https://github.com/abc15018045126/sora-editor/actions/workflows/gradle.yml)
[![GitHub license](https://img.shields.io/github/license/abc15018045126/sora-editor)](https://github.com/abc15018045126/sora-editor/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.abc15018045126/editor.svg?label=Maven%20Central)]((https://search.maven.org/search?q=io.github.abc15018045126%20editor))   
[![Telegram](https://img.shields.io/badge/Join-Telegram-blue)](https://t.me/abc15018045126_code_editor)
[![QQ](https://img.shields.io/badge/Join-QQ_Group-ff69b4)](https://jq.qq.com/?_wv=1027&k=n68uxQws)

sora-editor是一款高效的安卓代码编辑器

</div>

阅读其他语言的文档: [English](README.md), [简体中文](README.zh-cn.md), [Español](README.es.md), [日本語](README.jp.md).

***这个项目仍在缓慢开发中，欢迎提交问题和合并请求***

## ⚠️ 独立开发与重大更改说明

本仓库已从原作者 [Rosemoe/sora-editor](https://github.com/Rosemoe/sora-editor) 独立开发，并进行了大量底层架构与包名的更改。

**重大更改：**
*   **包名更换**：为了避免与原项目混淆并方便独立发布，本项目包名已全局更名为 `io.github.abc15018045126.sora`。
*   **同步建议**：由于包名和路径改动极大，**请勿直接通过 Git pull/merge 同步原作者的仓库**，这会导致不可调和的冲突。如果需要同步原作者功能，建议手动对比代码逻辑进行迁移。
*   **如何回退/迁移**：如果你希望在保留本项目功能的同时使用原项目代码，你需要手动处理包名映射或将本项目的逻辑 cherry-pick 到原项目的包结构中。

**新增特性 (截止 v0.0.2)：**
*   **折行间距独立控制**：支持通过 `wrapLineSpacingMultiplier` 和 `wrapLineSpacingExtra` 独立配置自动折行后的行间距。
*   **当前行背景高亮**：支持开启/关闭当前行高亮，并可自定义高亮颜色（支持透明色）。
*   **高度自定义光标 (Caret)**：
    *   支持自定义光标颜色、光标宽度（粗细）。
    *   支持选择光标提手（Selection Handle）样式：侧水滴、正水滴、或**彻底隐藏提手**（仅保留竖杆）。


## 特色

- [x] 增量语法高亮
- [x] 自动补全 (包含对[代码块（Code Snippets）](https://macromates.com/manual/en/snippets)的支持)
- [x] 自动缩进
- [x] 代码块辅助线
- [x] 手势缩放
- [x] 撤销/重做
- [x] 搜索和替换文本
- [x] 自动换行
- [x] 显示不可打印的字符
- [x] 诊断信息标记
- [x] 文本放大镜
- [x] 粘性滚动
- [x] 高亮显示括号对
- [x] 事件系统
- [x] TextMate 和 TreeSitter 支持

## 文档

请阅读 [快速开始](https://project-sora.github.io/sora-editor-docs/guide/getting-started)
来快速上手使用此编辑器。也可以在[项目文档站点](https://project-sora.github.io/sora-editor-docs/)
查看所有的使用文档。

* [编辑器概览](https://project-sora.github.io/sora-editor-docs/guide/editor-overview)
* [参考](https://project-sora.github.io/sora-editor-docs/reference/xml-attributes)
* [文档仓库](https://github.com/project-sora/sora-editor-docs)

## 编辑器预览图

<div style="overflow: hidden">
<img src="/images/general.jpg" alt="GeneralAppearance" width="40%" align="bottom" />
<img src="/images/problem_indicators.jpg" alt="ProblemIndicator" width="40%" align="bottom" />
</div>

## 讨论

* QQ群:[734652304](https://qm.qq.com/q/kKBqRsVrQ4)
* [Telegram 群组](https://t.me/abc15018045126_code_editor)

## 贡献者

<a href="https://github.com/abc15018045126/sora-editor/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=abc15018045126/sora-editor" />
</a>

## 许可证

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

## 鸣谢

感谢[JetBrains](https://www.jetbrains.com/?from=CodeEditor)为本项目
提供的[IntelliJ IDEA](https://www.jetbrains.com/idea/?from=CodeEditor)等IDE的免费许可证。

[<img src=".github/jetbrains-variant-3.png" width="200"/>](https://www.jetbrains.com/?from=CodeEditor)


