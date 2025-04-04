package com.openfeint.internal.analytics;

import android.content.Context;
import com.openfeint.internal.JsonCoder;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.analytics.internal.AnalyticsRequest;
import com.openfeint.internal.analytics.p002db.AnalyticsDBManager;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.request.OrderedArgList;
import com.tapjoy.TapjoyConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class AnalyticsManager {
    private static final int BATCH_UPLOAD_NUM_MAX = 100;
    private static final int DEFALUT_BATCH_UPLOAD_TRIGGER = 10;
    private static int batch_num_trigger;
    private static AnalyticsManager instance;
    private String client_application_id;
    private String country;
    private String hardware;
    private Map<String, Object> info;
    private String locale;
    private Context mContext;
    private String of_version;
    private String os_version;
    private String platform;
    private String version;
    private boolean waiting;
    private String tag = "AnalyticsManager";
    private int count = 0;

    private AnalyticsManager(Context context) {
        this.mContext = context;
        if (batch_num_trigger == 0) {
            batch_num_trigger = 10;
        }
        this.waiting = false;
        initInfo();
    }

    private void initInfo() {
        this.info = new HashMap();
        this.hardware = OpenFeintInternal.getModelString();
        this.client_application_id = OpenFeintInternal.getInstance().getAppID();
        this.version = String.valueOf(OpenFeintInternal.getInstance().getAppVersion());
        this.of_version = OpenFeintInternal.getInstance().getOFVersion();
        this.os_version = OpenFeintInternal.getOSVersionString();
        this.platform = "Android";
        this.locale = OpenFeintInternal.getInstance().getLocaleString();
        this.country = OpenFeintInternal.getInstance().getCountryString();
        OFLog.m181d(this.tag, "hardware:" + this.hardware);
        OFLog.m181d(this.tag, "client_application_id:" + this.client_application_id);
        OFLog.m181d(this.tag, "version:" + this.version);
        OFLog.m181d(this.tag, "of_version:" + this.of_version);
        OFLog.m181d(this.tag, "os_version:" + this.os_version);
        OFLog.m181d(this.tag, "platform:" + this.platform);
        OFLog.m181d(this.tag, "locale:" + this.locale);
        OFLog.m181d(this.tag, "country:" + this.country);
        this.info.put("hardware", this.hardware);
        this.info.put("client_application_id", this.client_application_id);
        this.info.put("version", this.version);
        this.info.put("of_version", this.of_version);
        this.info.put(TapjoyConstants.TJC_DEVICE_OS_VERSION_NAME, this.os_version);
        this.info.put(TapjoyConstants.TJC_PLATFORM, this.platform);
        this.info.put("locale", this.locale);
        this.info.put("country", this.country);
    }

    public static AnalyticsManager instance(Context context) {
        if (instance == null) {
            instance = new AnalyticsManager(context);
        }
        return instance;
    }

    public static AnalyticsManager instance() {
        return instance;
    }

    public void makelog(IAnalyticsLogger event, String loggerTag) {
        try {
            OFLog.m181d(loggerTag, "Log:\n" + log_(event));
        } catch (Exception e) {
            OFLog.m182e(loggerTag, "log save failed" + e.getLocalizedMessage());
        }
    }

    public void makelog(IAnalyticsLogger event, String loggerTag, int level) {
        try {
            if (level == 2) {
                OFLog.m184v(loggerTag, "Log:\n" + log_(event));
            } else if (level == 3) {
                OFLog.m181d(loggerTag, "Log:\n" + log_(event));
            } else if (level == 4) {
                OFLog.m183i(loggerTag, "Log:\n" + log_(event));
            } else if (level == 5) {
                OFLog.m185w(loggerTag, "Log:\n" + log_(event));
            } else if (level == 6) {
                OFLog.m182e(loggerTag, "Log:\n" + log_(event));
            }
        } catch (Exception e) {
            OFLog.m182e(loggerTag, "log save failed" + e.getLocalizedMessage());
        }
    }

    private String log_(IAnalyticsLogger logger) {
        Map<String, Object> o = logger.getMap();
        String _log = JsonCoder.generateJson(o);
        store(_log);
        this.count++;
        if (this.count >= batch_num_trigger) {
            OFLog.m181d(this.tag, "log batch upload triggered");
            upload();
            this.count = 0;
        }
        return _log;
    }

    public Map<String, Object> getInfo() {
        if (this.info == null || this.info.isEmpty()) {
            initInfo();
        }
        return this.info;
    }

    public void store(String eventJSON) {
        if (eventJSON != null) {
            AnalyticsDBManager manager = AnalyticsDBManager.instance(this.mContext);
            manager.insertLog(eventJSON);
        }
    }

    public void upload() {
        if (this.waiting) {
            OFLog.m181d(this.tag, "Waiting for response,skip upload this time");
            return;
        }
        this.waiting = true;
        List<Map<String, Object>> readyLog = AnalyticsDBManager.instance(this.mContext).getAllItems();
        int len = readyLog.size();
        Long startid = null;
        List<Map<String, Object>> array = new ArrayList<>(100);
        int count = 0;
        for (Map<String, Object> item : readyLog) {
            String logJson = (String) item.get(AnalyticsDBManager.KEY_JSON);
            OFLog.m184v(this.tag, logJson);
            OFLog.m184v(this.tag, "--------");
            Long logTime = (Long) item.get(AnalyticsDBManager.KEY_TIME);
            Long deltaMillis = Long.valueOf(System.currentTimeMillis() - logTime.longValue());
            double deltaSeconds = deltaMillis.longValue() / 1000.0f;
            Map<String, Object> single = (Map) JsonCoder.parse(logJson);
            for (String wrapperKey : single.keySet()) {
                Map<String, Object> wrapperObject = (Map) single.get(wrapperKey);
                Map<String, Object> event = (Map) wrapperObject.get("event");
                if (event != null) {
                    event.put("time_delta", Double.valueOf(deltaSeconds));
                }
            }
            array.add(single);
            count++;
            if (count == 1) {
                startid = (Long) item.get(AnalyticsDBManager.KEY_ID);
            }
            if (count == 100) {
                Long endid = (Long) item.get(AnalyticsDBManager.KEY_ID);
                OrderedArgList arguments = new OrderedArgList();
                arguments.put("event", JsonCoder.generateJson(array));
                arguments.put("info", JsonCoder.generateJson(getInfo()));
                if (OFLog.willLog(3)) {
                    OFLog.m181d(this.tag, String.format("try upload from %d to %d :", startid, endid));
                    OFLog.m181d(this.tag, JsonCoder.generateJson(getInfo()));
                    OFLog.m181d(this.tag, JsonCoder.generateJson(array));
                }
                AnalyticsRequest req = new AnalyticsRequest(startid, endid, arguments);
                req.launch();
                array = new ArrayList<>(100);
                count = 0;
            }
        }
        if (len > 0) {
            Long endid2 = (Long) readyLog.get(len - 1).get(AnalyticsDBManager.KEY_ID);
            OrderedArgList arguments2 = new OrderedArgList();
            arguments2.put("event", JsonCoder.generateJson(array));
            arguments2.put("info", JsonCoder.generateJson(getInfo()));
            OFLog.m181d(this.tag, String.format("try upload from %d to %d :", startid, endid2));
            OFLog.m181d(this.tag, JsonCoder.generateJson(getInfo()));
            OFLog.m181d(this.tag, JsonCoder.generateJson(array));
            AnalyticsRequest req2 = new AnalyticsRequest(startid, endid2, arguments2);
            req2.launch();
        }
    }

    public void deleteLog(Long id) {
        AnalyticsDBManager.instance(this.mContext).removeLog(id.longValue());
    }

    public void deleteLog(Long startid, Long endid) {
        AnalyticsDBManager.instance(this.mContext).removeLog(startid.longValue(), endid.longValue());
    }

    public static void setBatch_num_trigger(int batch_num_trigger2) {
        batch_num_trigger = batch_num_trigger2;
    }

    public static int getBatch_num_trigger() {
        return batch_num_trigger;
    }

    public void unlock() {
        this.waiting = false;
    }
}
