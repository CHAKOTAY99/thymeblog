package org.chak;

public class SiteProperties {

    private String input;
    private String templates;
    private String assets;
    private String title;
    private String baseUrl;

    public SiteProperties() {
        // no arg constructor for snakeyaml
    }

    public String getInput() {
        return input;
    }

    public void setInput(final String input) {
        this.input = input;
    }

    public String getTemplates() {
        return templates;
    }

    public void setTemplates(final String templates) {
        this.templates = templates;
    }

    public String getAssets() {
        return assets;
    }

    public void setAssets(final String assets) {
        this.assets = assets;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
