package ga.asev.service;

import ga.asev.model.UserSerial;

public interface NyaaCrawlerService {
    int downloadTorrents(UserSerial userSerial);
    void updateSerialList();
}
