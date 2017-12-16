public class Location {
	public double x;
	public double y;

	public Location(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Location add(double xOff, double yOff) {
		x += xOff;
		y += yOff;

		return this;
	}

	public void divide(double xOff, double yOff) {
		x /= xOff;
		y /= yOff;
	}

	public void multi(double xOff, double yOff) {
		x *= xOff;
		y *= yOff;
	}

	public double length() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	public void normalize(int norm) {
		double l = length();
		if(l != 0) divide(l, l);
		else {
			x = 0;
			y = 0;
		}
		multi(norm, norm);
	}

	@Override
	public int hashCode() {
		double hash = 17L;
		hash = hash*31 + x;
		hash = hash*31 + y;

		Long l = new Double(hash).longValue();
		return l.intValue();
	}

	@Override
	public Location clone() {
		return new Location(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			Location b = (Location) obj;
			return x == b.x && y == b.y;
		}
		return false;
	}

	public double distanceTo(Location loc2) {
		return Math.sqrt(Math.pow(loc2.x - x, 2) + Math.pow(loc2.y - y, 2));
	}

	@Override
	public String toString() {
		return String.format("(%f | %f)", x, y);
	}
}
