package upm.etsisi.poo.controller;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import upm.etsisi.poo.model.*;
import upm.etsisi.poo.view.PublicView;

import java.util.List;

public class DataController {
    private static Session session;
    public static boolean initialitation(){
        Authentication.getInstance();
        AdminsController.getInstance();
        TournamentsController.getInstance();
        TeamsController.getInstance();
        PlayersController.getInstance();
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            session = sessionFactory.openSession();
            return true;
        } catch (Exception es){
            return false;
        }
    }

    public static boolean getData(){
        try{
            List<Admin> admins = session.createQuery("FROM Admin a", Admin.class).getResultList();
            for (Admin admin : admins){
                AdminsController.addAdmin(admin);
            }
            List<Player> players = session.createQuery("SELECT DISTINCT p FROM Player p JOIN FETCH p.stats", Player.class).getResultList();
            for (Player player : players){
                PlayersController.addPlayer(player);
            }
            return true;
        } catch (Exception es){
            System.out.println(es);
            return false;
        }
    }

    public static void saveData(){
        try {
            session.beginTransaction();
            for (Player player : PlayersController.getPlayers()){
                for (Stat stat : player.getStats()){
                    stat.setPlayer(player);
                    session.saveOrUpdate(stat);
                }
                session.saveOrUpdate(player);
            }
            session.getTransaction().commit();
            session.close();
            PublicView.saveData(true);
        } catch (Exception es){
            System.out.println(es.getMessage());
            PublicView.saveData(false);
        }
    }
}