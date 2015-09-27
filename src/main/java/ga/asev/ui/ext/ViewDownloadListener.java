package ga.asev.ui.ext;

import ga.asev.model.UserSerialNotification;
import ga.asev.ui.view.favorite.FavoriteListView;

import java.util.Observable;
import java.util.Observer;

public class ViewDownloadListener implements Observer {
    FavoriteListView view;

    public ViewDownloadListener(FavoriteListView view) {
        this.view = view;
    }

    @Override
    public void update(Observable o, Object arg) {
        UserSerialNotification notification = (UserSerialNotification) arg;

        view.getUI().access(() -> view.showNotification(notification));
    }
}
