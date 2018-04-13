# kson in kotlin [com.moom.kson]

JSON is a text syntax that facilities structured data interchange between all programming languages. See http://www.JSON.org

The files in this package implement JSON encoder/decoder in Kotlin.

The package compiles on Kotlin 1.2.31 with JDK 1.8 environment

### kson goals
  * Provide simple way to support convert between JSON and Kotlin token objects
  * Provide simple way to make JSON with programming
  * Provide ways to convert between JSON and another annotation languages such as XML (TODO)
  * Provide ways to support convert between JSON and actual Kotlin objects(TODO)


### Package files
**JsonParser.kt**: The `JsonParser` interface defines methods of parsing JSON from Reader/InputStream/String to token objects array.
It provides companion object method parser() to get an implementation to do the actual parsing work
```parse a JSON
// json is a JSON Reader, InputStream, or String
val tokens = JsonParser.parser().parse(json)
tokens.forEach { it ->
    //it is a JsonToken object, and generally it's a JsonArray or JsonObject
    it.toString()
}
```

**Tokens.kt**： Defines classes of Json tokens and methods for programming json easily. It defines `JsonToken`, `JsonNull`, `JsonBoolean`, `JsonNumber`, `JsonString`, `JsonElement`, `JsonCollectiveToken`, `JsonObject` and `JsonArray`.
With the methods defined in the file, a JSON text can be built easily with following programing method
```json tokens
val json = obj { 
    e {"id" to "0001"}
    e {"type" to "donut"}
    e {"name" to "Cake"}
    e {"image" to obj { 
        e {"url" to "images/0001.jpg"}
        e {"width" to 200}
        e {"height" to 200}
      }
    }
    e {"thumbnail" to obj { 
        e {"url" to "images/thumbnails/0001.jpg"}
        e {"width" to 32}
        e {"height" to 32} 
      }
    }
 }.toString()
```

**JsonReader.kt**: The `JsonReader` decodes JSON text into string tokens. It invokes a callback to handle each string token

**TokenQueue.kt**: The `TokenQueue` class defines a FIFO queue to store string tokens decoded by `JsonReader`

**JsonReaderThread.kt**: The `JsonReaderThread` implements a JSON string token producer thread, it puts string tokens decoded by `JsonReader` to a `TokenQueue`

**JsonBuilder.kt**: The `JsonBuilder` convert string tokens to token objects and construct the JSON token tree

**JsonBuilderThread.kt**: The `JsonBuilderThread` implements a JSON string token consumer thread, it gets string tokens from `TokenQueue`

**ValueBuilder.kt**: `ValueBuilder` is the base class of all token object builder classes

**ElementBuilder.kt**: `ElementBuilder` is the builder for `JsonElement`

**ArrayBuilder.kt**: `ArrayBuilder` is the builder for `JsonArray`

**ObjectBuilder.kt**: `ObjectBuilder` is the builder for `JsonObject`


### Release History:

~~~
20180420 first commit
~~~

### TODO
1.Add methods to get values from JSON objects
2.conversion between JSON and XML


### License

kson is released under the [Apache 2.0 license](LICENSE.txt).

```
Copyright 2018 Moomtain.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
