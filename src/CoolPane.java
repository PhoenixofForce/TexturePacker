import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class CoolPane extends JPanel {


	private float dx, dy, zoom;

	private int last_x, last_y;

	private JTextField in;

	public CoolPane(JTextField in) {

		this.in = in;

		centerCamera();

		this.addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				zoom *= Math.pow(1.2, -e.getPreciseWheelRotation());
			}
		});

		this.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				last_x = e.getX();
				last_y = e.getY();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isMiddleMouseButton(e)) {
					dx += (e.getX() - last_x) / zoom;
					dy += (e.getY() - last_y) / zoom;
				}

				last_x = e.getX();
				last_y = e.getY();
			}
		});

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() != 2) centerCamera();
			}
		});
	}

	private void centerCamera() {
		String size = in.getText();
		if(!size.equals(""))
		this.dx = -Integer.parseInt(size) / 2;
		this.dy = -Integer.parseInt(size) / 2;
		this.zoom = 0.5f;
	}

	public void paintComponent(List<Entity> images, int size, int wire, String filter) {
		Graphics g = this.getGraphics();
		BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();

		g2.setColor(new Color(240, 248, 255));
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());

		g2.translate(this.getWidth() / 2.0, this.getHeight() / 2.0);
		g2.scale(zoom, zoom);
		g2.translate(dx, dy);

		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, (size), (size));

		g2.setStroke(new BasicStroke(0.1f));
		g2.setColor(Color.RED);
		for(int i = 0; i < images.size(); i++) {
			Entity e = images.get(i);
			if(!e.getName().contains(filter)) continue;

			if(wire==0 || wire==1) g2.drawRect((int)e.getPosition().x, (int)e.getPosition().y, (int)e.getWidth(), (int)e.getHeight());
			if(wire == 1||wire ==2) g2.drawImage(TextureHandler.getImagePng(e.getName()), (int)e.getPosition().x+1, (int)e.getPosition().y+1, null);
		}

		g.drawImage(img, 0, 0, null);
	}
}