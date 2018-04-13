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
import kotlin.collections.ArrayList

/**
 * A consumer thread that gets string tokens from TokenQueue and add them to JsonBuilder
 */
class JsonBuilderThread(private val queue: TokenQueue): Runnable {

    private val builder = JsonBuilder()
    private val thread = Thread(this)

    fun join() = thread.join()

    fun start() {
        builder.reset()

        thread.start()
    }

    override fun run() {
        while(!queue.isDone()) {
            val token = queue.get()

            token?.let {
                /**
                 * Add structural token if it is one
                 * Note, a Number may have only one number char
                 */
                if (token.length == 1 && builder.add(token[0])) { /*  */}
                /**
                 * Add another token
                 */
                else builder.add(toTokenObjectQuick(token))
            }

        }

        println("Done")
    }

    /**
     * convert token string to object by quick method
     */
    private fun toTokenObjectQuick(token: String): JsonToken {
        assert(token.isNotEmpty())

        return when(token[0]) {
                'n' -> JsonNull()
                't' -> JsonBoolean(token.toBoolean())
                'f' -> JsonBoolean(token.toBoolean())
                '"' -> JsonString(token.substring(1..token.length-2))
                else -> JsonNumber(token)
            }
    }

    fun build(): ArrayList<JsonToken>? = builder.build()
}