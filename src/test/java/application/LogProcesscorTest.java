package application;

import org.junit.Test;
import spark.Response;

import static org.junit.Assert.*;

public class LogProcesscorTest {

    @Test
    public void jsonResponse(Response res) {
        assertNotNull(res);
    }
}