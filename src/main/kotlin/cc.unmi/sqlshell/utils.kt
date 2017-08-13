package cc.unmi.sqlshell

import org.dom4j.Node

object XPath {
    fun selectText(xpath: String, node: Node): String {
       return node.selectSingleNode(xpath).text
    }

    fun selectNodes(xpath: String, node: Node): List<Node> {
        return node.selectNodes(xpath)
    }
}