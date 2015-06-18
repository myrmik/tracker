package ga.asev.dao;

import ga.asev.model.UserSerial;

import java.util.List;

public interface UserSerialDao {
    UserSerial insertUserSerial(UserSerial UserSerial);
    List<UserSerial> selectAllUserSerials();
    void deleteUserSerial(int id);
}
