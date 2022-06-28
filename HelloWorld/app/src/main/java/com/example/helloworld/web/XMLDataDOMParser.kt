package com.example.helloworld.web

import android.util.Log
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.InputStream
import java.io.StringReader
import java.lang.Exception
import javax.xml.parsers.DocumentBuilderFactory

class XMLDataDOMParser : DataParser {
    companion object {
        const val TAG = "XMLDataDOMParser"
    }

    override fun parse(data: String) {
        var id = ""
        var name = ""
        var version = ""
        try {

            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(InputSource(StringReader(data)))
            // apps
            val root = document.documentElement
            val apps = root.getElementsByTagName("app")

            for (i in 0 until apps.length) {
                val element = apps.item(i)
                val childNodes = element.childNodes
                for (j in 0 until childNodes.length) {
                    val child = childNodes.item(j)
                    if (child.nodeType == Node.ELEMENT_NODE) {
                        when (child.nodeName) {
                            "id" -> id = child.firstChild.nodeValue
                            "name" -> name = child.firstChild.nodeValue
                            "version" -> version = child.firstChild.nodeValue
                        }
                    }
                }
                Log.d(TAG, "id is $id, name is $name, version is $version")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}