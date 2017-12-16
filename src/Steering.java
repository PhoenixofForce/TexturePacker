import java.util.List;

public class Steering {

	private List<Entity> entities;
	public Steering(List<Entity> entities) {
		this.entities = entities;
	}

	public Location steer(Entity e) {

		Location a = align(e, 10);
		Location s = separation(e, 2);
		Location c = cohesion(e, 10);

		Location l = new Location(s.x + c.x + a.x, s.y + c.y + a.y);
		l.normalize(1);
		return l;
	}

	public Location align(Entity e, double distance) {
		int neigth = 0;
		Location v = new Location(0, 0);

		for(Entity en: entities) {
			if(!e.equals(en)) {
				if(insight(e, en, distance)) {
					v.add(en.getVelocity().x, en.getVelocity().y);
					neigth++;
				}
			}
		}

		if(neigth == 0) return v;

		v.divide(neigth, neigth);
		v.normalize(1);

		return v;
	}

	public Location cohesion(Entity e, double distance) {
		int neigth = 0;
		Location v = new Location(0, 0);

		for(Entity en: entities) {
			if(!e.equals(en)) {
				if(insight(e, en, distance)) {
					v.add(en.getPosition().x, en.getPosition().y);
					neigth++;
				}
			}
		}

		if(neigth == 0) return v;

		v.divide(neigth, neigth);
		Location a = new Location(v.x - e.getPosition().x, v.y - e.getPosition().y);
		a.normalize(1);

		return a;
	}

	public Location separation(Entity e, double distance) {
		int neigth = 0;
		Location v = new Location(0, 0);

		for(Entity en: entities) {
			if(!e.equals(en)) {
				if(insight(e, en, distance)) {
					v.add(en.getPosition().x - e.getPosition().x, en.getPosition().y - e.getPosition().y);
					neigth++;
				}
			}
		}

		if(neigth == 0) return v;

		v.divide(neigth, neigth);
		v.multi(-1, -1);
		v.normalize(1);

		return v;
	}

	private boolean inside(double x1, double y1, double width1, double height1, double x2, double y2, double width2, double height2) {
		return (x2 + width2 > x1 && y2 + height2 > y1 && x1 + width1 > x2 && y1 + height1 > y2);
	}

	private boolean insight(Entity e1, Entity e2, double distance) {
		Location l = e1.getPosition();
		return insight_1(l, e2, distance) || insight_1(l.clone().add(e1.getWidth(), 0), e2, distance) || insight_1(l.clone().add(e1.getWidth(), e1.getHeight()), e2, distance) || insight_1(l.clone().add(0, e1.getHeight()), e2, distance) || inside(l.x, l.y, e1.getWidth(), e1.getHeight(), e2.getPosition().x, e2.getPosition().y, e2.getWidth(), e2.getHeight());
	}

	private boolean insight_1(Location l1, Entity e, double distance) {
		Location l2 = e.getPosition();

		return l2.distanceTo(l1) < distance || l2.clone().add(0, e.getHeight()).distanceTo(l1) < distance  || l2.clone().add(e.getWidth(), e.getHeight()).distanceTo(l1) < distance  || l2.clone().add(e.getWidth(), 0).distanceTo(l1) < distance;
	}
}