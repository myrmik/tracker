package ga.asev.service;

import ga.asev.model.CurrentEpisode;

public interface NyaaCrawlerService {
    int downloadTorrents(CurrentEpisode episode);
    void updateSerialList();
}
