package com.roomies.dao;

import com.roomies.model.Klient;
import com.roomies.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

public class KlientDao implements  GenericDao<Klient, Integer> {

    @Override
    public void save(Klient klient) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(klient);

            transaction.commit();
        }catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            System.out.println("Błąd podczas zapisywania Klienta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Klient klient) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.merge(klient);

            transaction.commit();
        }catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            System.out.println("Błąd poczas aktualizajci Klienta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Klient klient) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.remove(klient);

            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Błąd podczas usuwania Klienta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Klient> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Klient klient = session.get(Klient.class, id);
            return Optional.ofNullable(klient);
        }catch (Exception e) {
            System.out.println("Błąd podczas wyszukiwania klienta po ID: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Klient> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Klient> query = session.createQuery("FROM Klient", Klient.class);
            return query.list();
        }catch (Exception e) {
            System.out.println("Błąd podczas wyszukwania wszystkich klientów: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
