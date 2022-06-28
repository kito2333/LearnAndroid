package com.example.helloworld.web

import android.util.Log
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.lang.StringBuilder

class SAXContentHandler : DefaultHandler() {
    companion object {
        const val TAG = "SAXContentHandler"
    }

    private var nodeName: String? = ""
    private lateinit var id: StringBuilder
    private lateinit var name: StringBuilder
    private lateinit var version: StringBuilder

    override fun startDocument() {
        id = StringBuilder()
        name = StringBuilder()
        version = StringBuilder()
    }

    override fun startElement(
        uri: String?,
        localName: String?,
        qName: String?,
        attributes: Attributes?
    ) {
        nodeName = localName
        Log.d(
            TAG,
            "uri is $uri, localName is $localName, qName is $qName, attributes is $attributes"
        )
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        when (nodeName) {
            "id" -> id.append(ch, start, length)
            "name" -> name.append(ch, start, length)
            "version" -> version.append(ch, start, length)
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        if ("app" == localName) {
            Log.d(
                TAG,
                "id is ${id.toString().trim()}, name is ${
                    name.toString().trim()
                }, version is ${version.toString().trim()}"
            )
            id.clear()
            name.clear()
            version.clear()
        }

    }

    override fun endDocument() {
    }
}