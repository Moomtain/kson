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

import com.moom.kson.token.*
import java.io.*
import kotlin.String

const val DUMP = false

fun main(args: kotlin.Array<String>) {

    if (args.isEmpty()) {
        usage()
        return
    }

    when(args[0]) {
        "-p" -> {
            if (args.size<2) usage()
            else
                readJsonPerformance(args[1])
        }

        "-k" -> testkson()
        "-r" -> {
            if (args.size<2) usage()
            else
                readJson(args[1])
        }

        "-tp" -> testPatterns()
        "-h" -> usage()

    }
}

fun usage() {
    println("test command argument")
    println("command:")
    println("\t-p jsonfile\t\tperformance test with specific json file")
    println("\t-k\t\t\toutput the json text built with programming method")
    println("\t-r jsonfile\t\tread the jsonfile and output it with different format")
    println("\t-tp\t\t\ttest regular patterns for json tokens")
    println("\t-h\t\t\tshow this help")
    println("\nExamples:")
    println("test -p /data/Learning/JSON/15.json")
    println("test -k")
    println("test -r /data/Learning/JSON/15.json")
    println("test -tp")
}

fun readJsonPerformance(file: String) {
    println("==============================================")
    parseJsonFromFileSingle(file)
    println()
    parseJsonFromFileMultipleThread(file)
    println()
    parseJsonFromStringMultipleThread(file)
    println("==============================================")

}

fun parseJsonFromFileSingle(file: String) {
    try {
        println("Single Thread: Parsing json $file")
        val start = System.nanoTime()
        val ar = JsonParser.parser(false).parse(FileInputStream(file))
        val end = System.nanoTime()
        println("Takes time = ${(end-start)/1000}us")

        if (DUMP) {
            ar!!.forEach { it ->
                println(it.toIndentedString())
            }
        }

    } catch(e: Exception) {
        println("Exception: $e")
    }
}

fun parseJsonFromFileMultipleThread(file: String) {
    try {
        println("Multiple Threads: Parsing json $file")
        val start = System.nanoTime()
        val b = JsonParser.parser().parse(FileInputStream(file))
        val end = System.nanoTime()
        println("Takes time = ${(end-start)/1000}us")

        if (DUMP) {
            b!!.forEach { it ->
                println(it.toIndentedString())
            }
        }

    } catch(e: Exception) {
        println("Exception: $e")
    }
}

fun parseJsonFromStringMultipleThread(file: String) {
    try {

        //val start = System.nanoTime()
        var text = ""
        val ca = CharArray(1024)
        val r = FileReader(file)
        var count:Int

        val start = System.nanoTime()
        do {
            count = r.read(ca)
            if (count<=0) break //EOF

            for(i in 0 until count) text += ca[i].toString()
        }while(true)

        println("Multiple Threads: Parsing json from string")

        val b = JsonParser.parser().parse(text)
        val end = System.nanoTime()
        println("Takes time = ${(end-start)/1000}us")

        if (DUMP) {
            b!!.forEach { it ->
                println(it.toIndentedString())
            }
        }

    } catch(e: Exception) {
        println("Exception: $e")
    }
}

fun readJson(file: String) {
    try {
        val ar = JsonParser.parser().parse(FileInputStream(file))

        ar?.forEachIndexed { index, token ->
            println("Item#$index ->")
            println("simple format:")
            println("$token\n")
            println("Indented format:")
            println("${token.toIndentedString()}\n")
        }

    } catch(e: Exception) {
        println("Exception: $e")
    }
}

fun testkson() {
    val hh = "Hello"
    val hv = "World"

    val kson = obj {
        e { "Middle Name" to "Moom" }
        e { "First Name" to "mmt" }
        e { "Last Name" to "c" }
        e { "Age" to 43 }
        e { "Gender" to true }
        e { "Retired" to false }
        e { "Score" to 89.5 }
        e { "Score" to -0.234E-8 }
        e { "comment" to JsonNull() }
        e { "Last Company" to "S" }
        e {
            "Current Company" to {
                +"T."
            }
        }
        e { hh to hv }
        e {
            "Obj" to {
                obj {
                    e { "I1" to "1" }
                    e { "I2" to "2" }
                    e { "I3" to 2456179 }
                    e { "I3" to "III" }
                    e { "I3" to 0.456245355434375E-23 }
                }
            }
        }
        e {
            "Array" to {
                a {
                    v { +"Number#1" }
                    v { !12345 }
                    v { JsonNull() }
                    v { +true }
                    v { +false }
                    v { !3.14159 }
                    v {
                        obj {
                            e { "Name" to "Object in array" }
                            e { "Purpose" to "Try object" }
                            e { "Answer" to 42 }
                            e {
                                "Choice Question" to {
                                    a {
                                        v { +"A" }
                                        v { +"B" }
                                        v { +"C" }
                                        v { +"D" }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    kson.updateIndent()

    println(kson.toIndentedString())
}

fun testPatterns() {
    println("checking whitespace pattern: ${isWhiteSpace("\t\t\n\r\t")}")
    print("checking structural pattern: [: ${isStructuralToken("[")}")
    print(" , ]: ${isStructuralToken("]")}")
    print(" , {: ${isStructuralToken("{")}")
    print(" , }: ${isStructuralToken("}")}")
    print(" , :: ${isStructuralToken(":")}")
    print(" , ,: ${isStructuralToken(",")}\n")

    println("checking null pattern: ${isNullToken("null")}")
    println("checking boolean pattern: true: ${isBooleanToken("true")} , false: ${isBooleanToken(
        "false"
    )}")
    print("checking number pattern: 1234567: ${isNumberToken("1234567")}")
    print(" , -1234567: ${isNumberToken("-1234567")}")
    print(" , 1234.567: ${isNumberToken("1234.567")}")
    print(" , -1234.567: ${isNumberToken("-1234.567")}")
    print(" , 1234.567E12: ${isNumberToken("1234.567E12")}")
    print(" , 1234.567E+12: ${isNumberToken("1234.567E+12")}")
    print(" , 1234.567E-12: ${isNumberToken("1234.567E-12")}")
    print(" , -1234.567E12: ${isNumberToken("-1234.567E12")}")
    print(" , -1234.567E+12: ${isNumberToken("-1234.567E+12")}")
    print(" , -1234.567E-12: ${isNumberToken("-1234.567E-12")}\n")

    var str = "\"hello \""
    println("checking string pattern: $str: ${isStringToken(str)}")
    str = "\"this is a simple \\\" test\""
    println("checking string pattern: $str: ${isStringToken(str)}")
    str = "\"this is a simple \\\" test\\\""
    println("checking string pattern: $str: ${isStringToken(str)}")
    str = "\"this is a simple \" test\""
    println("checking string pattern: $str: ${isStringToken(str)}")
    str = "\"\""
    println("checking string pattern: $str: ${isStringToken(str)}")
    str = "\"h\"e\"l  \"l \" \" o\\\""
    println("checking string pattern: $str: ${isStringToken(str)}")
}