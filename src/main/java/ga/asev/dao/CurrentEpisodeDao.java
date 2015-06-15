package ga.asev.dao;

import ga.asev.model.CurrentEpisode;

import java.util.List;

public interface CurrentEpisodeDao {
    CurrentEpisode insertTorrent(CurrentEpisode episode);
    CurrentEpisode selectTorrentByName(String name);
    List<CurrentEpisode> selectAllTorrents();
    void deleteTorrent(int id);
}
