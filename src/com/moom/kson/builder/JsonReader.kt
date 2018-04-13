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

package com.moom.kson.builder

import com.moom.kson.token.*
import java.io.*

/**
 * Json reader splits json into tokens
 */
class JsonReader(private var reader: Reader) {
    constructor(stream: InputStream): this(InputStreamReader(BufferedInputStream(stream)))
    constructor(json: String):this(StringReader(json))

    /**
     * last char is reserved for next reading
     */
    private var bufferedChar: Char? = null

    fun read(onToken: (String?) -> Unit) {
        readJson(onToken)
    }

    private fun readJson(onToken: (String?) -> Unit) {
        assert(bufferedChar == null)

        var token: String?

        try {
            do {
                token = readToken()

                //EOF
                if (token == null) {
                    onToken(token)
                    break
                }

                //add token to the queue
                onToken(token)

            } while (token != null)
        }catch (e: Exception) {
            throw JsonSyntaxException("Reading json failed. cause = $e")
        }

        println("read json completed.")
    }

    private fun skipWhiteSpaceAndReadChar(): Char? {
        val r = reader

        val chs = CharArray(1)
        var count: Int
        r.let {
            do {
                count = r.read(chs, 0, chs.size)
            } while(count == 1 && chs[0].isWhitespace())

            if (count == 1) return chs[0]
        }

        return null
    }

    //read a string between two whitespace(\t\r\n) or structural tokens([]{}:,)
    private fun readSegment(): String? {
        val ch0 = bufferedChar ?: skipWhiteSpaceAndReadChar()
        bufferedChar = null
        //EOF
        if (ch0 == null || ch0 == 0.toChar()) {
            return null
        }

        //first char,it should not be whitespace here
        var str = ch0.toString()
        if (isStructuralToken(str)) { //[]{}:,
            return str
        }

        //if (reader == null) return str
        //not a structural token, read till next whitespace
        val chs = CharArray(1)
        val r = reader
        var count: Int
        do {
            count = r.read(chs, 0, chs.size)
            if (count == 0) break

            val sch = chs[0].toString()
            if (isWhiteSpace(sch)) {
                /**
                 * Normally, whitespace appears between tokens
                 * while space ' ' may appear in string token
                 */
                if (isSpace(chs[0])) {
                    if (!isStringToken(str) && isStringTokenStart(str)) {
                        str += chs[0]
                        continue
                    }
                }

                break
            } else if (isStructuralToken(sch)) {
                /**
                 * Normally, structural token characters means a new token
                 * while these characters "[]{}:," may appear in string token
                 */
                if (isToken(str)) {
                    bufferedChar = chs[0]
                    break
                } else if (isStringTokenStart(str)) {
                    str += chs[0]
                } else {
                    //str is not a token, invalid format
                    throw JsonSyntaxException("Invalid token string. str=$str")
                }
            } else {
                str += chs[0]
            }
        } while(count>0)

        return str
    }

    private fun readToken(): String? {
        val segment = readSegment()

        //EOF
        if (segment.isNullOrEmpty()) return null

        //token
        if (isToken(segment!!)) {
            return segment
        }

        //invalid format,not a token
        throw JsonSyntaxException("Invalid token string. str=$segment")
    }
}


