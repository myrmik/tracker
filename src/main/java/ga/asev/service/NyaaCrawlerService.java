package ga.asev.service;

import ga.asev.model.CurrentEpisode;

public interface NyaaCrawlerService {
    boolean downloadTorrent(CurrentEpisode episode, String filePath);
    void updateSerialList();
}
