package org.pqh.dao;

import org.pqh.entity.Bdu;
import org.pqh.entity.Tsdm;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by reborn on 2017/4/3.
 */
@Component
public interface BduDao {
    void insertTsdm(Tsdm tsdm);

    List<Tsdm> selectTsdm(String animeName);

    List<Tsdm> selectUpdate(String updateTime);

    List<Tsdm> selectAllTsdm();

    void updateTsdm(Tsdm tsdm);

    void insertBdu(Bdu bdu);
}
