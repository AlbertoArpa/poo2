import java.util.ArrayList;

public class AdminsController {
    private ArrayList<Admin> admins;

    public AdminsController(){
        this.admins = new ArrayList<>();
    }

    public Admin getAdmin(String username) {
        Admin result = null;
        for (int i = 0; i<admins.size(); i++){
            if (admins.get(i).getUsername().equalsIgnoreCase(username)){
                result = admins.get(i);
            }
        }
        return result;
    }

    public boolean addAdmin(Admin admin){
        if (getAdmin(admin.getUsername())==null){
            admins.add(admin);
            return true;
        } else return false;
    }
}
