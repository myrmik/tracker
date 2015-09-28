package ga.asev.dao;

import ga.asev.model.Serial;

import java.util.List;

public interface SerialDao {
    void insertSerials(List<Serial> serials);
    List<Serial> selectAllSerials();
    List<Serial> selectNewSerials();
}
