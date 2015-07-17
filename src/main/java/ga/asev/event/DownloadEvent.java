package ga.asev.event;

import org.springframework.stereotype.Component;

import java.util.Observable;

@Component
public class DownloadEvent extends Observable {
    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }
}
