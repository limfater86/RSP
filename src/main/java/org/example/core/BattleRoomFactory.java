package org.example.core;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author dperminov
 * @since 14.04.2024
 */
public class BattleRoomFactory extends BasePooledObjectFactory<BattleRoom> {
    @Override
    public BattleRoom create() throws Exception {
        return new BattleRoom();
    }

    @Override
    public PooledObject<BattleRoom> wrap(BattleRoom room) {
        return new DefaultPooledObject<>(room);
    }

    @Override
    public void passivateObject(PooledObject<BattleRoom> p) throws Exception {
        p.getObject().reset();
    }
}
