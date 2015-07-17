package ga.asev.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

@Theme("valo")
@SpringUI
@Push
public class DefaultUI extends UI {

    @Autowired
    private SpringViewProvider viewProvider;

    private VerticalLayout root = new VerticalLayout();
    private Panel viewContainer = new Panel();
    private Navigator navigator = new Navigator(this, viewContainer);

    @Override
    protected void init(VaadinRequest request) {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        navigator.addProvider(viewProvider);
    }

    private void buildLayout() {
        viewContainer.setSizeFull();
        viewContainer.setWidth("70%");

        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);

        root.setDefaultComponentAlignment(Alignment.TOP_CENTER);

        root.addComponent(viewContainer);
        root.setExpandRatio(viewContainer, 1);

        setContent(root);
    }
}
