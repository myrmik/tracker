package ga.asev.ui.view.serial;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import ga.asev.model.SerialInfo;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
public class SerialInfoComponent extends Panel {

    private Label titleLabel = new Label();
    private Label genreLabel = new Label();
    private Label typeLabel = new Label();
    private Image posterImg = new Image();
    private Image companyLogoImg = new Image();

    private Component content;

    @PostConstruct
    void init() {
        setSizeFull();
        addStyleName("si-view");

        HorizontalLayout topLt = new HorizontalLayout();

        topLt.addComponent(posterImg);
        topLt.addComponent(buildInfoLayout());
        topLt.setSpacing(true);

        VerticalLayout mainLt = new VerticalLayout(
                topLt,
                companyLogoImg
        );
        mainLt.setSizeFull();
        mainLt.setExpandRatio(topLt, 1);

        content = mainLt;
        setImmediate(true);
        refresh(null);
    }

    private VerticalLayout buildInfoLayout() {
        titleLabel.addStyleName("si-title");

        return new VerticalLayout(
                titleLabel,
                new HorizontalLayout(
                        new SiLabel("Genre:"),
                        genreLabel
                ),
                new HorizontalLayout(
                        new SiLabel("Type:"),
                        typeLabel
                )
        );
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
    }

    private class SiLabel extends Label {
        public SiLabel(String content) {
            super(content, ContentMode.TEXT);
            addStyleName("si-label-capture");
        }
    }

}
