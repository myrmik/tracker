package ga.asev.ui.view;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ProgressBarRenderer;
import com.vaadin.ui.themes.ValoTheme;
import ga.asev.dao.SerialDao;
import ga.asev.dao.UserSerialDao;
import ga.asev.event.DownloadEvent;
import ga.asev.model.Serial;
import ga.asev.model.UserSerial;
import ga.asev.model.UserSerialNotification;
import ga.asev.service.UserSerialService;
import ga.asev.ui.ext.PostCommitHandler;
import ga.asev.ui.ext.ViewDownloadListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

import static com.vaadin.shared.data.sort.SortDirection.DESCENDING;
import static ga.asev.util.ThreadUtil.startThread;
import static java.util.Collections.singletonList;

@UIScope
@SpringView(name = MediaListView.VIEW_NAME)
public class MediaListView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";

    private ComboBox serialCb = new ComboBox();
    private Grid userSerials = new Grid();
    private BeanItemContainer<UserSerial> usContainer = new BeanItemContainer<>(UserSerial.class);

    private ViewDownloadListener downloadListener = new ViewDownloadListener(this);

    @Autowired
    private UserSerialDao userSerialDao;

    @Autowired
    private SerialDao serialDao;

    @Autowired
    private UserSerialService userSerialService;

    @Autowired
    private DownloadEvent downloadEvent;

    @PostConstruct
    void init() {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        configureSerialForm();
        configureMediaListGrid();
        addAttachListener(event -> downloadEvent.addObserver(downloadListener));
        addDetachListener(event -> downloadEvent.deleteObserver(downloadListener));
    }

    private void configureMediaListGrid() {
        userSerials.setImmediate(true);
        userSerials.setContainerDataSource(usContainer);
        userSerials.setColumnOrder("name", "episodeString", "timeLeft", "timeLeftProgress");
        userSerials.removeColumn("id");
        userSerials.removeColumn("episode");
        userSerials.removeColumn("lastUpdated");
        userSerials.removeColumn("publishDate");
        userSerials.removeColumn("serial");
        userSerials.removeColumn("originalName");
        userSerials.removeColumn("notifications");

        userSerials.getColumn("name").setHeaderCaption("Episode Name");
        userSerials.getColumn("episodeString").setHeaderCaption("Episode");

        userSerials.getColumn("timeLeftProgress").setRenderer(new ProgressBarRenderer());

        userSerials.setSortOrder(singletonList(new SortOrder("timeLeftProgress", DESCENDING)));

        userSerials.setSelectionMode(Grid.SelectionMode.SINGLE);
        userSerials.addSelectionListener(e -> onEpisodeSelect((UserSerial) userSerials.getSelectedRow()));

        userSerials.setEditorEnabled(true);
        userSerials.getColumn("timeLeft").setEditable(false);
        userSerials.getColumn("timeLeftProgress").setEditable(false);
        userSerials.getEditorFieldGroup().addCommitHandler(new PostCommitHandler(this::editSerial));
        userSerials.getColumn("episodeString").getEditorField().addValidator(new RegexpValidator("\\d+", "number required"));

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
        usContainer.removeAllItems();
        usContainer.addAll(userSerials);
        usContainer.sort(new String[]{"timeLeftProgress"}, new boolean[]{false});
    }

    public void editSerial(FieldGroup.CommitEvent commitEvent) {
        UserSerial editedItem = (UserSerial)userSerials.getEditedItemId();
        userSerialDao.insertUserSerial(editedItem);
        startThread(() ->
                userSerialService.downloadUserSerial(editedItem)
        );
    }

    public void addSerial(Button.ClickEvent event) {
        Serial serial = (Serial) serialCb.getValue();
        if (serial == null) return;

        UserSerial userSerial = new UserSerial();
        userSerial.setName(serial.getName());
        userSerial.setEpisode(serial.getPublishEpisode());
        userSerial.setSerial(serial);
        userSerial = userSerialDao.insertUserSerial(userSerial);
        if (userSerial != null) {
            refreshEpisodes();
            Notification.show("Added '" + serial.getName() + "'", Notification.Type.TRAY_NOTIFICATION);
        }
    }

    public void delSerial(Button.ClickEvent event) {
        UserSerial userSerial = (UserSerial) userSerials.getSelectedRow();
        if (userSerial == null) return;

        userSerialService.deleteUserSerial(userSerial);
        refreshEpisodes();
        Notification.show("Deleted '" + userSerial.getName() + "'", Notification.Type.TRAY_NOTIFICATION);
    }

    private void onEpisodeSelect(UserSerial userSerial) {
        if (userSerial == null) return;
        serialCb.select(userSerial.getSerial());
    }

    private void onSerialSelect(Serial serial) {
        if (serial == null) return;
        selectEpisodeBySerial(serial);
    }

    private void selectEpisodeBySerial(Serial serial) {
        UserSerial episode = getEpisodeBySerial(serial);
        if (episode != null) {
            userSerials.select(episode);
        }
    }

    private UserSerial getEpisodeBySerial(Serial serial) {
        return (UserSerial) userSerials.getContainerDataSource().getItemIds()
                    .stream().filter(o -> Objects.equals(((UserSerial) o).getSerial().getId(), serial.getId()))
                    .findFirst().orElse(null);
    }

    public void showNotification(UserSerialNotification notification) {
        refreshEpisodes();
        Notification.show(notification.toString(), Notification.Type.TRAY_NOTIFICATION);
        // todo
    }
}
