package ga.asev.ui.view;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import ga.asev.dao.CurrentEpisodeDao;
import ga.asev.dao.SerialDao;
import ga.asev.model.CurrentEpisode;
import ga.asev.model.Serial;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@UIScope
@SpringView(name = MediaListView.VIEW_NAME)
public class MediaListView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";

    private ComboBox serialCb = new ComboBox();
    private Grid episodes = new Grid();

    @Autowired
    CurrentEpisodeDao currentEpisodeDao;

    @Autowired
    SerialDao serialDao;

    @PostConstruct
    void init() {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        configureSerialForm();
        configureMediaListGrid();
    }

    private void configureMediaListGrid() {
        episodes.setContainerDataSource(new BeanItemContainer<>(CurrentEpisode.class));
        episodes.setColumnOrder("name", "episodeString", "lastUpdated");
        episodes.removeColumn("id");
        episodes.removeColumn("episode");
        episodes.removeColumn("lastUpdated");

        episodes.getColumn("name").setHeaderCaption("Episode Name");
        episodes.getColumn("episodeString").setHeaderCaption("Episode");

        episodes.setSelectionMode(Grid.SelectionMode.SINGLE);
        episodes.addSelectionListener(e -> onEpisodeSelect((CurrentEpisode) episodes.getSelectedRow()));

        refreshEpisodes();
    }

    private void configureSerialForm() {
        serialCb.setInputPrompt("Chose serial...");

        serialCb.setFilteringMode(FilteringMode.CONTAINS);
        serialCb.setPageLength(8);
        serialCb.setNullSelectionAllowed(false);
        serialCb.addValueChangeListener(e -> onSerialSelect((Serial)serialCb.getValue()));

        refreshSerials();
    }

    private void buildLayout() {
        setMargin(true);
        setSpacing(true);
        setSizeFull();

        setDefaultComponentAlignment(Alignment.TOP_CENTER);

        Button addBtn = new Button("Add", this::addSerial);
        addBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
        addBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        addBtn.setWidth("100px");

        Button delBtn = new Button("Delete", this::delSerial);
        delBtn.setStyleName(ValoTheme.BUTTON_DANGER);
        delBtn.setClickShortcut(ShortcutAction.KeyCode.DELETE);
        delBtn.setWidth("100px");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setSpacing(true);

        horizontalLayout.addComponent(serialCb);
        horizontalLayout.setExpandRatio(serialCb, 1);
        serialCb.setWidth("100%");

        horizontalLayout.addComponent(addBtn);

        horizontalLayout.addComponent(delBtn);

        addComponent(horizontalLayout);

        addComponent(episodes);
        episodes.setSizeFull();
        setExpandRatio(episodes, 1);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // the view is constructed in the init() method()
    }

    private void refreshSerials() {
        List<Serial> serials = serialDao.selectAllSerials();
        serialCb.removeAllItems();
        BeanItemContainer<Serial> container = new BeanItemContainer<>(Serial.class, serials);
        serialCb.setContainerDataSource(container);
        serialCb.setItemCaptionPropertyId("name");
    }

    private void refreshEpisodes() {
        List<CurrentEpisode> torrents = currentEpisodeDao.selectAllTorrents();
        episodes.setContainerDataSource(new BeanItemContainer<>(CurrentEpisode.class, torrents));
    }

    public void addSerial(Button.ClickEvent event) {
        Serial value = (Serial) serialCb.getValue();
        if (value == null) return;

        CurrentEpisode episode = new CurrentEpisode();
        episode.setName(value.getName());
        episode.setPublishDate(value.getPublishDate());
        CurrentEpisode inserted = currentEpisodeDao.insertTorrent(episode);
        if (inserted != null) {
            refreshEpisodes();
            Notification.show("Added '" + value.getName() + "'", Notification.Type.TRAY_NOTIFICATION);
        }
    }

    public void delSerial(Button.ClickEvent event) {
        CurrentEpisode episode = (CurrentEpisode) episodes.getSelectedRow();
        if (episode == null) return;

        currentEpisodeDao.deleteTorrent(episode.getId());
        refreshEpisodes();
        Notification.show("Deleted '" + episode.getName() + "'", Notification.Type.TRAY_NOTIFICATION);
    }

    private void onEpisodeSelect(CurrentEpisode episode) {
        if (episode == null) return;
        serialCb.setValue(episode.getName());
        serialCb.select(new Serial(episode.getName(), null));
    }

    private void onSerialSelect(Serial serial) {
        if (serial == null) return;
        selectEpisodeBySerial(serial);
    }

    private void selectEpisodeBySerial(Serial serial) {
        CurrentEpisode episode = (CurrentEpisode) episodes.getContainerDataSource().getItemIds()
                .stream().filter(o -> ((CurrentEpisode) o).getName().equals(serial.getName()))
                .findFirst().orElse(null);
        if (episode != null) {
            episodes.select(episode);
        }
    }
}
