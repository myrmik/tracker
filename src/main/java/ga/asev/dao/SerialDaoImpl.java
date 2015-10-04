package ga.asev.dao;

import ga.asev.model.Serial;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class SerialDaoImpl extends BaseDao<Integer, Serial> implements SerialDao {
    @Override
    public void insertSerials(List<Serial> serials) {
        insertAll(serials.stream()
                .map(this::mergeSerial)
                .collect(toList())
        );
    }

    private Serial mergeSerial(Serial serial) {
        Serial storedSerial = selectByCriteria("name", serial.getName());
        if (storedSerial != null) {
            storedSerial.setPublishDate(serial.getPublishDate());
            storedSerial.setPublishEpisode(serial.getPublishEpisode());
            if (serial.getSerialInfo() != null) {
                storedSerial.setSerialInfo(serial.getSerialInfo());
                storedSerial.getSerialInfo().setSerial(storedSerial);
            }
            return storedSerial;
        }
        return serial;
    }

    @Override
    public List<Serial> selectAllSerials() {
        return selectAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Serial> selectNewSerials() {
        return getCurrentSession()
                .createCriteria(Serial.class)
                .addOrder(Order.desc("publishDate"))
                .list();
    }
}
