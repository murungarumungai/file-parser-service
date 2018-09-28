package application;

import entity.LogEntry;
import jodd.json.JsonSerializer;
import jodd.props.Props;
import lombok.Cleanup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LogService;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.*;
import java.util.*;

public class LogProcesscor {

    static Props props;
    private static Logger logger = LogManager.getLogger(API.class);

    protected static Map<String, Object> response;

    private static final JsonSerializer jsonSerializer = new JsonSerializer();

    public static Route postLogFile = (Request request, Response res) ->{

        //get props
        props = ApplicationContext.getInstance().getProps();

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(props.getValue("ProcessedLogFilesPath")));
        response = new HashMap<>();

        Part filePart = null;
        try {
            filePart = request.raw().getPart("logFile");
        } catch (IOException e) {
            logger.error(e.getMessage(),e);

            res.status(400);

            response.put("response_code", 400);
            response.put("error", e.getClass() + ": " + e.getMessage());

            return jsonResponse(res);
        } catch (ServletException e) {
            logger.error(e.getMessage(),e);

            res.status(400);
            response.put("response_code", 400);
            response.put("error", e.getClass() + ": " + e.getMessage());

            return jsonResponse(res);
        }

        if(filePart!=null){

            @Cleanup InputStream inputStream = null;
            try {
                inputStream = filePart.getInputStream();
            } catch (IOException e) {
                logger.error("Error reading log file",e);

                res.status(400);
                response.put("response_code", 400);
                response.put("error", e.getClass() + ": " + e.getMessage());

                return jsonResponse(res);
            }

            //create scanner instance to read through lines of file
            Scanner sc = null;

            sc = new Scanner(inputStream, "UTF-8");
            int lineCount = 0;
            int duplicateCount = 0;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                //split line using | as is in log file
                StringTokenizer st = new StringTokenizer(line, "|");

                //create instance of Log entity class
                LogEntry logEntry = new LogEntry();

                logEntry.setRequestParams(st.nextToken().trim());
                logEntry.setRequestIp(st.nextToken().trim());
                logEntry.setPerformanceMetrics(st.nextToken().trim());

                //persist log entry to db
                try {
                    LogService.saveLog(logEntry);
                    lineCount++;
                } catch (org.hibernate.exception.ConstraintViolationException e){
                    duplicateCount++;
                } catch (Exception e) {

                    logger.error("Error saving logEntry",e);

                    res.status(400);
                    response.put("response_code", 400);
                    response.put("error", e.getClass() + ": " + e.getMessage());

                    return jsonResponse(res);
                }
            }

            if (sc.ioException() != null) {
                throw sc.ioException();
            }

            response.put("success","Processed [" + lineCount + "] lines from [" + filePart.getName() + "] file.");
            response.put("duplicateEntries",duplicateCount);

            res.status(201);

            try {
                //move file to processed folder
                @Cleanup OutputStream outputStream = new FileOutputStream(props.getValue("ProcessedLogFilesPath") +
                        File.separator + filePart.getSubmittedFileName());
                IOUtils.copy(inputStream, outputStream);

            } catch (IOException e) {
                logger.error("Error moving file to processed folder ");
            }

            return jsonResponse(res);
        }else {
            response.put("error","logFile cannot be empty or null");
            res.status(400);
            return jsonResponse(res);
        }
    };

    public static Route list = (Request req, Response res) -> {

        response = new HashMap();

        List<LogEntry> results = null;

        Integer response_code = 200;

        Integer pagesize = Integer.parseInt(Optional.ofNullable(req.queryParams("page_size")).orElse("10"));

        Integer offset = Integer.parseInt(Optional.ofNullable(req.queryParams("offset")).orElse("0"));

        try {
            //fetch list of terminals
            results = LogService.getLogEntries(offset,pagesize,null,null,null);
        }catch (Exception e){
            e.printStackTrace();
        }

        response.put("page_size",pagesize);
        response.put("offset",offset);
        response.put("results",results);
        response.put("response_code",response_code);

        return jsonResponse(res);
    };

    public static String jsonResponse(Response res) {
        res.header("Content-Type", "application/json");

        return jsonSerializer.deep(true).serialize(response);
    }
}
