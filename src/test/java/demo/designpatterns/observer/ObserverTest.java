package demo.designpatterns.observer;

import org.junit.Before;
import org.junit.Test;

import java.util.Observer;

/**
 * Created by wgu on 9/23/2015.
 */
public class ObserverTest {
    Observer observer1;
    Observer observer2;
    @Before
    public void createObservers() {
        observer1 = new ConcreteObserver();
        ((ConcreteObserver)observer1).setObserverName("Robbie Gu");
        observer2 = new ConcreteObserver();
        ((ConcreteObserver)observer2).setObserverName("Wenjun Gu");
    }
    @Test
    public void observer1() {
        ConcreteWeatherSubject subject = new ConcreteWeatherSubject();
        subject.addObserver(observer1);
        subject.addObserver(observer2);
        subject.setContent("Go home early today!!!");
    }
}
