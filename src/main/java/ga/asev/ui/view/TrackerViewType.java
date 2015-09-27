package ga.asev.ui.view;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import ga.asev.ui.view.favorite.FavoriteListView;
import ga.asev.ui.view.serial.SerialListView;

public enum TrackerViewType {
    FAVORITES(FavoriteListView.NAME, FontAwesome.STAR),
    SERIALS(SerialListView.NAME, FontAwesome.FILM)
    ;

    private final String viewName;
    private final Resource icon;

    TrackerViewType(final String viewName, final Resource icon) {
        this.viewName = viewName;
        this.icon = icon;
    }

    public String getViewName() {
        return viewName;
    }

    public Resource getIcon() {
        return icon;
    }

    public static TrackerViewType getByViewName(final String viewName) {
        TrackerViewType result = null;
        for (TrackerViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

}
