package com.olddriver.data;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * @author Danny
 * @ClassName: XXXX
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date
 */
@AVClassName(Todo.TODO_CLASS)
public class Todo extends AVObject {

    static final String TODO_CLASS = "Todo";
    private static final String CONTENT_KEY = "content";
    private static final String IMAGE_URL = "image_url";
    public String getContent() {
        return this.getString(CONTENT_KEY);
    }

    public void setContent(String content) {
        this.put(CONTENT_KEY, content);
    }


    public String getImageURL() {
        return this.getString(IMAGE_URL);
    }

    public void setImageURL(String ImageURL) {
        this.put(IMAGE_URL, ImageURL);
    }
}