package ga.asev.dao;

import ga.asev.model.UserSerialNotification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserSerialNotificationDaoImpl extends BaseDao<Integer, UserSerialNotification> implements UserSerialNotificationDao {
    @Override
    public UserSerialNotification insertNotification(UserSerialNotification notification) {
        return insert(notification);
    }

    @Override
    public List<UserSerialNotification> selectAllNotifications() {
        return selectAll();
    }

    @Override
    public void deleteNotification(int id) {
        delete(id);
    }

    public int selectUnreadCount() {
        Object result = getCurrentSession()
                .createCriteria(UserSerialNotification.class)
                .add(Restrictions.eq("read", false))
                .setProjection(Projections.rowCount())
                .uniqueResult();
        return ((Long) result).intValue();
    }

}
