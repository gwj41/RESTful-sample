package demo.designpatterns.observer;

import java.util.Observable;

/**
 * Created by wgu on 9/23/2015.
 */
public class ConcreteWeatherSubject extends Observable {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.setChanged();
        this.notifyObservers(content);
    }
}
