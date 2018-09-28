package service;

import entity.LogEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import util.HibernateUtil;

import java.util.List;
import java.util.Map;


public class LogService {

    private static Logger log = LogManager.getLogger(LogService.class);

    public static LogEntry saveLog(LogEntry logEntryEntry){

        Transaction transaction = null;
        Session session = null;

        try {

            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(logEntryEntry);

        } catch (ConstraintViolationException e){
            transaction.rollback();
            session.close();
            throw e;
        } catch (HibernateException e) {
            transaction.rollback();
            session.close();
            throw e;
        } catch (Exception e) {
            transaction.rollback();
            session.close();
            e.printStackTrace();
            throw e;
        }finally {
            try {
                transaction.commit();
                session.close();
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
        return logEntryEntry;
    }

    public static List<LogEntry> getLogEntries(int pageNumber, int pageSize,
                                                   List<Order> orderbyCollection,
                                                   Map<String,String> orCollections,
                                                   Map<String,String> whereCollections) {
        List list = null;

        try{

            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria = session.createCriteria(LogEntry.class);

            criteria.setFirstResult((pageNumber - 1) * pageSize);
            criteria.setMaxResults(pageSize);

            if(whereCollections!=null){
                for (Map.Entry<String, String> whereCollection : whereCollections.entrySet()) {
                    criteria.add(Restrictions.eq(whereCollection.getKey(), whereCollection.getValue()));
                }
            }

            if(orCollections!=null){
                Disjunction or = Restrictions.disjunction();
                for (Map.Entry<String, String> orCollection : orCollections.entrySet()) {
                    or.add(Restrictions.eq(orCollection.getKey(),orCollection.getValue()));
                }
                criteria.add(or);
            }

            if(orderbyCollection!=null){
                for (Order orderBySet : orderbyCollection) {
                    criteria.addOrder(orderBySet);
                }
            }

            list = criteria.list();
            session.close();
        }catch (Exception e){
            e.printStackTrace();
            log.error("Error getting logEntries");
        }

        return list;
    }
}
