package local.isi.wheelofluck.entities;

public class Level {

    int level;
    int nbArrow;
    boolean clockwise;
    int nbObstacle;
    float speed1Modifier;
    float speed2Modifier;
    int speedInterval;

    public Level(int level, boolean clockwise, int nbArrow, int nbObstacle, float speed1Modifier, float speed2Modifier, int speedInterval) {
        this.clockwise = clockwise;
        this.level = level;
        this.nbArrow = nbArrow;
        this.nbObstacle = nbObstacle;
        this.speed1Modifier = speed1Modifier;
        this.speed2Modifier = speed2Modifier;
        this.speedInterval = speedInterval;
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

    public float getSpeed1Modifier() {
        return speed1Modifier;
    }

    public void setSpeed1Modifier(float speed1Modifier) {
        this.speed1Modifier = speed1Modifier;
    }

    public float getSpeed2Modifier() {
        return speed2Modifier;
    }

    public void setSpeed2Modifier(float speed2Modifier) {
        this.speed2Modifier = speed2Modifier;
    }

    public int getSpeedInterval() {
        return speedInterval;
    }

    public void setSpeedInterval(int speedInterval) {
        this.speedInterval = speedInterval;
    }
}
