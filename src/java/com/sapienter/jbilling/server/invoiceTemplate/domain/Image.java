package com.sapienter.jbilling.server.invoiceTemplate.domain;

/**
 * @author elmot
 */
public class Image extends DocElement {

//    private Source imageSource;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Source getImageSource() {
        return imageUrl != null && imageUrl.startsWith("data") ? Source.File : Source.URL;
    }

    /*public void setImageSource(Source imageSource) {
        this.imageSource = imageSource;
    }*/

    @Override
    public void visit(Visitor visitor) {
        visitor.accept(this);
    }

    public enum Source { URL, File }
}
