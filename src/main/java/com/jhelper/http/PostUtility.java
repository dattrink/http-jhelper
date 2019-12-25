/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jhelper.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import com.helper.exception.HttpException;

/**
 *
 * @author ttdat
 */
public class PostUtility implements Serializable {

	private static final long serialVersionUID = 8851001145171713341L;
	
	private String url;
	private final String boundary;
    private HttpURLConnection httpConn;
    private String charset = "UTF-8";
    private OutputStream outputStream;
    private PrintWriter writer;
    private String contentType = APPLICATION_FROM_URLENCODED;
    private Map<String, String> headers;
    private static final String LINE_FEED = "\r\n";
    public static final String APPLICATION_FROM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String MULTIPAT_FROM_DATA = "multipart/form-data";
    
    public PostUtility(String requestURL, Map<String, String> params, Map<String, String> headers) throws IOException {
    	this.url = requestURL;
        this.headers = headers;
        this.boundary = "===" + System.currentTimeMillis() + "===";
        this.constructor(this.url, this.charset, this.contentType, this.headers);
        this.setFormField(params);
    }

    public PostUtility(String requestURL, String charset, String contentType, Map<String, String> params, Map<String, String> headers) throws IOException {
    	this.url = requestURL;
        this.charset = charset;
        this.contentType = contentType;
        this.headers = headers;
        this.boundary = "===" + System.currentTimeMillis() + "===";
        this.constructor(this.url, this.charset, this.contentType, this.headers);
        this.setFormField(params);
    }
    
    /**
     * Base constructor
     * @param requestURL
     * @param charse
     * @param contentType
     * @param headers
     * @throws IOException
     */
    private void constructor(String requestURL, String charse, String contentType, Map<String, String> headers) throws IOException {
    	URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", contentType);
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                httpConn.setRequestProperty(key, headers.get(key));
            }
        }
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }

    /**
     * Set form param
     * @param params
     */
    private void setFormField(Map<String, String> params) {
        try {
            StringBuffer requestParams = new StringBuffer();
            if (params != null && params.size() > 0) {
                Iterator<String> paramIterator = params.keySet().iterator();
                while (paramIterator.hasNext()) {
                    String key = paramIterator.next();
                    String value = params.get(key);
                    requestParams.append(URLEncoder.encode(key, "UTF-8"));
                    requestParams.append("=").append(
                            URLEncoder.encode(value, "UTF-8"));
                    requestParams.append("&");
                }
                writer.write(requestParams.toString());
                writer.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Attach file
     * @param fieldName
     * @param uploadFile
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void addFilePart(String fieldName, File uploadFile) throws IOException, FileNotFoundException {
        FileInputStream inputStream = null;
        try {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            writer.append(LINE_FEED);
            writer.flush();
        } catch (IOException ex) {
        	ex.printStackTrace();
        } finally {
            inputStream.close();
        }
    }

    /**
     * Get payload from http stream
     * @return
     * @throws IOException
     */
    public String getPayload() throws IOException {
        String response = "";
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
        BufferedInputStream in = null;
        try {
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                in = new BufferedInputStream(httpConn.getInputStream());
                response = inputStreamToString(in);
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }

        } catch (IOException ex) {
        	ex.printStackTrace();
        } finally {
            in.close();
        }
        return response;
    }
    
    /**
     * Connect to service
     * @return
     * @throws HttpException
     */
    public int connect() throws HttpException {
    	try {
    		writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();
            httpConn.getResponseCode();
			return this.httpConn.getResponseCode();
		} catch (IOException e) {
			throw new HttpException();
		}
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getBoundary() {
		return boundary;
	}

	public HttpURLConnection getHttpConn() {
		return httpConn;
	}
}
