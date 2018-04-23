package org.lpw.tephra.office.pptx.parser

import org.springframework.stereotype.Component

import scala.xml.XML

/**
  * @author lpw
  */
@Component("tephra.office.pptx.parser.rels")
class RelsParserImpl extends Parser {
    override def getType: Parsers.Type = Parsers.Type.Rels

    override def parse(path: String, parserObject: ParserObject): Unit = {
        val name: String = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'))
        (XML.loadFile(path) \ "Relationship").foreach(node => {
            var t: String = (node \ "@Type").text
            t = t.substring(t.lastIndexOf('/') + 1)
            if (t == "image")
                parserObject.addImageRel(name + (node \ "@Id").text, (node \ "@Target").text.replaceFirst("..", "/ppt"))
        })
    }
}
