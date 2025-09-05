package cn.ksmcbrigade.cser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Constants {

    public static final String MOD_ID = "cser";
    public static final String MOD_NAME = "ChatSearcher";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final Config CONFIG = new Config(new File("configs/"+MOD_ID+"-config.json"));
}
