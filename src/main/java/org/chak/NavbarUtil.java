package org.chak;

import java.util.ArrayList;
import java.util.List;

public class NavbarUtil {

    private NavbarUtil() {}

    public static List<NavbarEntry> createNavBar(final List<MarkdownPage> markdownPages) {

        final List<MarkdownPage> indexes = markdownPages.stream()
                .filter(markdownPage -> markdownPage.metadata().index())
                .filter(markdownPage -> !markdownPage.metadata().draft())
                .toList();
        final List<NavbarEntry> navbarEntries = new ArrayList<>();

        for (final MarkdownPage page : indexes) {
            navbarEntries.add(new NavbarEntry(page.metadata().title(), '/' + page.metadata().sourcePath().toString().replace(".md", ".html"))); // hardcoded '/'
        }
        return navbarEntries;
    }
}
