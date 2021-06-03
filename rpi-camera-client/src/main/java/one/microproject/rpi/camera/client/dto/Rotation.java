package one.microproject.rpi.camera.client.dto;

public enum Rotation {

    D0(0),
    D90(90),
    D180(180),
    D270(270);

    private final Integer degree;

    Rotation(int degree) {
        this.degree = degree;
    }

    public Integer getDegree() {
        return degree;
    }

}
