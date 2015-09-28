package ga.asev.ui.view.serial;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import ga.asev.dao.SerialDao;
import ga.asev.model.Serial;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@UIScope
@SpringView(name = SerialListView.NAME)
public class SerialListView extends VerticalLayout implements View {
    public static final String NAME = "Serials";

    @Autowired
    private SerialDao serialDao;

    BeanItemContainer<Serial> serialContainer = new BeanItemContainer<>(Serial.class);
    private Grid serials = new Grid();

    @PostConstruct
    void init() {
        configureComponents();
        buildLayout();
    }

    private void buildLayout() {
        setMargin(true);
        setSizeFull();

        setDefaultComponentAlignment(Alignment.TOP_CENTER);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setSpacing(true);

        Component serialInfoLayout = buildSerialInfoLayout();
        horizontalLayout.addComponent(serialInfoLayout);
        horizontalLayout.setExpandRatio(serialInfoLayout, 1);

        horizontalLayout.addComponent(serials);

        addComponent(horizontalLayout);
    }

    private Component buildSerialInfoLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        Panel panelWrap = new Panel(verticalLayout);
        panelWrap.setSizeFull();
        return panelWrap;
    }

    private void configureComponents() {
        configureSerials();
    }

    private void configureSerials() {
        serials.setImmediate(true);
        serials.setContainerDataSource(serialContainer);
        serials.setColumnOrder("name", "publishEpisode");
        serials.removeColumn("id");
        serials.removeColumn("publishDate");

        serials.setWidth("320px");
        serials.setHeight("100%");

        int epColWidth = 60;
        int scrollWidth = 17;

        Grid.Column nameCol = serials.getColumn("name");
        nameCol.setHeaderCaption("Name");
        nameCol.setExpandRatio(1);
        nameCol.setWidth(serials.getWidth() - epColWidth - scrollWidth);

        Grid.Column epCol = serials.getColumn("publishEpisode");
        epCol.setHeaderCaption("Ep");
        epCol.setWidth(epColWidth);

        serials.setSelectionMode(Grid.SelectionMode.SINGLE);
        serials.addSelectionListener(e -> onEpisodeSelect((Serial) serials.getSelectedRow()));

        refreshSerials();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // the view is constructed in the init() method()
    }

    private void refreshSerials() {
        List<Serial> serials = serialDao.selectNewSerials();
        serialContainer.removeAllItems();
        serialContainer.addAll(serials);
    }

    private void onEpisodeSelect(Serial serial) {
        if (serial == null) return;
        // todo
    }



}
