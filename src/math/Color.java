package math;

public class Color {
    public float r;
    public float g;
    public float b;

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void add(Color c) {
        this.r += c.r;
        this.g += c.g;
        this.b += c.b;
    }

    public void subtract(Color c) {
        this.r -= c.r;
        this.g -= c.g;
        this.b -= c.b;
    }

    public void divide(int s) {
        this.r /= s;
        this.g /= s;
        this.b /= s;
    }

    public void multiply(double s) {
        this.r *= s;
        this.g *= s;
        this.b *= s;
    }

    public void multiply(Color s) {
        this.r *= s.r;
        this.g *= s.g;
        this.b *= s.b;
    }

    public int toRender() {
        return (int) (r*255) << 16 | (int) (g*255) << 8 | (int) (b*255);
    }

}
