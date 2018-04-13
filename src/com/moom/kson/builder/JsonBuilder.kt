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

import com.moom.kson.token.StructuralTokenType.*
import com.moom.kson.token.JsonToken
import com.moom.kson.token.getCharToken
import java.util.*
import kotlin.collections.ArrayList

/**
 * Json token tree builder
 * This class is used for single thread process to parser json and build token tree
 * Note, this build assumes the json file's root token is Objects or Arrays,not separate elements or values
 */
class JsonBuilder {

    private var stack = ArrayDeque<ValueBuilder>()

    fun add(ch: Char): Boolean = when(getCharToken(ch)) {
        ArrayStart  -> startArray()
        ArrayEnd    -> endArray()
        ObjectStart -> startObject()
        ObjectEnd   -> endObject()
        ElementPair -> setElementPair()
        MoreValue   -> setMoreToken()
        else        -> false
    }

    fun add(v: JsonToken): Boolean {
        val top = stack.peek()

        if (top == null || top.isDone() || !top.readyForMore()) throw JsonSyntaxException("add value failed.")

        return when(top) {
            is ArrayBuilder -> top.add(v)
            is ElementBuilder -> {
                top.add(v)
                validate()
            }
            is ObjectBuilder -> startElement(v)
            else -> false
        }
    }

    fun validate(): Boolean {
        if (isAllDone()) return true

        var top = stack.peek()
        if (top!=null && top.isDone()) {
            if (stack.size == 1) return true


            val db = stack.pop()
            top = stack.peek()
            if (top.isDone()) {
                stack.push(db)
            } else {
                top.add(db.build()!!)
                return validate()
            }
        }

        return true
    }

    fun reset() = stack.clear()

    private fun isAllDone(): Boolean {
        stack.forEach { if(!it.isDone()) return false }
        return true
    }

    private fun startElement(v: JsonToken): Boolean {
        val top = stack.peek()

        if (top == null || top.isDone() || !top.readyForMore())
            throw JsonSyntaxException("add value failed. the top builder should be ObjectBuilder")

        assert(top is ObjectBuilder)

        val e = ElementBuilder()
        e.add(v)
        stack.push(e)

        return true
    }

    private fun startArray(): Boolean {
        val ab = ArrayBuilder()

        stack.push(ab)

        return true
    }

    private fun endArray(): Boolean {
        val ab = stack.peek() as? ArrayBuilder
        ab?.let {
            ab.setEnd()
            validate()
            return true
        }

        return false
    }

    private fun startObject(): Boolean {
        val ob = ObjectBuilder()
        stack.push(ob)

        return true
    }

    private fun endObject(): Boolean {
        val ob = stack.peek() as? ObjectBuilder
        ob?.let {
            ob.setEnd()
            validate()
            return true
        }

        return false
    }

    private fun setElementPair(): Boolean {
        val eb = stack.peek() as? ElementBuilder
        eb?.let {
            eb.setMore()
            return true
        }

        return false
    }

    private fun setMoreToken(): Boolean {
        val ab = stack.peek()

        when(ab) {
            is ArrayBuilder -> ab.setMore()
            is ObjectBuilder -> ab.setMore()
            else -> throw JsonSyntaxException("builder is broken, there is null object in stack!")
        }

        return true
    }

    fun build(): ArrayList<JsonToken>? {
        if (!validate())  {
            println("builder is not completed")
            return null
        }

        val ar = ArrayList<JsonToken>()
        while(stack.size>0) {
            stack.pop()?.build()?.let {
                ar.add(it)
            }
        }

        ar.forEach {
            it.updateIndent()
        }

        return ar
    }
}