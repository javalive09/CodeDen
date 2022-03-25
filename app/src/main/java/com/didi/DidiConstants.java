package com.didi;

public class DidiConstants {

    public static final class WindowInfoService extends Base {
        public static final int ID = 1;
        public static final String KEY_WINDOW_TOKEN = "window_token";
        public static final int ACTION_SHOW = 1;
        public static final int ACTION_HIDE = 2;
        public static final int ACTION_REGISTER = 3;
        public static final int ACTION_UNREGISTER = 4;
        public static final int ACTION_WATCHER = 5;
    }

    public static final class LocalService extends Base {
        private static final int ID = 2;
        public static final int ACTION_REGISTER_WINDOW_INFO_WATCHER = 3;
        public static final int ACTION_UNREGISTER_WINDOW_INFO_WATCHER = 4;
    }

    public static class Base {
        public static final String KEY_SERVICE = "global_service";
        public static final String KEY_SERVICE_ID = "didi_service_id";
        public static final String KEY_ACTION = "action";
        public static final String KEY_CALLBACK = "callback";
        public static final String KEY_WATCHER = "watcher";
    }

}
