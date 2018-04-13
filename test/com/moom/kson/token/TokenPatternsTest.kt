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

import org.junit.Assert.assertEquals
import org.junit.Test

internal class TokenPatternsTest {
    @Test fun testTokenPatterns() {
        //whitespaces
        assertEquals(true, TokenPatterns.WhiteSpace.match("\t"))
        assertEquals(true, TokenPatterns.WhiteSpace.match("\r"))
        assertEquals(true, TokenPatterns.WhiteSpace.match("\n"))
        assertEquals(true, TokenPatterns.WhiteSpace.match(" "))
        assertEquals(false, TokenPatterns.WhiteSpace.match("\\"))

        //structural tokens
        assertEquals(true, TokenPatterns.StructuralToken.match("["))
        assertEquals(true, TokenPatterns.StructuralToken.match("]"))
        assertEquals(true, TokenPatterns.StructuralToken.match("{"))
        assertEquals(true, TokenPatterns.StructuralToken.match("}"))
        assertEquals(true, TokenPatterns.StructuralToken.match(":"))
        assertEquals(true, TokenPatterns.StructuralToken.match(","))
        assertEquals(false, TokenPatterns.StructuralToken.match(";"))

        //null token
        assertEquals(true, TokenPatterns.NullPattern.match("null"))
        assertEquals(false, TokenPatterns.NullPattern.match("Null"))
        assertEquals(false, TokenPatterns.NullPattern.match("NULL"))

        //boolean tokens
        assertEquals(true, TokenPatterns.BooleanPattern.match("true"))
        assertEquals(true, TokenPatterns.BooleanPattern.match("false"))
        assertEquals(false, TokenPatterns.BooleanPattern.match("True"))
        assertEquals(false, TokenPatterns.BooleanPattern.match("False"))
        assertEquals(false, TokenPatterns.BooleanPattern.match("TRUE"))
        assertEquals(false, TokenPatterns.BooleanPattern.match("FALSE"))

        //Number tokens
        assertEquals(true, TokenPatterns.NumberPattern.match("1234567"))
        assertEquals(true, TokenPatterns.NumberPattern.match("-1234567"))
        assertEquals(true, TokenPatterns.NumberPattern.match("1234.567"))
        assertEquals(true, TokenPatterns.NumberPattern.match("1234.567E12"))
        assertEquals(true, TokenPatterns.NumberPattern.match("1234.567E-12"))
        assertEquals(true, TokenPatterns.NumberPattern.match("1234.567E+12"))
        assertEquals(true, TokenPatterns.NumberPattern.match("-1234.567E12"))
        assertEquals(true, TokenPatterns.NumberPattern.match("-1234.567E+12"))
        assertEquals(true, TokenPatterns.NumberPattern.match("-1234.567E-12"))
        assertEquals(true, TokenPatterns.NumberPattern.match("-1234.567e-12"))

        //String tokens
        assertEquals(true, TokenPatterns.StringPattern.match("\"hello \""))
        assertEquals(true, TokenPatterns.StringPattern.match("\"this is a simple \\\" test\""))
        assertEquals(false, TokenPatterns.StringPattern.match("\"this is a simple \\\" test\\\""))
        assertEquals(false, TokenPatterns.StringPattern.match("\"this is a simple \" test\""))
        assertEquals(true, TokenPatterns.StringPattern.match("\"\""))
        assertEquals(false, TokenPatterns.StringPattern.match("\"h\"e\"l  \"l \" \" o\\\""))
    }

    @Test fun testStructuralTokenType() {
        assertEquals(StructuralTokenType.MoreValue, getCharToken(','))
        assertEquals(StructuralTokenType.ElementPair, getCharToken(':'))
        assertEquals(StructuralTokenType.ArrayStart, getCharToken('['))
        assertEquals(StructuralTokenType.ArrayEnd, getCharToken(']'))
        assertEquals(StructuralTokenType.ObjectStart, getCharToken('{'))
        assertEquals(StructuralTokenType.ObjectEnd, getCharToken('}'))

        assertEquals(null, getCharToken(';'))
        assertEquals(null, getCharToken('('))
        assertEquals(null, getCharToken(')'))
    }

    @Test fun testTokenFunctions() {
        assertEquals(true, isWhiteSpace("\t\r\n "))
        assertEquals(true, isStructuralToken(","))
        assertEquals(true, isStructuralToken(":"))
        assertEquals(true, isStructuralToken("["))
        assertEquals(true, isStructuralToken("]"))
        assertEquals(true, isStructuralToken("{"))
        assertEquals(false, isStructuralToken(";"))
        assertEquals(true, isNullToken("null"))
        assertEquals(true, isBooleanToken("true"))
        assertEquals(true, isBooleanToken("false"))
        assertEquals(true, isNumberToken("-3.14159e+24"))
        assertEquals(true, isNumberToken("3000012312423400993928440000000000000000.03"))
        assertEquals(true, isStringToken("\"Hello World!\""))

        assertEquals(false, isToken("\t"))
        assertEquals(false, isToken("\r"))
        assertEquals(false, isToken("\n"))
        assertEquals(false, isToken(" "))
        assertEquals(true, isToken(","))
        assertEquals(true, isToken(":"))
        assertEquals(true, isToken("["))
        assertEquals(true, isToken("]"))
        assertEquals(true, isToken("{"))
        assertEquals(true, isToken("}"))
        assertEquals(true, isToken("null"))
        assertEquals(true, isToken("true"))
        assertEquals(true, isToken("false"))
        assertEquals(true, isToken("-3.14159e+24"))
        assertEquals(true, isToken("3000012312423400993928440000000000000000.03"))
        assertEquals(true, isToken("\"Hello World!\""))

        assertEquals(true, isLiteralNameToken("null"))
        assertEquals(true, isLiteralNameToken("true"))
        assertEquals(true, isLiteralNameToken("false"))

        assertEquals(true, isNullTokenStart("n"))
        assertEquals(true, isBooleanTokenStart("t"))
        assertEquals(true, isBooleanTokenStart("f"))

        assertEquals(true, isStringTokenStart("\""))
        assertEquals(true, isNumberTokenStart("-"))
        assertEquals(true, isNumberTokenStart("0"))
        assertEquals(true, isNumberTokenStart("1"))
        assertEquals(true, isNumberTokenStart("2"))
        assertEquals(true, isNumberTokenStart("3"))
        assertEquals(true, isNumberTokenStart("4"))
        assertEquals(true, isNumberTokenStart("5"))
        assertEquals(true, isNumberTokenStart("6"))
        assertEquals(true, isNumberTokenStart("7"))
        assertEquals(true, isNumberTokenStart("8"))
        assertEquals(true, isNumberTokenStart("9"))
        assertEquals(false, isNumberTokenStart("+"))
        assertEquals(false, isNumberTokenStart("."))
        assertEquals(false, isNumberTokenStart("E"))
        assertEquals(false, isNumberTokenStart("e"))

        assertEquals(true, isLiteralNameTokenStart("n"))
        assertEquals(true, isLiteralNameTokenStart("f"))
        assertEquals(true, isLiteralNameTokenStart("t"))

        assertEquals(true, isSpace(' '))

        assertEquals(true, isTokenStart("n"))
        assertEquals(true, isTokenStart("f"))
        assertEquals(true, isTokenStart("t"))
        assertEquals(true, isTokenStart("n"))
        assertEquals(true, isTokenStart("\""))
    }
}