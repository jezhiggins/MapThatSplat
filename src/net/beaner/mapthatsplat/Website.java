package net.beaner.mapthatsplat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;


public class Website {
	static private String WEBSITE = "www.jezuk.co.uk";
	static private String PREFIX = "mapThatSplat/";
	
	static public String baseUrl() {
	  return "http://" + WEBSITE + "/" + PREFIX;
	}
	
	static JSONArray fetchSplatData() throws Exception {
    final String json = fetchString("splats.php");
    return new JSONArray(json);
	} // fetchSplatData
	
	static private String fetchString(final String what) throws Exception {
    final List<NameValuePair> params = createParamsList();
	  final URI uri = createURI("http", what, params);
    final HttpGet httpget = new HttpGet(uri);
    final byte[] bytes = executeRaw(httpget);
    return new String(bytes, "UTF-8");
	}
	
  static boolean uploadSplat(final double lon,
                             final double lat,
                             final String filename,
                             final String animal) throws Exception  
  {
    return postApi("upload.php",
			   "longitude", Double.toString(lon),
			   "latitude", Double.toString(lat),
			   "animal", animal,
			   "photo", new FileBody(new File(filename)));
  } // uploadSplat

  static private boolean postApi(final String path, Object...args) throws Exception
  {
    final byte[] xml = postApiRaw(path, args);
    return true;
  } // postApi
		  
	static private byte[] postApiRaw(final String path, Object... args) throws Exception
	{
		final List<NameValuePair> params = createParamsList();
		final URI uri = createURI("http", path, params);
		    
		final MultipartEntity entity = new MultipartEntity();
		for (int i = 0; i < args.length; i += 2)
		{
		    final String name = (String)args[i];
		    final Object value = args[i+1];
		    if(value instanceof String)
		        entity.addPart(name, new StringBody((String)value));
		    else
		        entity.addPart(name, (ContentBody)value);
		} // for ...
		      
		final HttpPost httppost = new HttpPost(uri);
		httppost.setEntity(entity);
		return executeRaw(httppost);
	} // postApiRaw

	static private byte[] executeRaw(final HttpRequestBase method)
		      throws ClientProtocolException, IOException
	{
		method.setHeader("User-Agent", "SplatApp/1.0");

		final HttpClient httpclient = new DefaultHttpClient();

		final HttpResponse response = httpclient.execute(method);
		    
		final HttpEntity entity = response.getEntity();
		if (entity == null)
			return null;
		     
		final StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() < 300)
			return EntityUtils.toByteArray(entity);
		    
		entity.consumeContent();
		throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
	} // executeRaw

	static private URI createURI(final String scheme,
            final String path,
            final List<NameValuePair> params) throws Exception
    {
		return URIUtils.createURI(scheme, WEBSITE, 80, PREFIX + path, URLEncodedUtils.format(params, "UTF-8"), null);
    } // createCycleStreetsURI

	static private List<NameValuePair> createParamsList(final String... args)
	{
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (int i = 0; i < args.length; i += 2) {
			params.add(new BasicNameValuePair(args[i], args[i+1]));
		}
		return params;
	} // createParamsList
}
