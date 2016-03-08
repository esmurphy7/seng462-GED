package org.seng462.webapp.deployment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Evan on 3/7/2016.
 */
public class DeploymentConfig
{
    private static final String CONFIG_PATH = "/config.json";
    private static String CONFIG_JSON = null;

    public static <T> T GetValue(String path, Class<T> classtype)
    {
        T value = null;
        JsonElement jsonElement = null;
        try {
            jsonElement = DeploymentConfig.PathToJsonObject(path);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(String.format("Could not convert %s to JsonElement",CONFIG_PATH));
        }
        Gson gson = new Gson();
        value = gson.fromJson(jsonElement, classtype);

        return value;
    }

    private static JsonElement PathToJsonObject(String path) throws IOException
    {
        // initialize the json string if not done yet
        if(CONFIG_JSON == null)
        {
            URL url = DeploymentConfig.class.getResource(CONFIG_PATH);
            byte[] encoded = Files.readAllBytes(Paths.get(url.getPath()));
            CONFIG_JSON = new String(encoded, Charset.defaultCharset());
        }

        // build and return a json object at the given path
        JsonObject obj = new GsonBuilder().create().fromJson(CONFIG_JSON, JsonObject.class);
        String[] seg = path.split("\\.");
        for (String element : seg) {
            if (obj != null) {
                JsonElement ele = obj.get(element);
                if (!ele.isJsonObject())
                    return ele;
                else
                    obj = ele.getAsJsonObject();
            } else {
                return null;
            }
        }
        return obj;
    }
}
