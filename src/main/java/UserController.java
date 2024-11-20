public class UserController {
    private static final Authentication authentication = Authentication.getInstance();

    public static void initialUsers() {
        AdminsController adminsController = AdminsController.getInstance();
        adminsController.addAdmin(new Admin("a.arpa", "4321"));
        adminsController.addAdmin(new Admin("javier", "1234"));
        adminsController.addAdmin(new Admin("adrian", "1432"));
    }

    public static boolean logIn(String username, String password) {
        AdminsController adminsController = AdminsController.getInstance();
        PlayersController playersController = PlayersController.getInstance();
        return authentication.logIn(adminsController, playersController, username, password);
    }

    public static boolean playerCreate(String username, String password, String name, String surname, String dni) {
        AdminsController adminsController = AdminsController.getInstance();
        PlayersController playersController = PlayersController.getInstance();
        TeamsController teamsController = TeamsController.getInstance();
        if (adminsController.getAdmin(username) == null && playersController.getPlayer(username) == null && teamsController.getTeam(username) == null) {
            return playersController.createPlayer(username, password, name, surname, dni, (Admin) authentication.getCurrentUser());
        } else return false;
    }

    public static boolean statisticsShow(String option) {
        if (option.equals("-csv")) {
            ((Player) authentication.getCurrentUser()).showStatsCsv();
            return true;
        } else if (option.equals("-json")) {
            ((Player) authentication.getCurrentUser()).showStatsJson();
            return true;
        } else return false;
    }
    public static boolean addPoints(String username, String stat, double points){
        PlayersController playersController = PlayersController.getInstance();
        return playersController.addPoints(username, stat, points);
    }

}