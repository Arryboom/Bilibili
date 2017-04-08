package org.pqh.qq;

import com.scienjus.smartqq.callback.MessageCallback;
import org.pqh.entity.Bili;

/**
 * Created by reborn on 2017/3/7.
 */

public interface QueryRule extends MessageCallback {
        boolean check(Bili bili);
}
