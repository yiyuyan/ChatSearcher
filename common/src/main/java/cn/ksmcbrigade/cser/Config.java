package cn.ksmcbrigade.cser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * &#064;Author: KSmc_brigade
 * &#064;Date: 2025/9/5 下午6:21
 */
public class Config {

    public final File file;
    public boolean value = true;

    public Config(File file){
        this.file = file;
        this.initOrReload();
    }

    public void initOrReload(){
        try {
            if(!file.exists()){
                JsonObject object = new JsonObject();
                object.addProperty("equalsIgnoreCase",value);
                FileUtils.writeStringToFile(file, object.toString());
            }
            this.value = JsonParser.parseString(FileUtils.readFileToString(file)).getAsJsonObject().get("equalsIgnoreCase").getAsBoolean();
        } catch (IOException e) {
            Constants.LOG.error("Can't init or reload the mod config.",e);
        }
    }
}
