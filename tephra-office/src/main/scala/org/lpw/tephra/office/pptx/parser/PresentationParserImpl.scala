package org.lpw.tephra.office.pptx.parser

import javax.inject.Inject
import org.lpw.tephra.office.OfficeHelper
import org.springframework.stereotype.Component

import scala.xml.{NodeSeq, XML}

/**
  * @author lpw
  */
@Component("tephra.office.pptx.parser.presentation")
class PresentationParserImpl extends Parser {
    @Inject private val officeHelper: OfficeHelper = null

    override def getType: Parsers.Type = Parsers.Type.Presentation

    override def parse(path: String, parserObject: ParserObject): Unit = {
        val sldSz: NodeSeq = XML.loadFile(path) \ "sldSz"
        parserObject.setWidth(officeHelper.emuToPixel((sldSz \ "@cx").text.toInt))
        parserObject.setHeight(officeHelper.emuToPixel((sldSz \ "@cy").text.toInt))
    }
}
