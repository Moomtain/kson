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

import java.io.InputStream
import java.io.Reader

/**
 * Json reader in a thread, parser json to tokens and add them to queue
 */
class JsonReaderThread(private val queue: TokenQueue, private val reader: JsonReader): Runnable {
    private val thread = Thread(this)

    constructor(queue: TokenQueue, json: Reader): this(queue, JsonReader(json))
    constructor(queue: TokenQueue, json: InputStream): this(queue,
        JsonReader(json)
    )
    constructor(queue: TokenQueue, json: String): this(queue, JsonReader(json))

    override fun run() {
        read()
    }

    private fun read() {
        //assert(reader != null)
        reader.read {
            if (it == null) queue.done = true
            else queue.add(it)
        }
    }

    fun start() {
        thread.start()
    }

    fun join() {
        thread.join()
    }
}