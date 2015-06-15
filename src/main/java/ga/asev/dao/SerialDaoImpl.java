package ga.asev.dao;

import ga.asev.model.Serial;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class SerialDaoImpl extends BaseDao<Integer, Serial> implements SerialDao {
    @Override
    public void insertSerials(List<Serial> serials) {
        List<Serial> storedSerials = selectAllSerials();
        insertAll(serials.stream()
                .filter(s -> !storedSerials.contains(s))
                .collect(toList())
        );
    }

    @Override
    public List<Serial> selectAllSerials() {
        return selectAll();
    }
}
