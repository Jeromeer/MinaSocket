package app.socketlib.com.library.utils;

import java.util.ArrayList;

/**
 * author：JianFeng
 * date：2017/8/22 16:45
 * description：socket命令的缓存管理
 */
public class SocketCommandCacheUtils {
    private volatile static ArrayList<String> cache = null;
    private volatile static SocketCommandCacheUtils mInstance = null;

    public static SocketCommandCacheUtils getInstance() {
        if (mInstance == null) {
            synchronized (SocketCommandCacheUtils.class) {
                if (mInstance == null) {
                    mInstance = new SocketCommandCacheUtils();
                }
            }
        }
        return mInstance;
    }

    public ArrayList<String> initCache() {
        if (cache == null) {
            synchronized (SocketCommandCacheUtils.class) {
                if (cache == null) {
                    cache = new ArrayList<>();
                }
            }
        }
        return cache;
    }


    public void addCache(String type) {
        if (!cache.contains(type)) {
            cache.add(type);
        }
    }


    public ArrayList<String> getTradeCache() {
        return cache;
    }

    /***
     * 取消单个订阅
     * @param msg
     */
    public void removeSingleCache(String msg) {
        if (null != cache && cache.contains(msg)) {
            cache.remove(msg);
        }
    }

    /***
     * 取消所有订阅
     */
    public void removeAllCache() {
        if (null != cache) {
            cache.clear();
            cache = null;
        }
    }

}
