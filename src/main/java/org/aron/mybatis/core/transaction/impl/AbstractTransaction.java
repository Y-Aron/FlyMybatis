package org.aron.mybatis.core.transaction.impl;

import lombok.Data;
import org.aron.mybatis.core.transaction.Transaction;

/**
 * @author: Y-Aron
 * @create: 2019-03-26 23:28
 */
@Data
public abstract class AbstractTransaction implements Transaction {

    protected boolean autoCommit;
}
