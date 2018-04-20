package org.lpw.tephra.office.pptx

import com.alibaba.fastjson.JSONObject
import org.springframework.stereotype.Component

import scala.xml.XML

/**
  * @author lpw
  */
@Component("tephra.office.pptx.parser.rels")
class RelsParserImpl extends Parser {
    override def getName: String = "rels"

    override def parse(path: String, rels: JSONObject): JSONObject = {
        val json: JSONObject = new JSONObject()
        (XML.loadFile(path) \ "Relationship").foreach(node => {
            var t: String = (node \ "@Type").text
            t = t.substring(t.lastIndexOf('/') + 1)
            if (t == "image") {
                val target: String = (node \ "@Target").text.replaceFirst("..", "/ppt")
                if (rels.containsKey(target))
                    json.put((node \ "@Id").text, rels.getJSONObject(target))
            }
        })

        json
    }
}
