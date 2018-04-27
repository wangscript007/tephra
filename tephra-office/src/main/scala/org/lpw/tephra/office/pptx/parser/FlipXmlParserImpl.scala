package org.lpw.tephra.office.pptx.parser

import com.alibaba.fastjson.JSONObject
import org.springframework.stereotype.Component

import scala.xml.XML

/**
  * @author lpw
  */
@Component("tephra.office.pptx.parser.flip.xml")
class FlipXmlParserImpl extends FlipXmlParser {
    override def putScene3d(xml: String, flip: JSONObject): Unit = {
        (XML.loadString(xml) \ "spPr" \ "scene3d" \ "camera" \ "rot").foreach(node => {
            node.attributes.foreach(md => {
                if (md.key == "lon" && md.value.text == "10800000")
                    flip.put("vertical", true)
                else if (md.key == "lat" && md.value.text == "10800000")
                    flip.put("horizontal", true)
            })
        })
    }
}
