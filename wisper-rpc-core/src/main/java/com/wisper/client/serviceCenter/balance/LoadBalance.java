package com.wisper.client.serviceCenter.balance;

import com.wisper.client.serviceCenter.balance.impl.ConsistencyHashBalance;
import com.wisper.client.serviceCenter.balance.impl.RandomLoadBalance;
import com.wisper.client.serviceCenter.balance.impl.RoundLoadBalance;
import com.wisper.common.config.ProtocolConfig;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public interface LoadBalance {
    String balance(List<String> addressList);
    void addNode(String node);
    void delNode(String node);

    static LoadBalance getInstance(){
        LoadBalanceStrategy loadBalance = LoadBalanceStrategy.getLoadBalance(ProtocolConfig.loadBalance);
        switch (loadBalance){
            case ROUND:
                return new RoundLoadBalance();
            case CONSISTENCY_HASH:
                return new ConsistencyHashBalance();
            default:
                return new RandomLoadBalance();
        }
    }

    enum LoadBalanceStrategy {
        RANDOM,
        ROUND,
        CONSISTENCY_HASH;

        public static LoadBalanceStrategy getLoadBalance(String loadBalance){
            if (StringUtils.isBlank(loadBalance)){
                return RANDOM;
            }
            for (LoadBalanceStrategy value : LoadBalanceStrategy.values()) {
                if (value.name().equals(loadBalance)) {
                    return value;
                }
            }
            return RANDOM;
        }
    }
}
