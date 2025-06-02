package org.chak;

public class SiteProperties {

    private String input;
    private String templates;
    private String css;
    private String images;
    private String favicons;
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

    public String getCss() {
        return css;
    }

    public void setCss(final String css) {
        this.css = css;
    }

    public String getImages() {
        return images;
    }

    public void setImages(final String images) {
        this.images = images;
    }

    public String getFavicons() {
        return favicons;
    }

    public void setFavicons(final String favicons) {
        this.favicons = favicons;
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
