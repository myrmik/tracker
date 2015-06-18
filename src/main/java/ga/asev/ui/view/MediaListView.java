package ga.asev.ui.view;

import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ProgressBarRenderer;
import com.vaadin.ui.themes.ValoTheme;
import ga.asev.dao.UserSerialDao;
import ga.asev.dao.SerialDao;
import ga.asev.model.UserSerial;
import ga.asev.model.Serial;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.vaadin.shared.data.sort.SortDirection.ASCENDING;
import static java.util.Collections.singletonList;

@UIScope
@SpringView(name = MediaListView.VIEW_NAME)
public class MediaListView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";

    private ComboBox serialCb = new ComboBox();
    private Grid userSerials = new Grid();

    @Autowired
    UserSerialDao userSerialDao;

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
        userSerials.setContainerDataSource(new BeanItemContainer<>(UserSerial.class));
        userSerials.setColumnOrder("name", "episodeString", "timeLeft", "timeLeftProgress");
        userSerials.removeColumn("id");
        userSerials.removeColumn("episode");
        userSerials.removeColumn("lastUpdated");
        userSerials.removeColumn("publishDate");
        userSerials.removeColumn("serial");

        userSerials.getColumn("name").setHeaderCaption("Episode Name");
        userSerials.getColumn("episodeString").setHeaderCaption("Episode");

        userSerials.getColumn("timeLeftProgress").setRenderer(new ProgressBarRenderer());

        userSerials.setSortOrder(singletonList(new SortOrder("publishDate", ASCENDING)));

        userSerials.setSelectionMode(Grid.SelectionMode.SINGLE);
        userSerials.addSelectionListener(e -> onEpisodeSelect((UserSerial) userSerials.getSelectedRow()));

        userSerials.setEditorEnabled(true);
        userSerials.getColumn("timeLeft").setEditable(false);
        userSerials.getColumn("timeLeftProgress").setEditable(false);

        refreshEpisodes();
    }

    private void configureSerialForm() {
        serialCb.setInputPrompt("Chose serial...");

        serialCb.setFilteringMode(FilteringMode.CONTAINS);
        serialCb.setPageLength(8);
        serialCb.setNullSelectionAllowed(false);
        serialCb.addValueChangeListener(e -> onSerialSelect((Serial) serialCb.getValue()));

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

        addComponent(userSerials);
        userSerials.setSizeFull();
        setExpandRatio(userSerials, 1);
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
        List<UserSerial> userSerials = userSerialDao.selectAllUserSerials();
        this.userSerials.setContainerDataSource(new BeanItemContainer<>(UserSerial.class, userSerials));
    }

    public void addSerial(Button.ClickEvent event) {
        Serial serial = (Serial) serialCb.getValue();
        if (serial == null) return;

        UserSerial userSerial = new UserSerial();
        userSerial.setName(serial.getName());
        userSerial.setSerial(serial);
        UserSerial inserted = userSerialDao.insertUserSerial(userSerial);
        if (inserted != null) {
            refreshEpisodes();
            Notification.show("Added '" + serial.getName() + "'", Notification.Type.TRAY_NOTIFICATION);
        }
    }

    public void delSerial(Button.ClickEvent event) {
        UserSerial userSerial = (UserSerial) userSerials.getSelectedRow();
        if (userSerial == null) return;

        userSerialDao.deleteUserSerial(userSerial.getId());
        refreshEpisodes();
        Notification.show("Deleted '" + userSerial.getName() + "'", Notification.Type.TRAY_NOTIFICATION);
    }

    private void onEpisodeSelect(UserSerial userSerial) {
        if (userSerial == null) return;
        serialCb.setValue(userSerial.getName());
        serialCb.select(new Serial(userSerial.getName(), null));
    }

    private void onSerialSelect(Serial serial) {
        if (serial == null) return;
        selectEpisodeBySerial(serial);
    }

    private void selectEpisodeBySerial(Serial serial) {
        UserSerial episode = (UserSerial) userSerials.getContainerDataSource().getItemIds()
                .stream().filter(o -> ((UserSerial) o).getName().equals(serial.getName()))
                .findFirst().orElse(null);
        if (episode != null) {
            userSerials.select(episode);
        }
    }
}
