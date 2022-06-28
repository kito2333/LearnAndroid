package com.example.helloworld.web

import org.xml.sax.InputSource
import java.io.StringReader
import java.lang.Exception
import javax.xml.parsers.SAXParserFactory

class XMLDataSaxParser : DataParser {
    override fun parse(data: String) {
        try {
            val factory = SAXParserFactory.newInstance()
            val xmlReader = factory.newSAXParser().xmlReader
            val handler = SAXContentHandler()
            xmlReader.contentHandler = handler
            xmlReader.parse(InputSource(StringReader(data)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}