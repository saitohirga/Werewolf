package com.dogonfire.werewolf;

import java.net.URLConnection;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateChecker
{
    private final int projectID = 39095;
    private final String apiKey;
    private static final String API_NAME_VALUE = "name";
    private static final String API_LINK_VALUE = "downloadUrl";
    private static final String API_RELEASE_TYPE_VALUE = "releaseType";
    private static final String API_FILE_NAME_VALUE = "fileName";
    private static final String API_GAME_VERSION_VALUE = "gameVersion";
    private static final String API_QUERY = "/servermods/files?projectIds=";
    private static final String API_HOST = "https://api.curseforge.com";
    private static String versionName;
    private static String versionLink;
    private static String versionType;
    private static String versionFileName;
    private static String versionGameVersion;
    
    public UpdateChecker() {
        super();
        this.apiKey = null;
    }
    
    public String getLatestVersionName() {
        URL url = null;
        String versionName = null;
        try {
            url = new URL("https://api.curseforge.com/servermods/files?projectIds=39095");
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return versionName;
        }
        try {
            final URLConnection conn = url.openConnection();
            if (this.apiKey != null) {
                conn.addRequestProperty("X-API-Key", this.apiKey);
            }
            conn.addRequestProperty("User-Agent", "Werewolf (by DoggyOnFire)");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();
            final JSONArray array = (JSONArray)JSONValue.parse(response);
            if (array.size() > 0) {
                final JSONObject latest = (JSONObject)array.get(array.size() - 1);
                versionName = (String)latest.get((Object)"name");
                UpdateChecker.versionLink = (String)latest.get((Object)"downloadUrl");
                UpdateChecker.versionType = (String)latest.get((Object)"releaseType");
                UpdateChecker.versionFileName = (String)latest.get((Object)"fileName");
                UpdateChecker.versionGameVersion = (String)latest.get((Object)"gameVersion");
            }
            else {
                System.out.println("There are no files for this project");
            }
        }
        catch (IOException e2) {
            e2.printStackTrace();
            return versionName;
        }
        return versionName;
    }
    
    public String getLatestVersionGameVersion() {
        return UpdateChecker.versionGameVersion;
    }
    
    public String getLatestVersionLink() {
        return UpdateChecker.versionLink;
    }
}
