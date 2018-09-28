package service;

import entity.LogEntry;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.junit.Test;
import util.HibernateUtil;

import java.util.List;

import static org.junit.Assert.*;

public class LogServiceTest {

    @Test
    public void saveLog() {
    }

    @Test
    public void getLogEntries() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria = session.createCriteria(LogEntry.class);


        List<LogEntry> logEntries = criteria.list();

        assertNotNull(logEntries);
        assertEquals(39, logEntries.size());
    }


}