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

package com.moom.kson.parser

import java.util.*
import java.io.InputStream
import java.io.Reader

import com.moom.kson.builder.JsonBuilder
import com.moom.kson.builder.JsonBuilderThread
import com.moom.kson.builder.TokenQueue
import com.moom.kson.builder.JsonReader
import com.moom.kson.builder.JsonReaderThread
import com.moom.kson.token.*
import com.moom.kson.*

/**
 * Get a parser implementation
 */

internal fun getParser(multithread: Boolean = true): JsonParser {
    if (multithread)
        return JsonParserParallel()

    return JsonParserSingleThread()
}

/**
 * parser a json file/string in single thread
 */
private class JsonParserSingleThread: JsonParser {
    /**
     * Builder
     */
    private var builder = JsonBuilder()

    override fun parse(json: Reader): ArrayList<JsonToken>? = parse(JsonReader(json))
    override fun parse(json: InputStream): ArrayList<JsonToken>? = parse(JsonReader(json))
    override fun parse(json: String): ArrayList<JsonToken>? = parse(JsonReader(json))

    private fun parse(reader: JsonReader): ArrayList<JsonToken>? {
        builder.reset()
        reader.read {
            it?.let {
                parseToken(it)
            }
        }

        println("End reading JSON.")

        if (!builder.validate()) {
            println("validate builder failed")
            return null
        }

        return builder.build()
    }

    private fun parseToken(token: String): Boolean {

        var ret = false
        if(isStructuralToken(token)) {
            ret = builder.add(token[0])
        } else if (isNullToken(token)) {
            ret = builder.add(JsonNull())
        } else if (isBooleanToken(token)) {
            ret = builder.add(JsonBoolean(token.toBoolean()))
        } else if (isNumberToken(token)) {
            ret = builder.add(JsonNumber(token))
        } else if (isStringToken(token)) {
            ret = builder.add(JsonString(token.substring(1..(token.length - 2))))    //remove ""
        } else {
            println("Unknown token: $token")
        }

        return ret
    }
}

/**
 * Parse json in mutiple threads with consumer/producer model
 */
private class JsonParserParallel: JsonParser {
    val queue = TokenQueue()

    override fun parse(json: Reader): ArrayList<JsonToken>? = parse(JsonReader(json))
    override fun parse(json: InputStream): ArrayList<JsonToken>? = parse(JsonReader(json))
    override fun parse(json: String): ArrayList<JsonToken>? = parse(JsonReader(json))

    private fun parse(json: JsonReader): ArrayList<JsonToken>? {
        queue.reset()
        val reader = JsonReaderThread(queue, json)
        val builder = JsonBuilderThread(queue)
        reader.start()
        builder.start()

        reader.join()
        builder.join()

        return builder.build()
    }

}


