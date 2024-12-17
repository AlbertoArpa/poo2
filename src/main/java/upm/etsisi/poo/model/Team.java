package upm.etsisi.poo.model;

import java.util.ArrayList;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "teams")
public class Team implements Participant {
    private static final String ATTR_NAME_NAME = "name";
    @Id
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    @Column(name = "players", nullable = false)
    private ArrayList<Player> players;
    @Column(name = "stats", nullable = false)
    private ArrayList<Stat> stats;
    @Column(name = "creator", nullable = false)
    private Admin creator;

    public Team(String name, Admin creator) throws ModelException {
        Validations.isNotNull(ATTR_NAME_NAME, name);
        Validations.isMinimum(ATTR_NAME_NAME, name, 2);
        this.name = name;
        this.players = new ArrayList<>();
        this.stats = initialStats();
        this.creator = creator;
    }

    private ArrayList<Stat> initialStats() {
        ArrayList<Stat> statList = new ArrayList<>();
        for (int i = 0; i < Categories.getCategories().length; i++) {
            Stat stat = new Stat(Categories.getCategories()[i].name());
            statList.add(stat);
        }
        return statList;
    }

    @Override
    public Stat getStat(String category) {
        updateStats();
        Stat stat = null;
        for (int i = 0; i < stats.size(); i++) {
            if (stats.get(i).getCategory().equals(Categories.getCategory(category))) {
                stat = stats.get(i);
            }
        }
        return stat;
    }

    public void updateStats() {
        for (int i = 0; i < stats.size(); i++) {
            stats.get(i).setValue(0);
        }
        for (int i = 0; i < players.size(); i++) {
            for (int d = 0; d < stats.size(); d++) {
                if (stats.get(d).getValue()==0){
                    stats.get(d).setValue(players.get(i).getStats().get(d).getValue());
                } else stats.get(d).setValue(stats.get(d).getValue() * players.get(i).getStats().get(d).getValue());
            }
        }
        for (int i = 0; i < stats.size(); i++) {
            stats.get(i).setValue(Math.pow(stats.get(i).getValue(), 1.0 / players.size()));
        }
    }

    public ArrayList<Stat> getStats() {
        updateStats();
        return stats;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer(String username) {
        Player result = null;
        int i = 0;
        while (i < players.size() && result == null) {
            if (players.get(i).getUsername().equalsIgnoreCase(username)) result = players.get(i);
            i++;
        }
        return result;
    }

    public boolean addPlayer(Player player) {
        boolean result = false;
        if (player == null) {
            return false;
        }
        if (getPlayer(player.getUsername()) == null) {
            players.add(player);
            updateStats();
            result = true;
        }
        return result;
    }

    public boolean removePlayer(String username) {
        boolean result = false;
        if (getPlayer(username) != null) {
            players.remove(getPlayer(username));
            updateStats();
            result = true;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
