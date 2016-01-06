package ga.asev.ui.view.serial;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import ga.asev.model.SerialComment;
import ga.asev.model.SerialInfo;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.List;

@UIScope
@SpringComponent
public class SerialInfoComponent extends Panel {

    private Label titleLabel = new Label();
    private Label genreLabel = new Label();
    private Label typeLabel = new Label();
    private Image posterImg = new Image();
    private Image companyLogoImg = new Image();
    private Label summaryLabel = new Label();
    private VerticalLayout commentsLt = new VerticalLayout();

    private Component content;

    private static final DateTimeFormatter commentDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @PostConstruct
    void init() {
        setSizeFull();
        addStyleName("si-view");

        HorizontalLayout topLt = new HorizontalLayout();

        topLt.addComponent(posterImg);
        topLt.addComponent(buildInfoLayout());
        topLt.setSpacing(true);

        Panel summaryPanel = getSummaryPanel();

        VerticalLayout mainLt = new VerticalLayout(
                topLt,
                summaryPanel
        );
        mainLt.setSizeFull();
        mainLt.setExpandRatio(summaryPanel, 1);

        content = mainLt;
        setImmediate(true);
        refresh(null);
    }

    private Panel getSummaryPanel() {
        VerticalLayout summaryLt = new VerticalLayout(
                new BoldLabel("Summary"),
                summaryLabel,
                new BoldLabel("Comments"),
                commentsLt
        );
        summaryLt.setSpacing(true);
        summaryLt.setMargin(true);
        Panel summaryPanel = new Panel(summaryLt);
        summaryPanel.setHeight(25, Unit.EM);
        summaryPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        return summaryPanel;
    }

    private VerticalLayout buildInfoLayout() {
        titleLabel.addStyleName("si-title");

        VerticalLayout logoLt = new VerticalLayout();
        Label logoSp = new EmptyLabel();
        logoLt.addComponent(logoSp);
        logoLt.addComponent(companyLogoImg);
        logoLt.setExpandRatio(logoSp, 1);
        logoLt.setDefaultComponentAlignment(Alignment.BOTTOM_RIGHT);

        VerticalLayout layout = new VerticalLayout(
                titleLabel,
                new HorizontalLayout(
                        new SiLabel("Genre:"),
                        genreLabel
                ),
                new HorizontalLayout(
                        new SiLabel("Type:"),
                        typeLabel
                ),
                logoLt
        );
        layout.setExpandRatio(logoLt, 1);
        logoLt.setSizeFull();
        layout.setSizeFull();
        return layout;
    }

    public void refresh(SerialInfo serialInfo) {
        if (serialInfo == null) {
            setContent(null);
            return;
        }
        setContent(content);
        titleLabel.setValue(serialInfo.getName());
        genreLabel.setValue(serialInfo.getGenre());
        typeLabel.setValue(serialInfo.getSize() + " ep, " + serialInfo.getDuration() + " min");
        posterImg.setSource(new ExternalResource(serialInfo.getPosterUrl()));
        companyLogoImg.setSource(new ExternalResource(serialInfo.getCompanyLogoUrl()));
        summaryLabel.setValue(serialInfo.getSummary());

        refreshComments(serialInfo.getComments());
    }

    private void refreshComments(List<SerialComment> comments) {
        if (comments == null) return;
        comments.forEach(comment -> {
            HorizontalLayout commentHeader = new HorizontalLayout();
            commentHeader.addComponent(new BoldLabel(comment.getAuthor()));
            EmptyLabel emptyLabel = new EmptyLabel();
            commentHeader.addComponent(emptyLabel);
            Label dateLabel = new Label(comment.getPublishDate().format(commentDateFormatter));
            commentHeader.addComponent(dateLabel);
            commentHeader.setExpandRatio(emptyLabel, 1);
            commentsLt.addComponent(commentHeader);
            commentsLt.addComponent(new Label(comment.getContent()));
        });
    }

    private class SiLabel extends Label {
        public SiLabel(String content) {
            super(content, ContentMode.TEXT);
            addStyleName("si-label-capture");
        }
    }

    private class EmptyLabel extends Label {
        public EmptyLabel() {
            super("&nbsp;", ContentMode.HTML);
        }
    }

    private class BoldLabel extends Label {
        public BoldLabel(String content) {
            super(content);
            addStyleName(ValoTheme.LABEL_BOLD);
        }
    }

}
