package io.plaidapp.data;

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

    public String getContent() {
        return this.getString(CONTENT_KEY);
    }

    public void setContent(String content) {
        this.put(CONTENT_KEY, content);
    }
}