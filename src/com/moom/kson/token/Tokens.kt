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

package com.moom.kson.token

import com.moom.kson.token.IndentedString.Companion.char
import com.moom.kson.token.IndentedString.Companion.number
import kotlin.math.max

/**
 * Indented string interface
 * This interface is used to output user friendly string
 */
internal interface IndentedString {
    /**
     * indent number
     */
    var indent: Int

    /**
     * update children's indent number
     */
    fun updateIndent()

    /**
     * convert to indented string
     */
    fun toIndentedString(): String

    /**
     * get max siblings in all descendants
     */
    fun estimateLines(): Int

    companion object {
        /**
         * number of indent char for each indent
         */
        const val number = 2

        /**
         * the char used to fill indent, default it is space ' '
         */
        const val char = ' '
    }
}

/**
 * base token of all values in json, such as boolean, number, null and strings
 */
open class JsonToken protected constructor(private val v: String): IndentedString {

    override fun toString(): String = v

    /**
     * Nested indent, used to determine indent in toIndentedString()
     */
    override var indent = 0

    /**
     * convert to text with human friendly multiline string with indents
     */
    override fun toIndentedString(): String = toString()

    /**
     * update children's indent value
     */
    override fun updateIndent() { /* no children, no action */ }

    /**
     * get max siblings in all descendants
     */
    override fun estimateLines(): Int = 1
}

/**
 * For literal name token null
 */
class JsonNull: JsonToken("null")

/**
 * For literal name token true and false
 */
class JsonBoolean(private val b: Boolean): JsonToken(b.toString()) {
    fun getValue() = b
}

/**
 * Number token
 */
class JsonNumber(num: String): JsonToken(num)

/**
 * String token
 */
class JsonString(text: String): JsonToken(text) {
    override fun toString(): String {
        return "\"${super.toString()}\""
    }
}

/**
 * Name token
 */
typealias JsonName = JsonString

/**
 * JsonElement is a name: value token pair
 */
class JsonElement(private val name: JsonToken, private val value: JsonToken): JsonToken("e") {
    override fun toString(): String {
        return "$name: $value"
    }

    override fun toIndentedString(): String = "${name.toIndentedString()}: ${value.toIndentedString()}"
    override fun estimateLines(): Int = max(value.estimateLines(), 1)
    override fun updateIndent() {
        name.indent = indent+1
        value.indent = indent+1
        value.updateIndent()
    }
}

/**
 * collective token has a set of values, such as object and array
 */
abstract class JsonCollectiveToken(private val b: Char, private val e: Char, type:String): JsonToken(type) {
    private val items: MutableList<JsonToken> = ArrayList()

    open fun add(e: JsonToken) {
        e.indent = indent+1
        items.add(e)
    }

    /**
     * all in one line
     */
    override fun toString(): String {
        var str = " "
        items.forEachIndexed { index, value ->
            if (index<items.size-1)
                str += "$value, "
            else
                str += "$value "
        }

        return "$b$str$e"
    }

    /**
     * to string in more friendly way by indents and multilines
     */
    override fun toIndentedString(): String {
        val braceIndent = "${char}".repeat(indent* number)
        val elementIndent = "${char}".repeat((indent+1)* number)

        var text = "$b "
        items.forEachIndexed {  index, value ->
            val head = if(estimateLines()==1) "" else "\n$elementIndent"
            val tail = if (index < items.size-1) ", " else " "
            text += "$head${value.toIndentedString()}$tail"
        }

        val lastIndent = if(estimateLines()==1) "" else "\n$braceIndent"
        text += "$lastIndent$e"

        return text
    }

    /**
     * this method is mainly used in toIndentedString to evaluate
     * the number of lines it may be split
     * @return >=1
     */
    override fun estimateLines(): Int {
        val count = items.maxBy { it.estimateLines() }?.estimateLines() ?: 1

        return max(count, items.size)
    }

    /**
     * update indent number of all descendants
     */
    override fun updateIndent() {
        items.forEach {
            it.indent = indent+1
            it.updateIndent()
        }
    }
}

class JsonObject: JsonCollectiveToken('{','}',"o") {
    override fun add(e: JsonToken) {
        assert(e is JsonElement)
        super.add(e)
    }

}

/**
 * Array is a list of values
 */
class JsonArray: JsonCollectiveToken('[',']',"a")

/**
 * methods used for constructing JSON format programmatically
 */

/**
 * support programming format as below
 * obj {
 *   e {"name" to "value"}
 *   e {"name" to number}
 *   e {"name" to boolean}
 *   e {"name" to JsonNull()}
 *   e {"name" to JsonTokenInstance }
 * }
 */
fun obj(init: JsonObject.() -> Unit) = JsonObject().apply(init)
fun JsonObject.e(init: () -> JsonElement) = add(init())

// name to value -> item
infix fun String.to(v: JsonToken): JsonElement =
    JsonElement(JsonName(this), v)
infix fun String.to(v: String): JsonElement =
    JsonElement(JsonName(this), JsonString(v))
infix fun String.to(v: Boolean): JsonElement =
    JsonElement(JsonName(this), JsonBoolean(v))
infix fun String.to(v: Short): JsonElement =
    JsonElement(JsonName(this), JsonNumber("$v"))
infix fun String.to(v: Int): JsonElement =
    JsonElement(JsonName(this), JsonNumber("$v"))
infix fun String.to(v: Long): JsonElement =
    JsonElement(JsonName(this), JsonNumber("$v"))
infix fun String.to(v: Float): JsonElement =
    JsonElement(JsonName(this), JsonNumber("$v"))
infix fun String.to(v: Double): JsonElement =
    JsonElement(JsonName(this), JsonNumber("$v"))

infix fun String.to(init: () -> JsonToken): JsonElement = this to init()

/**
 * Add a value to array
 * support programming as below
 * a {
 *  v{ +"a string value" }
 *  v{ +true|false }
 *  v{ !number }
 *  v{ jsontoken }
 * }
 */
fun a(init: JsonArray.() -> Unit) = JsonArray().apply(init)
fun JsonArray.v(init: () -> JsonToken) = add(init())

operator fun String.unaryPlus(): JsonToken = JsonString(this)
operator fun Short.not(): JsonToken = JsonNumber("$this")
operator fun Int.not(): JsonToken = JsonNumber("$this")
operator fun Long.not(): JsonToken = JsonNumber("$this")
operator fun Float.not(): JsonToken = JsonNumber("$this")
operator fun Double.not(): JsonToken = JsonNumber("$this")
operator fun Boolean.unaryPlus(): JsonToken = JsonBoolean(this)
