package ga.asev.ui.view.serial;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import ga.asev.model.SerialComment;
import ga.asev.model.SerialInfo;
import ga.asev.util.StringUtil;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ga.asev.util.StringUtil.isEmpty;

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
    private Label summaryCaption = new BoldLabel("Summary");
    private Label commentSeparator = new HorizontalSeparator();
    private Label commentCaption = new BoldLabel("Comments");

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
                summaryCaption,
                summaryLabel,
                commentSeparator,
                commentCaption,
                commentsLt
        );
        summaryLt.setSpacing(true);
        summaryLt.setMargin(true);
        commentsLt.setSpacing(true);
        commentsLt.addStyleName("si-label-text");
        summaryLabel.addStyleName("si-label-text");
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
        boolean summaryVisible = (!isEmpty(serialInfo.getSummary()));
        summaryCaption.setVisible(summaryVisible);
        summaryLabel.setVisible(summaryVisible);
        commentSeparator.setVisible(summaryVisible);

        refreshComments(serialInfo.getComments());
    }

    private void refreshComments(List<SerialComment> comments) {
        boolean commentVisible = !CollectionUtils.isEmpty(comments);
        commentCaption.setVisible(commentVisible);
        commentsLt.setVisible(commentVisible);
        if (!commentVisible) {
            return;
        }
        comments.forEach(comment -> {
            commentsLt.addComponent(createCommentHeader(comment));
            commentsLt.addComponent(new Label(comment.getContent()));
            commentsLt.addComponent(new HorizontalSeparator());
        });
    }

    private Component createCommentHeader(SerialComment comment) {
        HorizontalLayout commentHeader = new HorizontalLayout();
        commentHeader.addComponent(new BoldLabel(comment.getAuthor()));

        Label dateLabel = new Label(commentDateToString(comment));
        EmptyLabel emptyLabel = new EmptyLabel();
        HorizontalLayout dateLt = new HorizontalLayout(emptyLabel, dateLabel);
        dateLt.setExpandRatio(emptyLabel, 0.8f);
        dateLt.setExpandRatio(dateLabel, 0.2f);
        dateLt.setWidth(100, Unit.PERCENTAGE);
        commentHeader.addComponent(dateLt);

        commentHeader.setWidth(100, Unit.PERCENTAGE);
        return commentHeader;
    }

    private String commentDateToString(SerialComment comment) {
        if (comment.getPublishDate() == null) return null;
        return comment.getPublishDate().format(commentDateFormatter);
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
            super("<b>" + content + "</b>", ContentMode.HTML);
        }
    }

    private class HorizontalSeparator extends Label {
        public HorizontalSeparator() {
            super("<hr />", ContentMode.HTML);
        }
    }

}
