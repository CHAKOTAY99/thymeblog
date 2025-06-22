package org.chak;

public class SiteProperties {

    // Getters and Setters are required for snakeyaml
    private String templates;
    private String assets;
    private String title;
    private String baseUrl;
    private String copyright;
    private String directoryName;

    public SiteProperties() {
        // no arg constructor for snakeyaml
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

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(final String copyright) {
        this.copyright = copyright;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(final String directoryName) {
        this.directoryName = directoryName;
    }
}
