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

import com.moom.kson.token.JsonElement
import com.moom.kson.token.JsonToken
import com.moom.kson.token.JsonObject

/**
 * JsonObject object builder
 */
class ObjectBuilder: AbstractValueBuilder(false /* JsonObject may be empty */, false) {

    private var elements = ArrayList<JsonElement>()

    override fun add(t: JsonToken): Boolean {

        if (t is JsonElement) {
            elements.add(t)
            more = false
            return true
        }

        return false
    }

    override fun readyForMore(): Boolean =
        when(elements.size) {
            0   -> !end
            else -> more
        }

    override fun isDone():Boolean {
        return end && !more
    }

    override fun build(): JsonToken? {
        if (isDone()) {
            val obj = JsonObject()
            elements.forEach {
                obj.add(it)
            }

            return obj
        }

        throw JsonSyntaxException(
            "Cannot build object because the builder is not completed." +
                    "size=${elements.size}, more=$more, end=$end"
        )

    }
}