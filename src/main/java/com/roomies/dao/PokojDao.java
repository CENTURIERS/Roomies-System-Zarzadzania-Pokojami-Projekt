package com.roomies.dao;

import com.roomies.model.Pokoj;
import com.roomies.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

public class PokojDao implements GenericDao<Pokoj, Integer> {

    @Override
    public void save(Pokoj pokoj) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();

            session.persist(pokoj);

            transaction.commit();
        }catch (Exception e){
            if(transaction != null){
                transaction.rollback();
            }
            System.out.println("Błąd podczas zapisywania pokoju: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Pokoj pokoj) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();

            session.merge(pokoj);

            transaction.commit();
        }catch (Exception e){
            if (transaction != null){
                transaction.rollback();
            }
            System.out.println("Błąd podczas aktualizacji pokoju: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Pokoj pokoj) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();

            session.remove(pokoj);

            transaction.commit();
        }catch (Exception e){
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Błąd podczas usuwania pokoju: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Pokoj> findById(Integer id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Pokoj pokoj = session.get(Pokoj.class, id);
            return Optional.ofNullable(pokoj);
        }catch(Exception e){
            System.out.println("Bład podczas wyszukiwania pokoju po ID: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Pokoj> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            Query<Pokoj> query = session.createQuery("From Pokoj", Pokoj.class);
            return query.list();
        }catch (Exception e){
            System.out.println("Bład podczas wyszukiwania wszystkich pokoii: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
