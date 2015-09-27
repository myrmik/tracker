package ga.asev.ui.view;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringComponent
public class MainView extends HorizontalLayout {

    @Autowired
    MainMenu mainMenu;

    private ComponentContainer contentContainer;

    @PostConstruct
    void init() {
        setSizeFull();

        addStyleName("mainview");
        setDefaultComponentAlignment(Alignment.TOP_CENTER);

        addComponent(mainMenu);

        contentContainer = new CssLayout();
        contentContainer.addStyleName("view-content");
        contentContainer.setSizeFull();
        contentContainer.setWidth("70%");
        addComponent(contentContainer);
        setExpandRatio(contentContainer, 1.0f);
    }

    public ComponentContainer getContentContainer() {
        return contentContainer;
    }
}
