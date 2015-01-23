/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.crypto.Base64;
import se.unlogic.standardutils.date.PooledSimpleDateFormat;
import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.mime.MimeUtils;
import se.unlogic.standardutils.readwrite.ReadWriteUtils;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.webutils.http.enums.ContentDisposition;

public class HTTPUtils {

	private static final PooledSimpleDateFormat RFC1123_DATE_FORMATTER = new PooledSimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US, TimeZone.getTimeZone("GMT"));	
	private static final Pattern HTTP_PATTERN = Pattern.compile("(http|https):\\/\\/(\\w+:{0,1}\\w*@)?(\\S+)(:[0-9]+)?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/]))?");
	private static final String DEFAULT_CONTENT_TYPE = "text/html;";

	public static boolean isValidURL(String url) {

		return HTTP_PATTERN.matcher(url).matches();
	}

	public static void sendReponse(String output, HttpServletResponse res) throws IOException {

		sendReponse(output, DEFAULT_CONTENT_TYPE, res);
	}

	public static void sendReponse(String output, String contentType, HttpServletResponse res) throws IOException {

		sendReponse(output, contentType, "ISO-8859-1", res);
	}

	public static void sendReponse(String output, String contentType, String encoding, HttpServletResponse res) throws IOException {

		res.setCharacterEncoding(encoding);
		res.setContentType(contentType);
		PrintWriter writer = res.getWriter();
		writer.append(output);
		writer.flush();
	}

	public static void sendReponse(InputStream inputStream, String contentType, HttpServletResponse res) throws IOException {

		sendReponse(inputStream, contentType, "ISO-8859-1", res);
	}

	public static void sendReponse(InputStream inputStream, String contentType, String encoding, HttpServletResponse res) throws IOException {

		OutputStream outputStream = null;

		try{
			outputStream = res.getOutputStream();

			res.setCharacterEncoding(encoding);
			res.setContentType(contentType);

			StreamUtils.transfer(inputStream, outputStream);

		}finally{

			StreamUtils.closeStream(outputStream);
		}
	}

	public static void setContentLength(long contentLength, HttpServletResponse res) {

		if (contentLength <= Integer.MAX_VALUE && contentLength >= 0) {

			res.setContentLength((int) contentLength);

		} else if (contentLength > Integer.MAX_VALUE) {

			res.addHeader("Content-Length", Long.toString(contentLength));
		}
	}

	public static HttpURLConnection getHttpURLConnection(String endpoint, List<Entry<String, String>> requestParameters) throws IOException {

		URL url;

		if (!CollectionUtils.isEmpty(requestParameters)) {

			StringBuilder urlStr = new StringBuilder(endpoint);

			if(!endpoint.contains("?")){

				urlStr.append("?");

			}else{

				urlStr.append("&");
			}

			Iterator<Entry<String, String>> iterator = requestParameters.iterator();
			Entry<String, String> entry;

			while (iterator.hasNext()) {
				entry = iterator.next();
				urlStr.append(entry.getKey());
				urlStr.append("=");
				urlStr.append(entry.getValue());
				if (iterator.hasNext()) {
					urlStr.append("&");
				}
			}

			url = new URL(urlStr.toString());

		}else{

			url = new URL(endpoint);
		}

		return (HttpURLConnection)url.openConnection();
	}

	public static void setBasicAuthentication(HttpURLConnection connection, String username, String password){

		String combinedString = username + ":" + password;

		connection.setRequestProperty  ("Authorization", "Basic " + new String(Base64.encodeBytesToBytes(combinedString.getBytes())));
	}

	/**
	 * 
	 * @param endpoint
	 *            - the HTTP server endpoint
	 * @param requestParameters
	 *            - a list with key and value entries corresponding to the request parameters names and values
	 * @return The server response or <code>null</code> if there was no response
	 * @throws IOException
	 */
	public static String sendHTTPGetRequest(String endpoint, List<Entry<String, String>> requestParameters, String username, String password) throws IOException {

		HttpURLConnection connection = null;
		InputStreamReader reader = null;
		StringBuilder response = null;

		if (endpoint.startsWith("http://")) {

			try {
				connection = getHttpURLConnection(endpoint,requestParameters);

				if(username != null && password != null){

					setBasicAuthentication(connection, username, password);
				}

				// Get the response
				reader = new InputStreamReader(connection.getInputStream());

				StringWriter stringWriter = new StringWriter();

				ReadWriteUtils.transfer(reader, stringWriter);

				return stringWriter.toString();

			} finally {
				ReadWriteUtils.closeReader(reader);

				if (connection != null) {
					connection.disconnect();
				}
			}
		}
		return response == null ? null : response.toString();
	}

	public static void sendHTTPGetRequest(String endpoint, List<Entry<String, String>> requestParameters, HttpServletResponse res) throws IOException {

		sendHTTPGetRequest(endpoint, requestParameters, res.getOutputStream());

		res.flushBuffer();
	}

	public static void sendHTTPGetRequest(String endpoint, List<Entry<String, String>> requestParameters, OutputStream outputStream) throws IOException {

		HttpURLConnection connection = null;

		try {
			connection = getHttpURLConnection(endpoint,requestParameters);

			StreamUtils.transfer(connection.getInputStream(), outputStream);

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static void sendHTTPPostRequest(Reader data, URL endpoint, Writer output, String encoding) throws IOException {
		
		sendHTTPPostRequest(data, endpoint, output, encoding, null, null);
	}
	
	public static void sendHTTPPostRequest(Reader data, URL endpoint, Writer output, String encoding, Integer connectionTimeout, Integer readTimeout) throws IOException {

		HttpURLConnection connection = null;
		Writer writer = null;
		Reader reader = null;
		InputStream inputStream = null;

		try {
			// Make the connection
			connection = (HttpURLConnection) endpoint.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setAllowUserInteraction(false);
			
			//TODO the content type should not be hard coded
			connection.setRequestProperty("Content-type", "text/xml; charset=" + encoding);
			
			if(connectionTimeout != null){
				
				connection.setConnectTimeout(connectionTimeout);
			}

			if(readTimeout != null){
				
				connection.setReadTimeout(readTimeout);
			}			
			
			try {
				// Write data
				writer = new OutputStreamWriter(connection.getOutputStream(), encoding);

				ReadWriteUtils.transfer(data, writer);
			} finally {
				ReadWriteUtils.closeWriter(writer);
			}

			try {
				// Read response
				inputStream = connection.getInputStream();
				reader = new InputStreamReader(inputStream);
				ReadWriteUtils.transfer(reader, output);
			} finally {
				ReadWriteUtils.closeReader(reader);
				StreamUtils.closeStream(inputStream);
			}
		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	public static void sendFile(File file, HttpServletRequest req, HttpServletResponse res, ContentDisposition disposition) throws IOException{
		
		sendFile(file, null, req, res, disposition);
	}
	
	public static void sendFile(File file, String filename, HttpServletRequest req, HttpServletResponse res, ContentDisposition disposition) throws IOException{
		
		String lastModifiedString = null;

		lastModifiedString = RFC1123_DATE_FORMATTER.format(new Date(file.lastModified()));

		String modifiedSinceString = req.getHeader("If-Modified-Since");

		if (lastModifiedString != null && modifiedSinceString != null && modifiedSinceString.equalsIgnoreCase(lastModifiedString)) {

			res.setStatus(304);

			try {
				res.flushBuffer();
			} catch (IOException e) {}

			return;
		}

		res.setHeader("Last-Modified", lastModifiedString);

		if(filename == null){
			
			filename = file.getName();
		}
		
		HTTPUtils.setContentLength(file.length(), res);
		res.setHeader("Content-Disposition", disposition.getValue() + "; filename=\"" + FileUtils.toValidHttpFilename(filename) + "\"");

		String contentType = MimeUtils.getMimeType(file);

		if (contentType != null) {
			res.setContentType(contentType);
		} else {
			res.setContentType("application/x-unknown-mime-type");
		}

		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			inputStream = new FileInputStream(file);
			outputStream = res.getOutputStream();

			StreamUtils.transfer(inputStream, outputStream);

		} finally {
			StreamUtils.closeStream(inputStream);
			StreamUtils.closeStream(outputStream);
		}
	}

	public static void sendBlob(Blob blob, String filename, HttpServletRequest req, HttpServletResponse res, ContentDisposition disposition) throws SQLException, IOException {

		HTTPUtils.setContentLength(blob.length(), res);
		res.setHeader("Content-Disposition", disposition.getValue() + "; filename=\"" + FileUtils.toValidHttpFilename(filename) + "\"");

		String contentType = MimeUtils.getMimeType(filename);

		if (contentType != null) {
			res.setContentType(contentType);
		} else {
			res.setContentType("application/x-unknown-mime-type");
		}

		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			inputStream = blob.getBinaryStream();
			outputStream = res.getOutputStream();

			StreamUtils.transfer(inputStream, outputStream);

		} finally {
			StreamUtils.closeStream(inputStream);
			StreamUtils.closeStream(outputStream);
		}		
	}
}