package ga.asev.ui.util;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class UiUtil {
    public static Label emptyLabel() {
        return new Label("&nbsp;", ContentMode.HTML);
    }
}
