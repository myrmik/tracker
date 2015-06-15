package ga.asev.dao;

import ga.asev.model.CurrentEpisode;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CurrentEpisodeDaoImpl extends BaseDao<Integer, CurrentEpisode> implements CurrentEpisodeDao {
    @Override
    public CurrentEpisode insertTorrent(CurrentEpisode episode) {
        if (episode == null)
            throw new IllegalArgumentException("Book is null");
        if (episode.getId() != null || torrentNotExists(episode)) {
            return insert(episode);
        }
        return null;
    }

    private boolean torrentNotExists(CurrentEpisode episode) {
        return selectTorrentByName(episode.getName()) == null;
    }

    @Override
    public CurrentEpisode selectTorrentByName(String name) {
        return selectByCriteria("name", name);
    }

    @Override
    public List<CurrentEpisode> selectAllTorrents() {
        return selectAll();
    }

    @Override
    public void deleteTorrent(int id) {
        delete(id);
    }
}
