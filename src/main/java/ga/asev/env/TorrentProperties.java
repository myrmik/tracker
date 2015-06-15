package ga.asev.env;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TorrentProperties {

    @Value("${torrent.savePath}")
    String savePath;

    public String getSavePath() {
        return savePath;
    }
}
