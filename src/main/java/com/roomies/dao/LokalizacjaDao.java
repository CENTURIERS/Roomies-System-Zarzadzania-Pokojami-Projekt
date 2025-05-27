package com.roomies.dao;

import com.roomies.model.Lokalizacja;
import com.roomies.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

public class LokalizacjaDao implements GenericDao<Lokalizacja, Integer> {

    @Override
    public void save(Lokalizacja lokalizacja) {
        Transaction transaction = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(lokalizacja);

            transaction.commit();
        }catch (Exception e) {
            if (transaction != null){
                transaction.rollback();
            }
            System.out.println("Błąd podczas zapisywania Lokalizacji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Lokalizacja lokalizacja){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.merge(lokalizacja);

            transaction.commit();
        }catch (Exception e) {
            if (transaction != null){
                transaction.rollback();
            }
            System.out.println("Błąd podczas aktualizacji lokalizacji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Lokalizacja lokalizacja) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.remove(lokalizacja);

            transaction.commit();
        }catch (Exception e) {
            if (transaction != null){
                transaction.rollback();
            }
            System.out.println("Błąd podczas usuwania lokalizacji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Lokalizacja> findById(Integer id){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            Lokalizacja lokalizacja = session.get(Lokalizacja.class, id);
            return Optional.ofNullable(lokalizacja);
        }catch (Exception e){
            System.out.println("Błąd podczas wyszukiwania lokalizacji: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Lokalizacja> findAll(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            Query<Lokalizacja> query = session.createQuery("FROM Lokalizacja", Lokalizacja.class);
            return query.list();
        }catch (Exception e){
            System.out.println("Bład podczas wyszukiwania wszystkich lokalizacji: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
