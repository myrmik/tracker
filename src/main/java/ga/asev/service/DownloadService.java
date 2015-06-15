package ga.asev.service;

public interface DownloadService {
    String download(String url);
    boolean downloadToFile(String url, String filePath);
}
