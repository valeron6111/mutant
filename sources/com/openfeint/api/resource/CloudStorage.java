package com.openfeint.api.resource;

import com.openfeint.internal.APICallback;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.Util;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.request.CompressedBlobDownloadRequest;
import com.openfeint.internal.request.Compression;
import com.openfeint.internal.request.JSONRequest;
import com.openfeint.internal.resource.ServerException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonFactory;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;

/* loaded from: classes.dex */
public class CloudStorage {
    public static int MAX_SIZE = 262144;
    private static Pattern sValidKeyPattern;

    public static abstract class DeleteCB extends APICallback {
        public abstract void onSuccess();
    }

    public static abstract class ListCB extends APICallback {
        public abstract void onSuccess(List<String> list);
    }

    public static abstract class LoadCB extends APICallback {
        public abstract void onSuccess(byte[] bArr);
    }

    public static abstract class SaveCB extends APICallback {
        public abstract void onSuccess();
    }

    public static void list(final ListCB cb) {
        String userID = OpenFeintInternal.getInstance().getUserID();
        if ((userID == null || userID.length() == 0) && cb != null) {
            cb.onFailure("A user must be logged in to list their persisted CloudStorage blobs.");
        }
        String appID = OpenFeintInternal.getInstance().getAppID();
        final String path = "/xp/users/" + userID + "/games/" + appID + "/save_cards";
        new JSONRequest() { // from class: com.openfeint.api.resource.CloudStorage.1
            @Override // com.openfeint.internal.request.BaseRequest
            public boolean wantsLogin() {
                return true;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return path;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "GET";
            }

            @Override // com.openfeint.internal.request.JSONRequest
            protected Object parseJson(byte[] bodyStream) {
                Object o = Util.getObjFromJson(bodyStream);
                if (o == null || !(o instanceof ServerException)) {
                    JsonFactory jsonFactory = new JsonFactory();
                    try {
                        JsonParser jp = jsonFactory.createJsonParser(bodyStream);
                        if (jp.nextToken() != JsonToken.START_OBJECT) {
                            throw new JsonParseException("Couldn't find toplevel wrapper object.", jp.getTokenLocation());
                        }
                        if (jp.nextToken() != JsonToken.FIELD_NAME) {
                            throw new JsonParseException("Couldn't find toplevel wrapper object.", jp.getTokenLocation());
                        }
                        String hopefullyCloudStorages = jp.getText();
                        if (!hopefullyCloudStorages.equals("save_cards")) {
                            throw new JsonParseException("Couldn't find toplevel wrapper object.", jp.getTokenLocation());
                        }
                        if (jp.nextToken() != JsonToken.START_ARRAY) {
                            throw new JsonParseException("Couldn't find savecard array.", jp.getTokenLocation());
                        }
                        ArrayList<String> rv = new ArrayList<>();
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            if (jp.getCurrentToken() != JsonToken.VALUE_STRING) {
                                throw new JsonParseException("Unexpected non-string in savecard array.", jp.getTokenLocation());
                            }
                            rv.add(jp.getText());
                        }
                        return rv;
                    } catch (Exception e) {
                        OFLog.m182e(TAG, e.getMessage());
                        return new ServerException("JSONError", "Unexpected response format");
                    }
                }
                return o;
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onSuccess(Object responseBody) {
                if (cb != null) {
                    cb.onSuccess((List) responseBody);
                }
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onFailure(String reason) {
                if (cb != null) {
                    cb.onFailure(reason);
                }
            }
        }.launch();
    }

    public static void load(String key, final LoadCB cb) {
        String userID = OpenFeintInternal.getInstance().getUserID();
        if ((userID == null || userID.length() == 0) && cb != null) {
            cb.onFailure("A user must be logged in to load data from a CloudStorage blob.");
        }
        if (!isValidKey(key) && cb != null) {
            cb.onFailure("'" + (key == null ? "(null)" : key) + "' is not a valid CloudStorage key.");
        }
        String appID = OpenFeintInternal.getInstance().getAppID();
        final String path = "/xp/users/" + userID + "/games/" + appID + "/save_cards/" + key;
        new CompressedBlobDownloadRequest() { // from class: com.openfeint.api.resource.CloudStorage.2
            @Override // com.openfeint.internal.request.BaseRequest
            public boolean wantsLogin() {
                return true;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return path;
            }

            @Override // com.openfeint.internal.request.CompressedBlobDownloadRequest
            public void onSuccessDecompress(byte[] body) {
                if (cb != null) {
                    cb.onSuccess(body);
                }
            }

            @Override // com.openfeint.internal.request.DownloadRequest
            public void onFailure(String exceptionMessage) {
                if (cb != null) {
                    cb.onFailure(exceptionMessage);
                }
            }
        }.launch();
    }

    public static void save(String key, final byte[] data, final SaveCB cb) {
        String userID = OpenFeintInternal.getInstance().getUserID();
        if (userID == null || userID.length() == 0) {
            if (cb != null) {
                cb.onFailure("Cannot save because the owner of this CloudStorage blob is not logged in.");
                return;
            }
            return;
        }
        if (!isValidKey(key)) {
            if (cb != null) {
                StringBuilder append = new StringBuilder().append("'");
                if (key == null) {
                    key = "(null)";
                }
                cb.onFailure(append.append(key).append("' is not a valid CloudStorage key.").toString());
                return;
            }
            return;
        }
        if (data == null || data.length == 0) {
            if (cb != null) {
                cb.onFailure("data is empty.  data must be set before saving.");
            }
        } else if (MAX_SIZE < data.length) {
            if (cb != null) {
                cb.onFailure("You cannot exceed 256 kB per save card");
            }
        } else {
            String appID = OpenFeintInternal.getInstance().getAppID();
            final String path = "/xp/users/" + userID + "/games/" + appID + "/save_cards/" + key;
            new JSONRequest() { // from class: com.openfeint.api.resource.CloudStorage.3
                @Override // com.openfeint.internal.request.BaseRequest
                public boolean wantsLogin() {
                    return true;
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String path() {
                    return path;
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String method() {
                    return "PUT";
                }

                @Override // com.openfeint.internal.request.JSONContentRequest, com.openfeint.internal.request.BaseRequest
                protected HttpUriRequest generateRequest() {
                    HttpPut retval = new HttpPut(url());
                    retval.setEntity(new ByteArrayEntity(Compression.compress(data)));
                    addParams(retval);
                    return retval;
                }

                @Override // com.openfeint.internal.request.JSONRequest
                public void onSuccess(Object body) {
                    if (cb != null) {
                        cb.onSuccess();
                    }
                }

                @Override // com.openfeint.internal.request.JSONRequest
                public void onFailure(String reason) {
                    if (cb != null) {
                        cb.onFailure(reason);
                    }
                }
            }.launch();
        }
    }

    public static void delete(String key, final DeleteCB cb) {
        String userID = OpenFeintInternal.getInstance().getUserID();
        if ((userID == null || userID.length() == 0) && cb != null) {
            cb.onFailure("The user who owns this CloudStorage blob is not logged in. The CloudStorage blob specified was not deleted.");
        }
        if (!isValidKey(key) && cb != null) {
            cb.onFailure("'" + (key == null ? "(null)" : key) + "' is not a valid CloudStorage key.");
        }
        String appID = OpenFeintInternal.getInstance().getAppID();
        final String path = "/xp/users/" + userID + "/games/" + appID + "/save_cards/" + key;
        new JSONRequest() { // from class: com.openfeint.api.resource.CloudStorage.4
            @Override // com.openfeint.internal.request.BaseRequest
            public boolean wantsLogin() {
                return true;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return path;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "DELETE";
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onSuccess(Object body) {
                if (cb != null) {
                    cb.onSuccess();
                }
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onFailure(String reason) {
                if (cb != null) {
                    cb.onFailure(reason);
                }
            }
        }.launch();
    }

    public static boolean isValidKey(String key) {
        if (key == null) {
            return false;
        }
        if (sValidKeyPattern == null) {
            try {
                sValidKeyPattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9-_]*");
            } catch (PatternSyntaxException e) {
                return false;
            }
        }
        return sValidKeyPattern.matcher(key).matches();
    }
}
