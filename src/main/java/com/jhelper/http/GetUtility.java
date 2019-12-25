package com.jhelper.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.helper.exception.HttpException;

/**
 * 
 * @author ttdat
 *
 */
public class GetUtility implements Serializable {
	
	private static final long serialVersionUID = 581967466767481936L;

    private String url;
    private Map<String, String> params;
    private Map<String, String> headers;
    private HttpURLConnection httpCon;
    private int timeout = 30000;
    private String chamHoi = "?";

    public GetUtility() {
        this.params = new HashMap<String, String>();
        this.headers = new HashMap<String, String>();
    }

    public GetUtility(String url, Map<String, String> params, Map<String, String> headers) {
        this.url = url;
        this.params = params;
        this.headers = headers;
    }

    /**
     * Generate url with parameter
     * @return
     */
    public String generateUrlParam() {
        StringBuilder fromUrl = new StringBuilder();
        fromUrl.append(url);
        if (params != null && !params.isEmpty()) {
            fromUrl.append(this.chamHoi);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                fromUrl.append("&");
                fromUrl.append(entry.getKey());
                fromUrl.append("=");
                fromUrl.append(entry.getValue());
            }
        }
        String generatedUrl = fromUrl.toString().replace("?&", "?");
        return generatedUrl;
    }
    
   /**
    * Connect to service
    * @return
    * @throws IOException
    */
    public int connect() throws IOException  {
        URL objUrl = null;
        try {
        	objUrl = new URL(generateUrlParam());
        } catch (MalformedURLException e) {
        	return 404;
        }
        httpCon = (HttpURLConnection) objUrl.openConnection();
        //time out macdinh là 30 giay
        httpCon.setReadTimeout(timeout);
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                httpCon.setRequestProperty(key, headers.get(key));
            }
        }
        int status = httpCon.getResponseCode();
        return status;
    }
    
    /**
     * Get payload from http connection stream
     * @return
     * @throws HttpException
     */
    public String getPayload() throws HttpException {
    	String content = "";
    	try {
    		if (httpCon == null) {
        		throw new HttpException("Sửa dụng connect() trước khi sử dụng getPayload()"); 
        	}
        	content = inputStreamToString(httpCon.getInputStream());
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new HttpException("Liên thông thất bại, vui lòng kiểm tra lại cấu hình liên thông");
    	}
    	if (content.isEmpty()) {
    		throw new HttpException("Liên thông thất bại, vui lòng kiểm tra lại cấu hình liên thông");
    	}
        return content;
    }
    
    /**
     * Save file with byte[] result
     * Edit this funtion service return file as base64 or other type
     * @param path
     * @param file_name
     * @return
     * @throws Exception
     */
    public String saveFile(String path, String file_name) throws Exception {
    	String file = path + "/" + file_name;
    	if (httpCon == null) {
    		throw new NullPointerException("Use connect() before getPayload()"); 
    	} else if (file == null || file.isEmpty()) {
    		throw new NullPointerException("Invalid parameters");
    	}
    	inputStreamToFile(httpCon.getInputStream(), file);
    	return file;
    }

    /**
     * Read payload from http stream
     * @param in
     * @return
     * @throws IOException
     */
    private String inputStreamToString(InputStream in) throws IOException {
        String result = "";
        BufferedReader reader = null;
        if (in == null) {
            return result;
        }
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            result = out.toString();
            return result;
        } catch (Exception e) {
            return result;
        } finally {
            reader.close();
        }
    }
    
    /**
     * Http stream to file
     * Service result byte array
     * @param in
     * @param url_file
     * @throws IOException
     */
    private void inputStreamToFile(InputStream in, String url_file) throws IOException {
        try {
            File targetFile = new File(url_file);
            OutputStream out = new FileOutputStream(targetFile);
            String str = getPayload().replace("\"", "");
            Gson g = new Gson();
            byte[] data = new byte[0];
            data = g.fromJson(str, data.getClass());
            if (data != null) {
            	out.write(data);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public HttpURLConnection getHttpCon() {
        return httpCon;
    }

    public void setHttpCon(HttpURLConnection httpCon) {
        this.httpCon = httpCon;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

	
	public int getTimeout() {
	
		return timeout;
	}

	
	public void setTimeout(int timeout) {
	
		this.timeout = timeout;
	}

	public String getChamHoi() {
		return chamHoi;
	}

	public void setChamHoi(String chamHoi) {
		this.chamHoi = chamHoi;
	}
}
