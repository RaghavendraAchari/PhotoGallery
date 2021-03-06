package com.raghav.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Fetcher {
    public static int page = 1;
    private static final String TAG = "Fetcher";
    private static final String API_KEY = "99c6999aec594bddaf5a2d92caeba6d9";
    private static final String FETCH_RECENT_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key",API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();


    public byte[] getUrlBytes(String urlPath) throws IOException {
        URL url = new URL(urlPath);
        //HTTP connection

        HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
        //get bytes from url
        try{
            //get input (from connection object) and output stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ": with " + urlPath);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer))>0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlPath)throws IOException{
        return new String(getUrlBytes(urlPath));
    }

    private String buildUrl(String method , String query){
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);
        if(method.equals(SEARCH_METHOD)){
            uriBuilder.appendQueryParameter("text",query);
        }
        return uriBuilder.build().toString();
    }

    private List<GalleryItem> downloadGalleryItems(String url){
        List<GalleryItem> list = new ArrayList<>();
        try{
            String jasonString = getUrlString(url);
            Log.i(TAG, "Received Json: " + jasonString );
            JSONObject jsonBody = new JSONObject(jasonString);
            parseItems(list,jsonBody);

        }catch (IOException e){
            Log.e(TAG, "Failed to fetch json" + e );
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse json" + je );
        }
        return list;
    }

    public List<GalleryItem> fetchRecentPhotos(){
        String url = buildUrl(FETCH_RECENT_METHOD, null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query){
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }

    private void parseItems(List<GalleryItem> list, JSONObject jsonBody ) throws IOException, JSONException{
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoArray = photosJsonObject.getJSONArray("photo");

        for (int i=0; i<photoArray.length();i++){
            JSONObject photoJsonObject = photoArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setCaption(photoJsonObject.getString("title"));
            item.setId(photoJsonObject.getString("id"));

            if(!photoJsonObject.has("url_s")){
                continue;
            }
            item.setUrl(photoJsonObject.getString("url_s"));
            list.add(item);

        }

    }
}
