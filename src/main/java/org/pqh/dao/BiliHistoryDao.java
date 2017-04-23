package org.pqh.dao;

import org.pqh.entity.history.Data;
import org.springframework.stereotype.Repository;

/**
 * Created by reborn on 2017/4/14.
 */
@Repository
public interface BiliHistoryDao {
        void insertHistory(Data data);

        void updateHistory(Data data);
}
