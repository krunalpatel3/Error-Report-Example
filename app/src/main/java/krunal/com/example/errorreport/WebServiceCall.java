package krunal.com.example.errorreport;

import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class WebServiceCall {

    static String response = null;
    public final static int POST = 2;
    DefaultHttpClient httpClient;
    HttpResponse httpResponse = null;

    public WebServiceCall() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    /*
     * Making service call
     *
     * @url - url to make request
     *
     * @method - http request method
     */
    public String makeServiceCall(String url, int i) {
        return this.makeServiceCall(url, null, i);
    }

    /*
     * Making service call
     *
     * @url - url to make request
     *
     * @method - http request method
     *
     * @params - http request params
     */
    public String makeServiceCall(String url, List<NameValuePair> params, int i) {
        try {

            // http client
            if (i == 1) {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);//5000
                HttpConnectionParams.setSoTimeout(httpParameters, 20000);//3000
                httpClient = new DefaultHttpClient(httpParameters);
            } else {
//				HttpParams httpParameters = new BasicHttpParams();
//				HttpConnectionParams.setConnectionTimeout(httpParameters, 50000);
//				HttpConnectionParams.setSoTimeout(httpParameters, 30000);
                httpClient = new DefaultHttpClient();
            }
            HttpEntity httpEntity = null;
            // Checking http request method type
            HttpPost httpPost = new HttpPost(url);

            // adding post params
            if (params != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
            }
//			System.setProperty("http.keepAlive", "false");
            httpResponse = httpClient.execute(httpPost);

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

            Log.e("response", response);
            Log.e("json111", response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public int stopservice() {
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
            return 1;
        } else {
            return 0;
        }
    }

    public void cleardata() {
        response = null;
        httpResponse = null;
    }

    public void settimeout() {
    }
}
