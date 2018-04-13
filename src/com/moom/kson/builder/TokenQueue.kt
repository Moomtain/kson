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

import java.util.concurrent.ConcurrentLinkedDeque

/**
 * A FIFO queue to store json tokens
 * Note, it doesn't validate the token
 */
class TokenQueue {
    private var queue: ConcurrentLinkedDeque<String> = ConcurrentLinkedDeque()
    var done = false

    /**
     * Add a json token to the FIFO queue
     * null means it's done
     */
    fun add(item: String?) {
        if (!done) {
            if (item == null)
                done = true
            else
                queue.add(item)
        }
    }

    /**
     * check if the queue is empty and done
     */
    fun isDone():Boolean = done && queue.isEmpty()

    /**
     * get a token from queue, null if the queue is empty
     */
    fun get(): String? {
        var ret: String? = null
        if(queue.isNotEmpty())
            ret = queue.removeFirst()

        return ret
    }

    /**
     * reset queue and done flag
     */
    fun reset() {
        queue.clear()
        done = false
    }
}