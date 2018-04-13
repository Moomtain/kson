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

import com.moom.kson.token.JsonToken
import com.moom.kson.token.JsonArray

/**
 * JsonArray object builder
 */
class ArrayBuilder: AbstractValueBuilder(false /* an array can be empty */, false) {

    private var elements = ArrayList<JsonToken>()

    override fun add(t: JsonToken): Boolean {

        elements.add(t)
        more = false

        return true
    }

    override fun readyForMore(): Boolean =
        when(elements.size) {
            0    -> !end
            else -> more
        }

    override fun isDone():Boolean {
        return (!more) && end
    }

    override fun build(): JsonToken? {
        if (isDone()) {
            val ar = JsonArray()
            elements.forEach {
                ar.add(it)
            }

            return ar
        }

        throw JsonSyntaxException(
            "Cannot build object because the builder is not completed." +
                    "size=${elements.size}, more=$more, end=$end"
        )
    }
}