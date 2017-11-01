package app.socketlib.com.library.utils;

import org.greenrobot.eventbus.EventBus;

/**
 * author：JianFeng
 * date：2017/7/20 14:26
 * description：eventbus封装
 */
public class Bus {

    public static void register(Object object) {
        if (null != object) {
            EventBus.getDefault().register(object);
        }
    }

    public static void unregister(Object object) {
        if (null != object) {
            EventBus.getDefault().unregister(object);
        }
    }


    public static void post(Object object) {
        if (null != object) {
            EventBus.getDefault().post(object);
        }
    }

    public static void postSticky(Object object) {
        if (null != object) {
            EventBus.getDefault().postSticky(object);
        }
    }


}
