package ga.asev.schedule;

import ga.asev.dao.CurrentEpisodeDao;
import ga.asev.model.CurrentEpisode;
import ga.asev.service.NyaaCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static ga.asev.util.ThreadUtil.sleepForDownload;

@Component
public class TorrentSchedule extends BaseSchedule {

    private final static long UPDATE_SERIALS_DELAY = 4 * 60 * 60 * 1000;    // 4h
    private final static long DOWNLOAD_DELAY = 30 * 60 * 1000;              // 30m

    @Autowired
    NyaaCrawlerService nyaaCrawlerService;

    @Autowired
    CurrentEpisodeDao currentEpisodeDao;

    @Scheduled(fixedDelay=UPDATE_SERIALS_DELAY)
    public void updateSerials() {
        nyaaCrawlerService.updateSerialList();
    }

    @Scheduled(fixedDelay=DOWNLOAD_DELAY)
    public void searchTorrents() {
        List<CurrentEpisode> currentEpisodes = currentEpisodeDao.selectAllTorrents();
        if (currentEpisodes == null) {
            log.info("There is no torrents to download");
            return;
        }

        log.info("Started search for torrents, size: " + currentEpisodes.size());
        int downloadedNbr = 0;
        for (CurrentEpisode episode : currentEpisodes) {
            downloadedNbr += downloadTorrent(episode);
            sleepForDownload();
            log.info("Episodes were downloaded: " + downloadedNbr + ", last: " + episode);
        }
        log.info("Finished search for torrents, size: " + currentEpisodes.size() + ", downloaded: " + downloadedNbr);
    }

    private int downloadTorrent(CurrentEpisode episode) {
        int downloaded = nyaaCrawlerService.downloadTorrents(episode);
        if (downloaded > 0) {
            episode.setEpisode(episode.getEpisode() + downloaded); // increase # of episode
            episode.setLastUpdated(LocalDateTime.now());
            currentEpisodeDao.insertTorrent(episode);
            return downloaded;
        }
        log.info("There is no next episode for: " + episode);
        return 0;
    }
}
