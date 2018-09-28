package application;

import jodd.props.Props;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

public class API {

    private static Logger logger = LogManager.getLogger(API.class);

    public static void main(String[] args) {

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

        Props props = null;

        try {
           props = ApplicationContext.getInstance().getProps();
        } catch (Exception e) {
            logger.error("Error getting application properties file terminanting...",e);
            System.exit(0);
        }

        int apiPort = Optional.ofNullable(Integer.valueOf(props.getValue("APIPort"))).orElse(0);

        if(apiPort==0){
            logger.warn("[APIPort] not configured in application.properties file, terminating ...");
            System.exit(0);
        }

        //check if api_port is configured
        port(Integer.valueOf(props.getValue("APIPort")));

        //route to post log file
        post("/api/v1/logEntries/upload", LogProcesscor.postLogFile);
        get("/api/v1/logEntries", LogProcesscor.list);

    }
}
