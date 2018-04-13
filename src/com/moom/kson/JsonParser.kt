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

package com.moom.kson

import java.util.*
import java.io.Reader
import java.io.InputStream

import com.moom.kson.token.*
import com.moom.kson.parser.getParser

/**
 * JSON parser interface
 */
interface JsonParser {
    fun parse(json: InputStream): ArrayList<JsonToken>?
    fun parse(json: Reader): ArrayList<JsonToken>?
    fun parse(json: String): ArrayList<JsonToken>?

    companion object {
        fun parser(multithread: Boolean = true): JsonParser = getParser(multithread)
    }
}
