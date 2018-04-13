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

package com.moom.kson

import org.junit.Assert.assertEquals
import org.junit.Test

internal class JsonParserTest {
    val json = "{\n" +
            "\t\"id\": \"0001\",\n" +
            "\t\"type\": \"donut\",\n" +
            "\t\"name\": \"Cake\",\n" +
            "\t\"image\":\n" +
            "\t\t{\n" +
            "\t\t\t\"url\": \"images/0001.jpg\",\n" +
            "\t\t\t\"width\": 200,\n" +
            "\t\t\t\"height\": 200\n" +
            "\t\t},\n" +
            "\t\"thumbnail\":\n" +
            "\t\t{\n" +
            "\t\t\t\"url\": \"images/thumbnails/0001.jpg\",\n" +
            "\t\t\t\"width\": 32,\n" +
            "\t\t\t\"height\": 32\n" +
            "\t\t}\n" +
            "}"
    val strJson = "{ \"id\": \"0001\", \"type\": \"donut\", \"name\": \"Cake\", \"image\": { \"url\": \"images/0001.jpg\", \"width\": 200, \"height\": 200 }, \"thumbnail\": { \"url\": \"images/thumbnails/0001.jpg\", \"width\": 32, \"height\": 32 } }"


    @Test
    fun testParse() {
        val parser = JsonParser.parser()
        val objs = parser.parse(json)
        var str = ""
        objs?.forEach {
            str += it.toString()
        }

        assertEquals(strJson, str)
    }

    val json2 = "{\n" +
            "    \"/\": {\n" +
            "        \"storage\": {\n" +
            "            \"type\": \"disk\",\n" +
            "            \"device\": \"/dev/sda1\"\n" +
            "        },\n" +
            "        \"fstype\": \"btrfs\",\n" +
            "        \"readonly\": true\n" +
            "    },\n" +
            "    \"/var\": {\n" +
            "        \"storage\": {\n" +
            "            \"type\": \"disk\",\n" +
            "            \"label\": \"8f3ba6f4-5c70-46ec-83af-0d5434953e5f\"\n" +
            "        },\n" +
            "        \"fstype\": \"ext4\",\n" +
            "        \"options\": [ \"nosuid\" ]\n" +
            "    },\n" +
            "    \"/tmp\": {\n" +
            "        \"storage\": {\n" +
            "            \"type\": \"tmpfs\",\n" +
            "            \"sizeInMB\": 64\n" +
            "        }\n" +
            "    },\n" +
            "    \"/var/www\": {\n" +
            "        \"storage\": {\n" +
            "            \"type\": \"nfs\",\n" +
            "            \"server\": \"my.nfs.server\",\n" +
            "            \"remotePath\": \"/exports/mypath\"\n" +
            "        }\n" +
            "    }\n" +
            "}"

    val strJson2 = "{ \"/\": { \"storage\": { \"type\": \"disk\", \"device\": \"/dev/sda1\" }," +
            " \"fstype\": \"btrfs\", \"readonly\": true }, \"/var\": { \"storage\": { \"type\": " +
            "\"disk\", \"label\": \"8f3ba6f4-5c70-46ec-83af-0d5434953e5f\" }, \"fstype\": \"ext4\", " +
            "\"options\": [ \"nosuid\" ] }, \"/tmp\": { \"storage\": " +
            "{ \"type\": \"tmpfs\", \"sizeInMB\": 64 } }, \"/var/www\": " +
            "{ \"storage\": { \"type\": \"nfs\", \"server\": \"my.nfs.server\", " +
            "\"remotePath\": \"/exports/mypath\" } } }"




    @Test
    fun testParse2() {
        val parser = JsonParser.parser()
        val objs = parser.parse(json2)
        var str = ""
        objs?.forEach {
            str += it.toString()
        }

        assertEquals(strJson2, str)
    }
}