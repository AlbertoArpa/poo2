package upm.etsisi.poo.view;

import java.util.Scanner;

import upm.etsisi.poo.controller.AdminsController;
import upm.etsisi.poo.controller.PlayersController;
import upm.etsisi.poo.controller.TeamsController;
import upm.etsisi.poo.controller.TournamentsController;
import upm.etsisi.poo.model.Admin;
import upm.etsisi.poo.model.Authentication;
import upm.etsisi.poo.model.ModelException;
import upm.etsisi.poo.model.Team;

public class I_O {
    private static final Scanner sc = new Scanner(System.in);

    public static void initialitation(){
        AdminsController.getInstance();
        TournamentsController.getInstance();
        TeamsController.getInstance();
        PlayersController.getInstance();
    }
    public static void initialUsers() throws ModelException {
        AdminsController.addAdmin(new Admin("a.arpa@alumnos.upm.es", "4321"));
        AdminsController.addAdmin(new Admin("javier@alumnos.upm.es", "1234"));
        AdminsController.addAdmin(new Admin("adrian@alumnos.upm.es", "1432"));
    }

    public static void start(){
        try{
            initialitation();
            initialUsers();
            System.out.println("SISTEMA DE GESTION DEPORTIVA INICIADO:");
            boolean end = false;
            while (!end) {
                end = menu();
            }
            sc.close();
        } catch (Exception modelException){
            System.out.println(modelException.getMessage());
            System.out.println("No se han podido iniciar los administradores iniciales");
        }
    }

    public static boolean menu(){
        Authentication authentication = Authentication.getInstance();
        System.out.print("Comandos:\n> login [username;password]\n> tournament-list\n----------\n\t> ");
        String[] arguments = sc.nextLine().toLowerCase().split(" ", 2);
        String option = arguments[0];
        if (!option.equals("tournament-list")) {
            if (arguments.length>1) arguments = arguments[1].split(";");
        }
        switch (option.toLowerCase()) {
            case "login":
                if (reviewArguments(arguments, 2)) {
                    if (authentication.logIn(arguments[0], arguments[1])) {
                        System.out.println("\nBIENVENIDO, " + authentication.getCurrentUser().getUsername());
                        if (authentication.getUserType().equals("ADMIN")) {
                            menuAdmin(authentication);
                        } else menuPlayer(authentication);
                    } else System.out.println("\nNO SE HA PODIDO INICIAR SESION");
                }
                break;
            case "tournament-list":
                System.out.println(TournamentsController.listTournaments(authentication.getUserType()));
                break;
            default:
                System.out.println("No existe esa opción. Se procederá a finalizar la ejecución.");
                return true;
        }
        return false;
    }

    public static void menuAdmin(Authentication authentication) {
        while (authentication.isLoggedIn()) {
            System.out.print("Comandos:\n> player-create [username;password;name;surname;dni]\n" +
                    "> team-create [name]\n" +
                    "> player-delete [username]\n" +
                    "> team-delete [name]\n" +
                    "> add-points [player username;category;points]\n" +
                    "> team-add [player username;team]\n" +
                    "> team-remove [player username;team]\n" +
                    "> tournament-create [name;startDate;endDate;league;sport;categoryRank]\n" +
                    "> tournament-delete [tournament name]\n" +
                    "> tournament-matchmaking [-m/-a;tournament name(;team1;team2)]\n" +
                    "> tournament-list\n" + "> logout\n----------\n\t> ");
            String[] arguments = sc.nextLine().toLowerCase().split(" ", 2);
            String option = arguments[0];
            if (!option.equals("logout") && !option.equals("tournament-list")) {
                if (arguments.length>1)
                    arguments = arguments[1].split(";");
            }
            switch (option.toLowerCase()) {
                case "player-create":
                    if (reviewArguments(arguments, 5)) {
                        try{
                            if (PlayersController.createPlayer(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], (Admin) Authentication.getCurrentUser())) {
                                System.out.println("\nJUGADOR " + arguments[0].toUpperCase() + " CREADO");
                            } else System.out.println("\nYA EXISTE UN USUARIO O UN EQUIPO CON EL NOMBRE: " + arguments[0].toUpperCase());
                        } catch (ModelException modelException){
                            System.out.println(modelException.getMessage());
                        }
                    }
                    break;
                case "team-create":
                    if (reviewArguments(arguments, 1)) {
                        try{
                            if (TeamsController.createTeam(arguments[0], (Admin) Authentication.getCurrentUser())) {
                                System.out.println("\nEQUIPO " + arguments[0].toUpperCase() + " CREADO");
                            } else System.out.println("\nEL EQUIPO O EL JUGADOR " + arguments[0].toUpperCase() + " YA EXISTE");
                        } catch (ModelException modelException){
                            System.out.println(modelException.getMessage());
                        }
                    }
                    break;
                case "player-delete":
                    if (reviewArguments(arguments, 1)) {
                        if (TournamentsController.playerDelete(arguments[0])){
                            System.out.println("\nJUGADOR BORRADO CORRECTAMENTE");
                        } else System.out.println("\nNO EXISTE EL JUGADOR " + arguments[0].toUpperCase() + " O ESTÁ EN ACTIVO.");
                    }
                    break;
                case "team-delete":
                    if (reviewArguments(arguments, 1)) {
                        if (TournamentsController.teamDelete(arguments[0])){
                            System.out.println("\nEQUIPO " + arguments[0].toUpperCase() + " ELIMINADO");
                        } else System.out.println("\nNO EXISTE EL EQUIPO " + arguments[0].toUpperCase() + " O ESTÁ ACTIVO");
                    }
                    break;
                case "add-points":
                    if (reviewArguments(arguments, 3)){
                        try{
                            double point = Double.parseDouble(arguments[2]);
                            if (PlayersController.addPoints(arguments[0], arguments[1], point)){
                                System.out.println("Puntos añadidos.");
                            } else System.out.println("No se ha podido añadir la puntuación.");
                        } catch (Exception e){
                            System.out.println("Los puntos no están en un formato correcto");
                        }
                    }
                    break;
                case "team-add":
                    if (reviewArguments(arguments, 2)) {
                        if (TournamentsController.teamAdd(arguments[0], arguments[1])) {
                            System.out.println("\nJUGADOR " + arguments[0].toUpperCase() + " ANADIDO AL EQUIPO " + arguments[1].toUpperCase());
                        } else System.out.println("\nEL EQUIPO O EL JUGADOR " + arguments[0].toUpperCase() + " NO EXISTE O YA SE ENCUENTRA EN UN EQUIPO.");
                    }
                    break;
                case "team-remove":
                    if (reviewArguments(arguments, 2)) {
                        if (TournamentsController.teamRemove(arguments[0], arguments[1])) {
                            System.out.println("\nJUGADOR " + arguments[0].toUpperCase() + " BORRADO DEL EQUIPO " + arguments[1].toUpperCase());
                        } else System.out.println("\nNO EXISTE EL EQUIPO " + arguments[1].toUpperCase() + " O NO EXISTE EL JUGADOR " + arguments[0].toUpperCase() +  " DENTRO DEL EQUIPO");
                    }
                    break;
                case "tournament-create":
                    if (reviewArguments(arguments, 6)) {
                        try{
                            if (TournamentsController.createTournament(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5])) {
                                System.out.println("\nTORNEO " + arguments[0].toUpperCase() + " CREADO");
                            } else System.out.println("\nNO SE HA PODIDO CREAR EL TORNEO. ASEGURATE DE QUE LA FECHA SEA CORRECTA Y LA CATEGORIA EXISTA.");
                        } catch (ModelException modelException){
                            System.out.println(modelException.getMessage());
                        }
                    }
                    break;
                case "tournament-delete":
                    if (reviewArguments(arguments, 1)) {
                        if (TournamentsController.deleteTournament(arguments[0])) {
                            System.out.println("\nTORNEO " + arguments[0].toUpperCase() + " BORRADO");
                        } else System.out.println("\nNO EXISTE EL TORNEO " + arguments[0].toUpperCase());
                    }
                    break;
                case "tournament-matchmaking":
                    if (reviewArguments(arguments, 2)) {
                        String matchmaking = arguments[0];
                        switch (matchmaking) {
                            case "-m":
                                if (reviewArguments(arguments, 4)) {
                                    int i = 0;
                                    while (i < 3) {
                                        arguments[i] = arguments[i + 1];
                                        i++;
                                    }
                                    if (TournamentsController.tournamentMatchmakingM(arguments[0], arguments[1], arguments[2])) {
                                        System.out.println("\nPARTICIPANTES " + arguments[1].toUpperCase() + " Y " + arguments[2].toUpperCase() + " EMPAREJADOS");
                                    } else System.out.println("\nNO EXISTE EL TORNEO O ALGUNO DE LOS PARTICIPANTES");
                                }
                                break;
                            case "-a":
                                if (reviewArguments(arguments, 2)) {
                                    arguments[0] = arguments[1];
                                    if (TournamentsController.tournamentMatchmakingA(arguments[0])) {
                                        System.out.println("PARTICIPANTES DE " + arguments[0].toUpperCase() + " EMPAREJADOS");
                                    } else System.out.println("\nNO EXISTE EL TORNEO O EL NUMERO DE PARTICIPANTES NO ES OPTIMO");
                                }
                        }
                    }

                    
                    break;
                case "tournament-list":
                    System.out.println(TournamentsController.listTournaments(authentication.getUserType()));
                    break;
                case "logout":
                    authentication.logOut();
                    System.out.println("\nCERRANDO SESIÓN...");
                    break;
                default:
                    System.out.println("\nNO EXISTE LA OPCION " + option.toUpperCase());
                    break;
            }
        }
    }

    public static void menuPlayer(Authentication authentication){
        while (authentication.isLoggedIn()) {
            System.out.print("Comandos:\n> tournament-add [tournament name(;team)]\n" +
                    "> tournament-remove [tournament name(;team)]\n" +
                    "> statistics-show [-csv/-json]\n" +
                    "> tournament-list\n" + "> logout\n----------\n\t> ");
            String[] arguments = sc.nextLine().toLowerCase().split(" ", 2);
            String option = arguments[0];
            if (!option.equals("logout") && !option.equals("tournament-list")) {
                if (arguments.length>1)
                    arguments = arguments[1].split(";");
            }
            switch (option.toLowerCase()) {
                case "tournament-add":
                    if (reviewArguments(arguments, 1)) {
                        if (arguments.length>1) {
                            if (TournamentsController.tournamentAdd(arguments[0], arguments[1])){
                                System.out.println("\nEQUIPO " + arguments[1].toUpperCase() + " ANADIDO A " + arguments[0].toUpperCase());
                            } else System.out.println("\nNO SE HA PODIDO ANADIR EL EQUIPO. ASEGURATE DE QUE FORMAS PARTE DE EL O DE QUE EXISTE Y NO ESTA YA AÑADIDO");
                        } else {
                            if (TournamentsController.tournamentAdd(arguments[0], null)){
                                System.out.println("\nJUGADOR AÑADIDO AL TORNEO");
                            } else System.out.println("\nNO SE HA PODIDO AÑADIR AL JUGADOR");
                        }
                    }
                    break;
                case "tournament-remove":
                    if (reviewArguments(arguments, 1)) {
                        if (arguments.length>1) {
                            if (TournamentsController.tournamentRemove(arguments[0], arguments[1])){
                                System.out.println("\nEQUIPO BORRADO DEL TORNEO");
                            } else System.out.println("\nNO SE HA PODIDO BORRAR EL EQUIPO.");
                        } else {
                            if (TournamentsController.tournamentRemove(arguments[0], null)){
                                System.out.println("\nJUGADOR BORRADO DEL TORNEO");
                            } else System.out.println("\nNO SE HA PODIDO BORRAR AL JUGADOR");
                        }
                    }
                    break;
                case "statistics-show":
                    if (reviewArguments(arguments, 1)) {
                        if(!PlayersController.statisticsShow(arguments[0])) System.out.println("\nARGUMENTO INVALIDO");
                    }
                    break;
                case "tournament-list":
                    System.out.println(TournamentsController.listTournaments(authentication.getUserType()));
                    break;
                case "logout":
                    authentication.logOut();
                    System.out.println("\nCERRANDO SESIÓN...\n");
                    break;
                default:
                    System.out.println("NO EXISTE LA OPCION " + option.toUpperCase());
                    break;
            }
        }
    }

    private static boolean reviewArguments(String[] arguments, int number){
        if (arguments.length>=number){
            return true;
        } else {
            System.out.println("\nMUY POCOS ARGUMENTOS");
            return false;
        }
    }
}
