package com.roomies.dao;

import com.roomies.model.Wynajem;
import com.roomies.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class WynajemDao implements GenericDao<Wynajem, Integer>{

    @Override
    public void save(Wynajem wynajem) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(wynajem);
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Błąd podczas zapisywania wynajmu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Wynajem wynajem) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(wynajem);
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Błąd podczas aktualizacji wynajmu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Wynajem wynajem) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(wynajem);
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Błąd podczas usuwania wynajmu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Wynajem> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Wynajem wynajem = session.get(Wynajem.class, id);
            return Optional.ofNullable(wynajem);
        }catch (Exception e) {
            System.err.println("Błąd podczas wyszukiwania wynajmu po ID: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Wynajem> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Wynajem> query = session.createQuery("FROM Wynajem", Wynajem.class);
            return query.list();
        }catch (Exception e) {
            System.err.println("Błąd podczas wyszukiwania wszystkich wynajmów: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Wynajem> findByKlientId(Integer klientId) {
        if (klientId == null) {
            return Collections.emptyList();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT w FROM Wynajem w " +
                    "LEFT JOIN FETCH w.pokoj p " +
                    "LEFT JOIN FETCH p.rodzajPokoju " +
                    "LEFT JOIN FETCH w.platnosc " +
                    "WHERE w.klient.idKlienta = :klientIdParam " +
                    "ORDER BY w.dataRozpoczecia DESC";
            Query<Wynajem> query = session.createQuery(hql, Wynajem.class);
            query.setParameter("klientIdParam", klientId);
            return query.list();
        } catch (Exception e) {
            System.err.println("Błąd podczas wyszukiwania wynajmów dla klienta ID " + klientId + ": " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}