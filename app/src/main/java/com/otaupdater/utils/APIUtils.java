package com.otaupdater.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.otaupdater.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class APIUtils {

    public static void fetchKernelInfo(Context ctx, APICallback callback) {
        if (!PropUtils.isKernelOtaEnabled()) {
            if (callback != null) callback.onError(ctx.getString(R.string.kernel_unsupported), null);
            return;
        }

        JSONObject data = new JSONObject();
        try {
            data.put("device", Utils.getDevice());
            data.put("rom_id", PropUtils.getKernelOtaID());
        } catch (JSONException ignored) {
        }

        new APITask(ctx, Config.KERNEL_PULL_URL, data, callback).execute();
    }

    public static class APITask extends AsyncTask<Void, Void, Boolean> {
        private final Context ctx;
        private final String endpoint;
        private final JSONObject data;
        private final APICallback callback;

        private String respMsg;
        private JSONObject respObj;

        public APITask(Context ctx, String endpoint, JSONObject data, APICallback callback) {
            this.ctx = ctx;
            this.endpoint = endpoint;
            this.data = data;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            if (callback != null) callback.onStart(this);
        }

        @Override
        protected Boolean doInBackground(Void... unused) {
            if (!Utils.dataAvailable(ctx)) {
                respMsg = ctx.getString(R.string.alert_nodata_title);
                return false;
            }

            JSONObject resp = makeServerCall(endpoint, data);
            if (resp == null || resp.length() == 0) {
                respMsg = ctx.getString(R.string.unknown_error);
                return false;
            }

            respMsg = resp.optString("message", null);
            respObj = resp.optJSONObject("data");

            return resp.optBoolean("success", true);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                if (success) {
                    callback.onSuccess(respMsg, respObj);
                } else {
                    callback.onError(respMsg, respObj);
                }

                callback.onComplete(success);
            }
        }

        @Override
        protected void onCancelled(Boolean aVoid) {
            if (callback != null) callback.onCancel();
        }

        public JSONObject makeServerCall(String endpoint, JSONObject data) {
            Log.v(Config.LOG_TAG + "serverCall", endpoint);

            try {
                HttpClient http = new DefaultHttpClient();

                String reqBody = data == null ? "" : data.toString();

                HttpPost req = new HttpPost(Config.SITE_BASE_URL + endpoint);

                req.addHeader("Content-Type", "application/json");
                req.addHeader("Accept", "application/json");

                req.addHeader("X-Device-ID", Utils.getDeviceID(ctx));

                req.setEntity(new StringEntity(reqBody, "UTF-8"));

                HttpResponse resp = http.execute(req);

                int status = resp.getStatusLine().getStatusCode();
                if (status != HttpStatus.SC_OK) {
                    Log.w(Config.LOG_TAG + "serverCall", "non-200 response to " + endpoint + " (" + status + ")");
                }

                HttpEntity e = resp.getEntity();
                if (e == null) {
                    Log.w(Config.LOG_TAG + "serverCall", "no response to " + endpoint);
                    return null;
                }

                String respBody = EntityUtils.toString(e);
                if (respBody.length() == 0) {
                    Log.w(Config.LOG_TAG + "serverCall", "empty response to " + endpoint);
                    return null;
                }

                JSONObject json = new JSONObject(respBody);
                if (json.length() == 0) {
                    Log.w(Config.LOG_TAG + "serverCall", "empty response to " + endpoint);
                    return null;
                }

                if (!json.has("success")) {
                    Log.w(Config.LOG_TAG + "serverCall", "malformed response to " + endpoint);
                    return null;
                }

                if (!json.getBoolean("success")) {
                    Log.w(Config.LOG_TAG + "serverCall", "error received from " + endpoint + " (" + json.optString("error", "unknown") + ")");
                }

                return json;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public interface APICallback {
        void onStart(APITask task);
        void onSuccess(String message, JSONObject respObj);
        void onError(String message, JSONObject respObj);
        void onCancel();
        void onComplete(boolean success);
    }

    public static abstract class APIAdapter implements APICallback {
        @Override public void onStart(APITask task) { }
        @Override public void onSuccess(String message, JSONObject respObj) { }
        @Override public void onError(String message, JSONObject respObj) { }
        @Override public void onCancel() { }
        @Override public void onComplete(boolean success) { }
    }
}