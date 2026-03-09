import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode

data class HtmlSpan(
    val start: Int,
    val end: Int,
    val style: HtmlStyle
)

enum class HtmlStyle {
    ITALIC, BOLD, BOLD_ITALIC
}

data class ParsedHtml(
    val text: String,
    val spans: List<HtmlSpan>
)

fun extractTextFromHtml(html: String): ParsedHtml {
    val doc = Ksoup.parse(html = html)
    val builder = StringBuilder()
    val spans = mutableListOf<HtmlSpan>()

    fun traverse(node: Node, isBold: Boolean = false, isItalic: Boolean = false) {
        when (node) {
            is TextNode -> {
                val text = node.text()
                if (text.isNotEmpty()) {
                    val start = builder.length
                    builder.append(text)
                    val end = builder.length
                    if (isBold && isItalic) {
                        spans.add(HtmlSpan(start, end, HtmlStyle.BOLD_ITALIC))
                    } else if (isBold) {
                        spans.add(HtmlSpan(start, end, HtmlStyle.BOLD))
                    } else if (isItalic) {
                        spans.add(HtmlSpan(start, end, HtmlStyle.ITALIC))
                    }
                }
            }

            is Element -> {
                val tagName = node.tagName().lowercase()
                if (tagName == "br") {
                    builder.append("\n")
                    return
                }
                if (tagName == "p" && builder.isNotEmpty()) {
                    builder.append("\n\n")
                }
                val newBold = isBold || tagName in listOf("b", "strong")
                val newItalic = isItalic || tagName in listOf("i", "em")
                for (child in node.childNodes()) {
                    traverse(child, newBold, newItalic)
                }
            }
        }
    }

    traverse(doc.body())
    return ParsedHtml(text = builder.toString().trim(), spans = spans)
}