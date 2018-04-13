/*
 *  Copyright (C) 2018 Moomtain <dev@moomtain.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Moomtain <dev@moomtain.com>
 * @version 2018-04-19
 */

package com.moom.kson.token

/**
 * token regexp patterns
 */
internal enum class TokenPatterns(val pattern: String) {
    WhiteSpace("[\t\r\n ]*"),
    StructuralToken("[\\[\\]\\{\\}:,]"),
    NullPattern("null"),
    BooleanPattern("(true|false)"),
    NumberPattern("-?[0-9]+(.[0-9]+)?([e|E][+|-]?[0-9]+)?"),

    /**
     * String token pattern rules:
     * 1.Starts with quotation"
     * 2.Ends with another quotation", the character before the last " should not be escape character \
     * 3.In the middle part, there may be zero or more \"
     * 4.Another characters are ones that are not whitespace(\t\r\n) or "
     */
    StringPattern("(^\"\"$|^\"(([^\t\r\n\"])|(\\\\\"))*[^\\\\]\"$)");

    fun match(str: String):Boolean {
        return pattern.toRegex().matches(str)
    }
}

/**
 * structural tokens
 */
enum class StructuralTokenType {
    MoreValue,
    ElementPair,
    ArrayStart,
    ArrayEnd,
    ObjectStart,
    ObjectEnd;

    companion object {
        /**
         * NOTE,
         * 1. keep the character ascending order in below string
         * 2. keep the string length same as count of StructuralToken members
         */
        const val structuralTokenString = ",:[]{}"
    }
}

/**
 * char to structural token
 */
internal fun getCharToken(ch: Char): StructuralTokenType? {
    val str = StructuralTokenType.structuralTokenString

    for(it in StructuralTokenType.values()) {
        if (str[it.ordinal] == ch) return it
    }

    return null
}

internal fun isWhiteSpace(str: String): Boolean       = TokenPatterns.WhiteSpace.match(str)
internal fun isStructuralToken(str: String): Boolean  = TokenPatterns.StructuralToken.match(str)
internal fun isNullToken(str: String): Boolean        = TokenPatterns.NullPattern.match(str)
internal fun isBooleanToken(str: String): Boolean     = TokenPatterns.BooleanPattern.match(str)
internal fun isNumberToken(str: String): Boolean      = TokenPatterns.NumberPattern.match(str)
internal fun isStringToken(str: String): Boolean      = TokenPatterns.StringPattern.match(str)
internal fun isToken(str: String): Boolean            = isStructuralToken(str)||isNullToken(str)||
                        isBooleanToken(str) || isNumberToken(str) || isStringToken(str)
internal fun isLiteralNameToken(str: String): Boolean = isNullToken(str)||isBooleanToken(str)

internal fun isNullTokenStart(str: String): Boolean   = str.isNotEmpty() && "null".startsWith(str)
internal fun isBooleanTokenStart(str: String): Boolean = str.isNotEmpty() &&
                                                        ("true".startsWith(str) || "false".startsWith(str))
internal fun isStringTokenStart(str: String): Boolean = str.isNotEmpty() && str[0].equals('"')
internal fun isNumberTokenStart(str: String): Boolean = str.isNotEmpty() && (str[0].equals('-') ||
                                                        (str[0] in '0'..'9'))

internal fun isLiteralNameTokenStart(str: String): Boolean = isNullTokenStart(str) || isBooleanTokenStart(
    str
)
internal fun isTokenStart(str: String): Boolean            = isLiteralNameTokenStart(str) ||
        isNullTokenStart(str) || isStringTokenStart(str)
internal fun isSpace(ch: Char): Boolean = ch == ' '


