package org.lpw.tephra.office.pptx.parser

import com.alibaba.fastjson.{JSONArray, JSONObject}
import javax.inject.Inject
import org.lpw.tephra.office.OfficeHelper
import org.springframework.stereotype.Component

import scala.xml.{Node, NodeSeq, XML}

/**
  * @author lpw
  */
@Component("tephra.office.pptx.parser.slide")
class SlideParserImpl extends Parser {
    @Inject private val officeHelper: OfficeHelper = null

    override def getType: Parsers.Type = Parsers.Type.Slide

    override def parse(path: String, parserObject: ParserObject): Unit = {
        val name: String = path.substring(path.lastIndexOf('/') + 1)
        val elements: JSONArray = new JSONArray()
        (XML.loadFile(path) \\ "spTree").head.child.foreach(node => {
            if (node.label == "pic")
                image(parserObject, name, node, elements)
            else if (node.label == "sp")
                text(node, elements)
        })
        parserObject.addSlide(elements)
    }

    private def image(parserObject: ParserObject, name: String, node: Node, elements: JSONArray): Unit = {
        val id: String = name + (node \ "blipFill" \ "blip").head.attributes.filter(_.key == "embed").value.text
        val size: Array[Int] = parserObject.getImageSize(id)
        if (size == null)
            return

        val json: JSONObject = new JSONObject()
        json.put("type", "image")
        xywh(node, json)

        json.put("uri", parserObject.getImageUri(id))
        val s: JSONObject = new JSONObject()
        s.put("width", size.apply(0))
        s.put("height", size.apply(1))
        json.put("size", size)

        cropped(node, s, json)
        elements.add(json)
    }

    private def cropped(node: Node, size: JSONObject, json: JSONObject): Unit = {
        val ltrb: JSONObject = new JSONObject()
        (node \ "blipFill" \ "srcRect").head.attributes.foreach(md => ltrb.put(md.key, md.value.text.toInt))
        if (ltrb.isEmpty)
            return

        val cropped: JSONObject = new JSONObject()
        val width: Int = size.getIntValue("width")
        val height: Int = size.getIntValue("height")
        cropped.put("x", width * ltrb.getIntValue("l") / 100000)
        cropped.put("y", height * ltrb.getIntValue("t") / 100000)
        cropped.put("width", width * (ltrb.getIntValue("r") - ltrb.getIntValue("l")) / 100000)
        cropped.put("height", height * (ltrb.getIntValue("b") - ltrb.getIntValue("t")) / 100000)
        json.put("cropped", cropped)
    }

    private def text(node: Node, elements: JSONArray): Unit = {
        val json: JSONObject = new JSONObject()
        json.put("type", "text")
        xywh(node, json)

        println()
        (node \ "txBody" \ "p").foreach(p => {
            println(p)
        })

        elements.add(json)
    }

    private def xywh(node: Node, json: JSONObject): Unit = {
        val nodes: NodeSeq = node \ "spPr" \ "xfrm" \ "off"
        if (nodes.isEmpty)
            return

        val off: Node = (node \ "spPr" \ "xfrm" \ "off").head
        json.put("x", officeHelper.emuToPixel((off \ "@x").text.toInt))
        json.put("y", officeHelper.emuToPixel((off \ "@y").text.toInt))
        val ext: Node = (node \ "spPr" \ "xfrm" \ "ext").head
        json.put("width", officeHelper.emuToPixel((ext \ "@cx").text.toInt))
        json.put("height", officeHelper.emuToPixel((ext \ "@cy").text.toInt))
    }
}
