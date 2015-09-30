package ga.asev.service;

import ga.asev.dao.SerialDao;
import ga.asev.model.Serial;
import ga.asev.model.SerialInfo;
import ga.asev.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ga.asev.util.StringUtil.encodeUrl;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;

@Service
public class ArtHtmlCrawlerService extends BaseService {


    private static final String ROOT_URL = "http://www.world-art.ru";
    private static final String URL_SEARCH_PREFIX = ROOT_URL + "/search.php?global_sector=animation&public_search=";

    private static final String SERIAL_TYPE_PATTERN = new String("(?i).*Тип.*\\((\\d+)\\s+.*\\s+(\\d+) мин.*".getBytes(), Charset.forName("UTF-8"));

    @Autowired
    private DownloadService downloadService;

    @Autowired
    SerialDao serialDao;

    public SerialInfo findSerialInfo(Serial serial) {
        try {
            SerialInfo serialInfo = findSerialInfo(serial.getName());
            if (serialInfo == null) {
                String shortName = cutSerialName(serial.getName());
                serialInfo = findSerialInfo(shortName);
            }
            if (serialInfo == null) return null;

            serial.setSerialInfo(serialInfo);
            serialDao.insertSerials(singletonList(serial));
            log.info("Added serial info for serial: " + serial.getName());
            return serialInfo;
        } catch (Exception e) {
            log.error("ERROR: Serial info url was not found for serial: " + serial.getName(), e);
            return null;
        }
    }

    private String cutSerialName(String name) {
        String[] s = name.split(" ");
        if (s.length <= 3) return null;
        String cutName = Stream.of(s)
                .filter(str -> str.length() > 2)
                .limit(3)
                .collect(joining(" "));
        if (cutName.equals(name)) return null;
        return cutName;
    }

    private SerialInfo findSerialInfo(String serialName) {
        if (serialName == null) return null;

        String url = URL_SEARCH_PREFIX + encodeUrl(serialName);
        String html = downloadService.download(url);
        if (html == null) return null;

        Document doc = Jsoup.parse(html);
        String serialInfoUrl = parseSerialInfoFromRedirect(doc);
        if (serialInfoUrl == null) {
            serialInfoUrl = parseSerialInfoUrlFromSearch(serialName, doc);
        }
        if (serialInfoUrl == null) {
            log.warn("Serial info url was not found for serial: " + serialName);
            return null;
        }

        String serialInfoHtml = downloadService.download(serialInfoUrl);
        if (serialInfoHtml == null) return null;

        return parseSerialInfo(Jsoup.parse(serialInfoHtml));
    }

    private String parseSerialInfoFromRedirect(Document doc) {
        String cont = doc.select("meta[http-equiv=Refresh]").attr("content");
        String[] split = cont.split("url=");
        if (split.length == 2) {
            return ROOT_URL + split[1];
        }
        return null;
    }

    private String parseSerialInfoUrlFromSearch(String serialName, Document doc) {
        String res = doc.select("a[class=estimation]").stream()
                .filter(e -> e.text().toUpperCase().contains(serialName.toUpperCase()))
                .map(e -> e.attr("href"))
                .findFirst().orElse(null);
        return res == null ? null : ROOT_URL + "/" + res;
    }

    private SerialInfo parseSerialInfo(Document doc) {
        Elements info = doc.select("center > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr");
        String posterUrl = info.select("> td > a > img").attr("src");
        String name = info.select("font[size=3] > b").first().text().replaceFirst("\\[$", "").trim();
        String companyLogoUrl = info.select("> td:nth-child(1) > table > tbody > tr > td:nth-child(1) > table > tbody > tr > td > a > img[border=0]").attr("src");

        String genre = info.select("font[size=2] a[href=http://www.world-art.ru/animation/list.php]").first().text().trim();

        String infoText = info.select("td[valign=top] > font[size=2]").text();
        int size = Integer.valueOf(infoText.replaceAll(SERIAL_TYPE_PATTERN, "$1"));
        int duration = Integer.valueOf(infoText.replaceAll(SERIAL_TYPE_PATTERN, "$2"));

        SerialInfo serialInfo = new SerialInfo();
        serialInfo.setName(name);
        serialInfo.setGenre(genre);
        serialInfo.setSize(size);
        serialInfo.setDuration(duration);
        serialInfo.setPosterUrl(posterUrl);
        serialInfo.setCompanyLogoUrl(companyLogoUrl);
        return serialInfo;
    }

}
