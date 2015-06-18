package ga.asev.dao;

import ga.asev.model.Serial;
import ga.asev.model.UserSerial;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserSerialDaoImpl extends BaseDao<Integer, UserSerial> implements UserSerialDao {
    @Override
    public UserSerial insertUserSerial(UserSerial userSerial) {
        if (userSerial == null)
            throw new IllegalArgumentException("Book is null");
        if (userSerial.getId() != null || useSerialNotExists(userSerial)) {
            return insert(userSerial);
        }
        return null;
    }

    private boolean useSerialNotExists(UserSerial userSerial) {
        return selectUserSerialBySerial(userSerial.getSerial()) == null;
    }

    private UserSerial selectUserSerialBySerial(Serial serial) {
        return selectByCriteria("serial", serial);
    }

    @Override
    public List<UserSerial> selectAllUserSerials() {
        return selectAll();
    }

    @Override
    public void deleteUserSerial(int id) {
        delete(id);
    }
}
