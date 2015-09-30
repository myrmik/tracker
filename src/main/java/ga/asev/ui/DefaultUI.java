package ga.asev.ui;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import ga.asev.event.TrackerEvent.BrowserResizeEvent;
import ga.asev.event.TrackerEvent.CloseOpenWindowsEvent;
import ga.asev.event.TrackerEventBus;
import ga.asev.ui.view.MainView;
import ga.asev.ui.view.TrackerViewType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

@Theme("tracker")
@SpringUI
@Push
public class DefaultUI extends UI {

    private static final TrackerViewType ERROR_VIEW = TrackerViewType.FAVORITES;

    @Autowired
    private SpringViewProvider viewProvider;

    @Autowired
    private TrackerEventBus trackerEventBus;

    @Autowired
    private MainView mainView;

    @Override
    protected void init(VaadinRequest request) {
        setContent(mainView);

        Responsive.makeResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);

        initNavigator();
        initListeners();
    }

    private void initNavigator() {
        TrackerNavigator navigator = new TrackerNavigator(this, mainView.getContentContainer(), trackerEventBus);
        navigator.addProvider(viewProvider);
        navigator.setErrorProvider(new ViewProvider() {
            @Override
            public String getViewName(final String viewAndParameters) {
                return ERROR_VIEW.getViewName();
            }

            @Override
            public View getView(final String viewName) {
                return viewProvider.getView(ERROR_VIEW.getViewName());
            }
        });

        navigator.navigateTo(navigator.getState());
    }

    private void initListeners() {
        // Some views need to be aware of browser resize events so a
        // BrowserResizeEvent gets fired to the event bus on every occasion.
        Page.getCurrent().addBrowserWindowResizeListener(
                event -> trackerEventBus.post(new BrowserResizeEvent()));
    }

    @Subscribe
    public void closeOpenWindows(final CloseOpenWindowsEvent event) {
        getWindows().forEach(Window::close);
    }
}
