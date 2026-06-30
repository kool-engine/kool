# Native code isn't used at the moment

Kotlin multi platform projects don't support native Android code anymore (since agp 9.x)
Native extensions have to be moved to a separate pure Android library to make them work again.
However, since they aren't really essential, native functions are simply disabled for now.