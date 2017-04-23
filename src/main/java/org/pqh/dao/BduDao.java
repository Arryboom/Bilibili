package org.pqh.dao;

import org.pqh.entity.Bdu;
import org.pqh.entity.Tsdm;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by reborn on 2017/4/3.
 */
@Repository
public interface BduDao {
    void insertTsdm(Tsdm tsdm);

    List<Tsdm> selectTsdm(Tsdm tsdm);

    void updateTsdm(Tsdm tsdm);

    void insertBdu(Bdu bdu);

    void updateBdu(Bdu bdu);

    List<Bdu> selectBdu(Bdu bdu);
}
