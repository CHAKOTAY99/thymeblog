package org.chak;


import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.ins.InsExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.List;
import java.util.Map;

public class MarkdownProcessor {

    private static final MutableDataSet OPTIONS = new MutableDataSet();
    private final Parser parser;
    private final HtmlRenderer htmlRenderer;

    public MarkdownProcessor() {
        OPTIONS.set(Parser.EXTENSIONS, List.of(
                StrikethroughExtension.create(),
                TaskListExtension.create(),
                TablesExtension.create(),
                FootnoteExtension.create(),
                DefinitionExtension.create(),
                EmojiExtension.create(),
                InsExtension.create(),
                YamlFrontMatterExtension.create()
        ));
        parser = Parser.builder(OPTIONS).build();
        htmlRenderer = HtmlRenderer.builder(OPTIONS).build();
    }


    public MarkdownPage convertToHtml(final String markdownDocument) {
        final Node document = parser.parse(markdownDocument);

        final AbstractYamlFrontMatterVisitor visitor = new AbstractYamlFrontMatterVisitor();
        visitor.visit(document);

        final Map<String, List<String>> data = visitor.getData();

        final Metadata metadata = Metadata.parse(data);
        final String html = htmlRenderer.render(document);

        return new MarkdownPage(metadata, html);
    }

    public Metadata getMetadata(final String markdownDocument) {
        final Node document = parser.parse(markdownDocument);

        final AbstractYamlFrontMatterVisitor visitor = new AbstractYamlFrontMatterVisitor();
        visitor.visit(document);

        final Map<String, List<String>> data = visitor.getData();

        return Metadata.parse(data);
    }
}
