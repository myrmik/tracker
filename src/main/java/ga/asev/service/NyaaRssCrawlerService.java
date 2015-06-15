package ga.asev.service;

import ga.asev.dao.SerialDao;
import ga.asev.model.CurrentEpisode;
import ga.asev.model.Serial;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static ga.asev.util.StringUtil.encodeUrl;
import static ga.asev.util.StringUtil.isEmpty;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Service
public class NyaaRssCrawlerService extends BaseService implements NyaaCrawlerService {
    private static final String TORRENT_OWNER = "HorribleSubs";
    private static final String TORRENT_QUALITY = "720p";

    public static final String RSS_URL_SEARCH_PREFIX = "http://www.nyaa.se/?page=rss&term=";

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
    public boolean downloadTorrent(CurrentEpisode episode, String filePath) {
        String query = TORRENT_OWNER + " " + episode.getName() + " " + episode.getEpisodeString() + " " + TORRENT_QUALITY;
        String url = RSS_URL_SEARCH_PREFIX + encodeUrl(query);
        String rss = downloadService.download(url);
        if (rss == null)
            return false;

        String episodeUrl = parseEpisodeUrl(episode, rss);

        if (!isEmpty(episodeUrl)) {
            downloadService.downloadToFile(episodeUrl, filePath);
            return true;
        }

        return false;
    }

    private List<Serial> parseSerials(String rss) {
        Elements items = getRssItems(rss);

        Collection<Optional<Serial>> values = items.stream()
                .map(e -> new Serial(parseItemTitle(getItemTitle(e)), getItemDate(e)))
                .collect(groupingBy(Serial::getName, maxBySerialLastUpdated()))
                .values();

        return values.stream().map(e -> e.orElse(null)).collect(toList());
    }

    private Collector<Serial, ?, Optional<Serial>> maxBySerialLastUpdated() {
        return Collectors.maxBy((o1, o2) -> o1.getLastUpdated().compareTo(o2.getLastUpdated()));
    }

    private String parseEpisodeUrl(CurrentEpisode episode, String rss) {
        Elements items = getRssItems(rss);

        return items.stream()
                .filter(elementsBy(episode))
                .map(Element::ownText) // link
                .findFirst().orElse(null);
    }

    private Elements getRssItems(String rss) {
        Document doc = Jsoup.parse(rss);
        return doc.select("rss > channel > item");
    }

    private Predicate<Element> elementsBy(CurrentEpisode episode) {
        return e -> {
            String name = getItemTitle(e);
            return isMatch(episode, name);
        };
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
        return LocalDateTime.parse(text, formatter);
    }

    private String getItemProp(Element e, String name) {
        return e.select(name).first().text();
    }

    private boolean isMatch(CurrentEpisode episode, String name) {
        return !(episode == null || isEmpty(name))
                && name.contains(TORRENT_OWNER)
                && name.contains(episode.getName())
                && name.contains(" " + episode.getEpisodeString() + " ");
    }
}
