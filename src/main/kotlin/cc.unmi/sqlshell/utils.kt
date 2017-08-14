package cc.unmi.sqlshell

import org.jaxen.dom.DOMXPath
import org.w3c.dom.Node


fun Node.selectText(xpath: String): String {
    return (DOMXPath(xpath).selectSingleNode(this) as Node).textContent
}

fun Node.selectNodes(xpath: String): List<Node> {
    return DOMXPath(xpath).selectNodes(this).map { any -> any as Node }.toList()
}
