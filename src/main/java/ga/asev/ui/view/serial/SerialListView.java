package ga.asev.ui.view.serial;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import javax.annotation.PostConstruct;

@UIScope
@SpringView(name = SerialListView.NAME)
public class SerialListView extends VerticalLayout implements View {
    public static final String NAME = "Serials";

    @PostConstruct
    void init() {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
    }

    private void buildLayout() {
        setMargin(true);
        setSpacing(true);
        setSizeFull();

        setDefaultComponentAlignment(Alignment.TOP_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // the view is constructed in the init() method()
    }

}
