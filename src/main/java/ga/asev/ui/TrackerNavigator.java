package ga.asev.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import ga.asev.event.TrackerEvent.BrowserResizeEvent;
import ga.asev.event.TrackerEvent.CloseOpenWindowsEvent;
import ga.asev.event.TrackerEvent.PostViewChangeEvent;
import ga.asev.event.TrackerEventBus;
import ga.asev.ui.view.TrackerViewType;

public class TrackerNavigator extends Navigator {

    private TrackerEventBus trackerEventBus;

    public TrackerNavigator(UI ui, final ComponentContainer container, TrackerEventBus trackerEventBus) {
        super(ui, container);
        this.trackerEventBus = trackerEventBus;

        initViewChangeListener();
    }

    private void initViewChangeListener() {
        addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(final ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(final ViewChangeEvent event) {
                TrackerViewType view = TrackerViewType.getByViewName(event.getViewName());
                // Appropriate events get fired after the view is changed.
                trackerEventBus.post(new PostViewChangeEvent(view));
                trackerEventBus.post(new BrowserResizeEvent());
                trackerEventBus.post(new CloseOpenWindowsEvent());
            }
        });

    }
}
