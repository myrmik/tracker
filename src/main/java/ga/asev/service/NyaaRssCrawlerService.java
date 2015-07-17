package ga.asev.service;

import ga.asev.dao.SerialDao;
import ga.asev.env.TorrentProperties;
import ga.asev.model.UserSerial;
import ga.asev.model.Serial;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static ga.asev.util.DateUtil.zoneToLocal;
import static ga.asev.util.StringUtil.encodeUrl;
import static ga.asev.util.ThreadUtil.sleepForDownload;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;


@Service
public class NyaaRssCrawlerService extends BaseService implements NyaaCrawlerService {
    private static final String TORRENT_OWNER = "HorribleSubs";
    private static final String TORRENT_QUALITY = "720p";

    private static final String EP_PATTERN = String.format("(?i).*\\[%s\\]\\s+(.*)\\s+-\\s+(\\d+)(.*)", TORRENT_OWNER);


    public static final String RSS_URL_SEARCH_PREFIX = "http://www.nyaa.se/?page=rss&term=";

    @Autowired
    TorrentProperties torrentProperties;

    @Autowired
    DownloadService downloadService;

    @Autowired
    SerialDao serialDao;

    @Override
    public void updateSerialList() {
        String query = "[HorribleSubs]+720p";
        String url = RSS_URL_SEARCH_PREFIX + encodeUrl(query);
        String rss = downloadService.download(url);
        if (rss == null) return;

        List<Serial> serials = parseSerials(rss);
        serialDao.insertSerials(serials);
    }

    @Override
    public int downloadTorrents(UserSerial userSerial) {
        String query = TORRENT_OWNER + " " + userSerial.getOriginalName() + " " + TORRENT_QUALITY;
        String url = RSS_URL_SEARCH_PREFIX + encodeUrl(query);
        String rss = downloadService.download(url);
        if (rss == null) return 0;

        List<Episode> episodes = parseEpisodes(userSerial, rss);

        if (!isEmpty(episodes)) {
            downloadTorrents(episodes);
            userSerial.setPublishDate(Episode.getMaxPubDate(episodes));
            return episodes.size();
        }

        return 0;
    }

    private void downloadTorrents(List<Episode> episodes) {
        episodes.forEach(e -> {
            String filePath = torrentProperties.getSavePath() + e.getEpisodeDesc() + ".torrent";
            downloadService.downloadToFile(e.url, filePath);
            log.info("Download episode: " + e.getEpisodeDesc());
            sleepForDownload();
        });
    }

    private List<Serial> parseSerials(String rss) {
        Elements items = getRssItems(rss);

        Collection<Optional<Serial>> values = items.stream()
                .map(e -> new Serial(parseItemTitle(getItemTitle(e)), getItemDate(e)))
                .collect(groupingBy(Serial::getName, maxBySerialPublishDate()))
                .values();

        return values.stream().map(e -> e.orElse(null)).collect(toList());
    }

    private Collector<Serial, ?, Optional<Serial>> maxBySerialPublishDate() {
        return Collectors.maxBy((o1, o2) -> o1.getPublishDate().compareTo(o2.getPublishDate()));
    }

    private List<Episode> parseEpisodes(UserSerial userSerial, String rss) {
        Elements items = getRssItems(rss);

        return items.stream()
                .map(this::toEpisode)
                .filter(e -> matchEpisode(userSerial, e))
                .sorted((o1, o2) -> o1.episode.compareTo(o2.episode))
                .collect(toList());
    }

    private boolean matchEpisode(UserSerial userSerial, Episode e) {
        return e.name.equals(userSerial.getOriginalName()) && e.episode > userSerial.getEpisode();
    }

    private Episode toEpisode(Element item) {
        Episode episode = new Episode();
        String title = getItemTitle(item);
        episode.name = parseName(title);
        episode.episode = parseEpisode(title);
        episode.pubDate = getItemDate(item);
        episode.url = item.ownText();
        return episode;
    }

    private Elements getRssItems(String rss) {
        Document doc = Jsoup.parse(rss);
        return doc.select("rss > channel > item");
    }

    private String parseItemTitle(String title) {
        String pattern = "(?i)(.*\\[HorribleSubs\\]\\s+)(.+?)(\\s+-\\s+\\d+.*)";
        return title.replaceAll(pattern, "$2")
                .replaceAll("\\[" + TORRENT_OWNER + "\\]", "")
                .replaceAll("\\[" + TORRENT_QUALITY + "\\]", "");
    }


    private String getItemTitle(Element e) {
        return getItemProp(e, "title");
    }

    private LocalDateTime getItemDate(Element e) {
        String text = getItemProp(e, "pubDate");
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(text, formatter);
        return zoneToLocal(zonedDateTime);
    }

    private String getItemProp(Element e, String name) {
        return e.select(name).first().text();
    }

    private String parseName(String title) {
        return title.replaceAll(EP_PATTERN, "$1");
    }

    private int parseEpisode(String title) {
        try {
            return Integer.parseInt(title.replaceAll(EP_PATTERN, "$2"));
        } catch (NumberFormatException ignored) {}
        return 0;
    }

    private static class Episode {
        String name;
        Integer episode;
        String url;
        LocalDateTime pubDate;

        String getEpisodeDesc() {
            return name + " - " + episode;
        }

        static LocalDateTime getMaxPubDate(List<Episode> episodes) {
            return episodes.stream().max((o1, o2) -> o1.episode.compareTo(o2.episode)).orElse(null).pubDate;
        }
    }
}
