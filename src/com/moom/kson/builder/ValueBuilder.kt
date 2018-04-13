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

/**
 * interface for building aggregation and collective token objects, such as JsonElement, JsonArray, JsonObject
 */
interface ValueBuilder {

    /**
     * check if the builder is ready for adding more values
     */
    fun readyForMore(): Boolean

    /**
     * check if the builder is done
     * an object builder is done when the brace } is parsed
     * an array builder is done when the ] is parsed
     * an element builder is done when the second value is added
     */
    fun isDone(): Boolean

    /**
     * Add a Value to the builder
     */
    fun add(t: JsonToken): Boolean {
        return false
    }

    /**
     * set more flag means the builder need at least one more value
     * this is invoked when parsing , and :
     */
    fun setMore()

    /**
     * set end flag when parsing "]}"
     */
    fun setEnd()

    /**
     * build the value
     */
    fun build(): JsonToken?
}

abstract class AbstractValueBuilder(initMore: Boolean, initEnd: Boolean): ValueBuilder {

    /**
     * internal property to identify whether the builder is endable
     */
    protected var more = initMore
    protected var end = initEnd

    override fun setMore() {
        more = true
    }

    override fun setEnd() {
        end = true
    }
}