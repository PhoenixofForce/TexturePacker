import com.sun.javafx.geom.Vec2d;

public class Entity {

	private String name;

	private Location pos;
	private Vec2d velo;
	private double width, height;

	public Entity(String name, double x, double y, double w, double h) {
		this.name = name;
		velo = new Vec2d(0, 0);
		pos = new Location(x, y);
		this.width = w;
		this.height = h;
	}

	public void setPosition(int x, int y) {
		this.pos = new Location(x, y);
	}

	@Override
	public boolean equals(Object n) {
		if(n instanceof Entity) {
			Entity e = (Entity) n;
			return pos.equals(e.pos) && velo.equals(e.velo) && width == e.width && height == e.height;
		}
		return false;
	}

	public void move(int xb, int yb) {
		if(pos.x < 0) pos.x = 0;
		if(pos.x + width > xb) pos.x = xb - width;

		if(pos.y < 0) pos.y = 0;
		if(pos.y + height > yb) pos.y = yb - height;
		pos.add(velo.x, velo.y);
	}

	public void setVelocity(double x, double y) {
		velo = new Vec2d(x, y);
	}

	public Vec2d getVelocity() {
		return velo;
	}

	public Location getPosition() {
		return pos;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public String getName() {
		return name;
	}
}