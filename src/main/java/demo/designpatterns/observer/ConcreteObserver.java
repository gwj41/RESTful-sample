package demo.designpatterns.observer;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by wgu on 9/23/2015.
 */
public class ConcreteObserver implements Observer {
    // Observer name
    private String observerName;
    public void update(Observable o, Object arg) {
        System.out.println(observerName + " received pushed message is " + arg);
        System.out.println(observerName + " received pulled message is " + ((ConcreteWeatherSubject)o).getContent());
    }

    public String getObserverName() {
        return observerName;
    }

    public void setObserverName(String observerName) {
        this.observerName = observerName;
    }
}
