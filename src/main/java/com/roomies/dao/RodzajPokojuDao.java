package com.roomies.dao;

import com.roomies.model.RodzajPokoju;
import com.roomies.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

public class RodzajPokojuDao implements GenericDao<RodzajPokoju, Integer> {

    @Override
    public void save(RodzajPokoju rodzajPokoju){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();

            session.persist(rodzajPokoju);

            transaction.commit();
        }catch(Exception e){
            if (transaction != null){
                transaction.rollback();
            }
            System.out.println("Błąd podczas zapisywania rodzaju pokoju: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(RodzajPokoju rodzajPokoju){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();

            session.merge(rodzajPokoju);

            transaction.commit();
        }catch (Exception e){
            if (transaction != null){
                transaction.rollback();
            }
            System.out.println("Błąd podczas aktualizacji rodzaju pokoju: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(RodzajPokoju rodzajPokoju){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();

            session.remove(rodzajPokoju);

            transaction.commit();
        }catch (Exception e){
            if (transaction != null){
                transaction.rollback();
            }
            System.out.println("Błąd podczas usuwania rodzaju pokoju: " + e.getMessage());
            e.printStackTrace();}
    }

    @Override
    public Optional<RodzajPokoju> findById(Integer id){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            RodzajPokoju rodzajPokoju = session.get(RodzajPokoju.class, id);
            return Optional.ofNullable(rodzajPokoju);
        }catch(Exception e){
            System.out.println("Błąd podczas wyszukiwania rodzaju pokoju po ID: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<RodzajPokoju> findAll(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            Query<RodzajPokoju> query = session.createQuery("FROM RodzajPokoju", RodzajPokoju.class);
            return query.list();
        }catch (Exception e){
            System.out.println("Błąd podczas wyszukiwania wszystkich rodzaji pokoii: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
