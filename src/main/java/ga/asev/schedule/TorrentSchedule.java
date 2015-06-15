package ga.asev.schedule;

import ga.asev.dao.CurrentEpisodeDao;
import ga.asev.env.TorrentProperties;
import ga.asev.model.CurrentEpisode;
import ga.asev.service.NyaaCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static java.util.concurrent.TimeUnit.*;

@Component
public class TorrentSchedule extends BaseSchedule {

    private final static long UPDATE_SERIALS_DELAY = 4 * 60 * 60 * 1000;    // 4h
    private final static long DOWNLOAD_DELAY = 30 * 60 * 1000;              // 30m
    private final static long DOWNLOAD_ONE_DELAY = 20000;                   // 20s

    @Autowired
    TorrentProperties torrentProperties;

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
            while (downloadTorrent(episode)) {
                ++downloadedNbr;
                sleepForTorrent();
            }
            sleepForTorrent();
        }
        log.info("Finished search for torrents, size: " + currentEpisodes.size() + ", downloaded: " + downloadedNbr);
    }

    private boolean downloadTorrent(CurrentEpisode episode) {
        episode.setEpisode(episode.getEpisode() + 1); // increase # of episode
        String filePath = torrentProperties.getSavePath() + episode + ".torrent";
        boolean downloaded = nyaaCrawlerService.downloadTorrent(episode, filePath);
        if (downloaded) {
            log.info("Episode was downloaded: " + episode);
            episode.setDate(new Date());
            currentEpisodeDao.insertTorrent(episode);
            return true;
        }
        log.info("There is no such episode yet: " + episode);
        return false;
    }

    private void sleepForTorrent() {
        try {
            Thread.sleep(DOWNLOAD_ONE_DELAY);
        } catch (InterruptedException e) {
            log.warn("Download sleep was interrupted", e);
        }
    }
}
