package org.chak;


import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

public class MarkdownProcessor {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

    public String convertToHtml(final String markdownDocument) {
        final Node document = parser.parse(markdownDocument);
        return htmlRenderer.render(document);
    }

}
