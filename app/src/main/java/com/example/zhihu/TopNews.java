package com.example.zhihu;

public class TopNews {
    private String title;
    private String hint;
    private String image;
    private String url;

    public TopNews(String title, String hint, String image,String url) {
        this.title = title;
        this.hint = hint;
        this.image = image;
        this.url=url;
    }

    public String getTitle() {
        return title;
    }

    public String getHint() {
        return hint;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}
