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

import org.junit.Assert.assertEquals
import org.junit.Test

internal class JsonTokenTest {

    @Test
    fun testObj() {
        assertEquals("{ \"1\": \"one\", \"2\": \"two\" }",
            obj { e {"1" to "one"}; e {"2" to "two"} }.toString())
    }

    @Test
    fun testA() {
        assertEquals("[ \"Number#1\", 12345, null, true, false, 3.14159 ]",
        a { v { +"Number#1" }; v { !12345 }; v { JsonNull() }; v { +true }; v { +false }; v { !3.14159 }}.toString())
    }

    @Test
    fun testObjAndA() {
        assertEquals("{ \"1\": \"one\", \"2\": [ \"two\", \"three\" ] }",
            obj {
                e {"1" to "one"}
                e {"2" to a {
                    v {+"two"}
                    v {+"three"}
                }
                }
            }.toString())
    }

}