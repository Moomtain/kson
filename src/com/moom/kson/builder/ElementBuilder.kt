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
import com.moom.kson.token.JsonName

/**
 * The class is used to build JsonElement objects
 */
class ElementBuilder: AbstractValueBuilder(true, false) {

    private var step = 0
    private var name: JsonName? = null
    private var value: JsonToken? = null

    override fun add(t: JsonToken): Boolean {
        var ret = false
        if (more) {
            when (step) {
                0 -> ret = addName(t)
                1 -> ret = addValue(t)
            }
        }

        return ret
    }

    private fun addName(t: JsonToken): Boolean {
        if (t is JsonName) {
            name = t
            step++
            more=false
            return true
        }

        return false
    }

    private fun addValue(t: JsonToken):Boolean {
        value = t
        step++
        more = false
        end = true

        return true
    }

    override fun readyForMore():Boolean = more

    override fun isDone():Boolean {
        if (step>1 && !more && end) return true

        return false
    }

    override fun build(): JsonToken? {
        if (isDone()) {
            return JsonElement(name!!, value!!)
        }

        throw JsonSyntaxException(
            "Cannot build object because the builder is not completed." +
                    "name=$name, value=$value, step=$step, more=$more, end=$end"
        )
    }
}