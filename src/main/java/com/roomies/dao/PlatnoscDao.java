package com.roomies.dao;

import com.roomies.model.Platnosc;
import com.roomies.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

public class PlatnoscDao implements GenericDao<Platnosc, Integer>{

    @Override
    public void save(Platnosc platnosc){
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(platnosc);

            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Błą podczas zapisywania Platnosci: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Platnosc platnosc){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.merge(platnosc);

            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Błąd podczas aktualizacji Platnosci: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Platnosc platnosc){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.remove(platnosc);

            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Błąd podczas aktualizacji Platnosci: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Platnosc> findById(Integer id){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Platnosc platnosc = session.get(Platnosc.class, id);
            return Optional.ofNullable(platnosc);
        } catch (Exception e) {
            System.out.println("Błąd podczas wyszukiwania platnosci po ID: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Platnosc> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Platnosc> query = session.createQuery("FROM Platnosc", Platnosc.class);
            return query.list();
        } catch (Exception e) {
            System.out.println("Błąd podczas wyszukiwania wszystkich platnosci: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }


    }
}
