# Keep models for serialization
-keep class org.eclipse.tm4e.languageconfiguration.internal.model.* { *; }
-keep class io.github.abc15018045126.sora.langs.textmate.registry.model.* { *; }
# Fix R8 unexpectedly removed class
-keep class org.joni.ast.QuantifierNode { *; }
# Ignore optional component
-dontwarn io.github.abc15018045126.oniguruma.OnigNative