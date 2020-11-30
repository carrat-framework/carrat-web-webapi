package org.carrat.webidl.build.compile.model.webidlir

enum class ArgumentNameKeyword(val keyword : String) {
    ASYNC("async"),
    ATTRIBUTE("attribute"),
    CALLBACK("callback"),
    CONST("const"),
    CONSTRUCTOR("constructor"),
    DELETER("deleter"),
    DICTIONARY("dictionary"),
    ENUM("enum"),
    GETTER("getter"),
    INCLUDES("includes"),
    INHERIT("inherit"),
    INTERFACE("interface"),
    ITERABLE("iterable"),
    MAPLIKE("maplike"),
    MIXIN("mixin"),
    NAMESPACE("namespace"),
    PARTIAL("partial"),
    READONLY("readonly"),
    REQUIRED("required"),
    SETLIKE("setlike"),
    SETTER("setter"),
    STATIC("static"),
    STRINGIFIER("stringifier"),
    TYPEDEF("typedef"),
    UNRESTRICTED("unrestricted");

    companion object {
        val byName = ArgumentNameKeyword.values().map { it.keyword to it }.toMap()
    }
}