package ga.asev.event;

import ga.asev.ui.view.TrackerViewType;

public abstract class TrackerEvent {

    public static class BrowserResizeEvent {
    }

    public static final class PostViewChangeEvent {
        private final TrackerViewType view;

        public PostViewChangeEvent(final TrackerViewType view) {
            this.view = view;
        }

        public TrackerViewType getView() {
            return view;
        }
    }

    public static class CloseOpenWindowsEvent {
    }
}
