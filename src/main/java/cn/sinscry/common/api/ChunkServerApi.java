package cn.sinscry.common.api;

import java.rmi.Remote;
import java.util.Map;

public interface ChunkServerApi extends Remote {
    Map<Long, String> getMap() throws Exception;
}
