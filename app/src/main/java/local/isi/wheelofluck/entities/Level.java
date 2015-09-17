package local.isi.wheelofluck.entities;

public class Level {

    int level;
    int nbArrow;
    boolean clockwise;
    int nbObstacle;
    int speed;

    public Level(int level, boolean clockwise, int nbArrow, int nbObstacle, int speed) {
        this.clockwise = clockwise;
        this.level = level;
        this.nbArrow = nbArrow;
        this.nbObstacle = nbObstacle;
        this.speed = speed;
    }

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNbArrow() {
        return nbArrow;
    }

    public void setNbArrow(int nbArrow) {
        this.nbArrow = nbArrow;
    }

    public int getNbObstacle() {
        return nbObstacle;
    }

    public void setNbObstacle(int nbObstacle) {
        this.nbObstacle = nbObstacle;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
