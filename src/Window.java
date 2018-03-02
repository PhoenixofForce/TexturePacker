import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Window extends JFrame{

	private List<Entity> images;
	private Random r;

	private JPanel controllPanel;
	private JButton startPack, stopPack, doubleSize, halfSize, importRes, toggleWire, export, random;
	private JTextField sizeInput;
	private CoolPane viewPanel;

	boolean packing;

	private int buttonWidth = 150, wire;

	private File lastOpened;

	public Window() {
		this.setTitle("Texture Packer");
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setVisible(true);

		wire = 0;

		images = new ArrayList<>();
		r = new Random();

		controllPanel = new JPanel();
		controllPanel.setLayout(null);
		controllPanel.setSize(new Dimension(buttonWidth, 450));
		controllPanel.setPreferredSize((new Dimension(buttonWidth, 450)));
		controllPanel.setMaximumSize((new Dimension(buttonWidth, 450)));

		startPack = new JButton("Start Packing");
		controllPanel.add(startPack);
		startPack.setBounds(0, 0, buttonWidth, 50);
		startPack.addActionListener(e -> {
			sizeInput.setEnabled(false);
			importRes.setEnabled(false);
			packing = true;
			new Thread(()->{
				Steering s = new Steering(images);
				boolean change = true;
				while (packing && change) {
					change = false;

					for(Entity en: images) {
						Location d = s.separation(en, 1);
						d.normalize(1);
						if(d.length() != 0)change = true;
						en.setVelocity(d.x, d.y);

						if(sizeInput.getText() != "") en.move(Integer.parseInt(sizeInput.getText()), Integer.parseInt(sizeInput.getText()));
					}
				}

				packing = false;
				sizeInput.setEnabled(true);
				importRes.setEnabled(true);
			}).start();
		});

		stopPack = new JButton("Stop Packing");
		controllPanel.add(stopPack);
		stopPack.setBounds(0, 50, buttonWidth, 50);
		stopPack.addActionListener(e -> {
			packing = false;
			sizeInput.setEnabled(true);
			importRes.setEnabled(true);
		});

		doubleSize = new JButton("Double Size");
		controllPanel.add(doubleSize);
		doubleSize.setBounds(0, 100, buttonWidth, 50);
		doubleSize.addActionListener(e -> {
				packing = false;
				sizeInput.setText(Math.max(Integer.parseInt(sizeInput.getText())*2, 1)+"");
				sizeInput.setEnabled(true);
				importRes.setEnabled(true);
				random();
		});

		sizeInput = new JTextField("256");
		controllPanel.add(sizeInput);
		sizeInput.setBounds(0, 150, buttonWidth, 50);

		halfSize = new JButton("Half Size");
		controllPanel.add(halfSize);
		halfSize.setBounds(0, 200, buttonWidth, 50);
		halfSize.addActionListener(e -> {
			packing = false;
			sizeInput.setText(Math.max(Integer.parseInt(sizeInput.getText())/2, 1)+"");
			sizeInput.setEnabled(true);
			importRes.setEnabled(true);
			random();
		});

		random = new JButton("Random");
		controllPanel.add(random);
		random.setBounds(0, 250, buttonWidth, 50);
		random.addActionListener(e -> random());

		toggleWire = new JButton("Toggle Wire Mode");
		controllPanel.add(toggleWire);
		toggleWire.setBounds(0, 300, buttonWidth, 50);
		toggleWire.addActionListener(e -> wire = (wire+1)%3);

		importRes = new JButton("Import");
		controllPanel.add(importRes);
		importRes.setBounds(0, 350, buttonWidth, 50);
		importRes.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();

			//chooser.setOpaque(true);
			chooser.setMultiSelectionEnabled(true);

			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".text", "text"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".png", "png"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".gif", "gif"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".jpg", "jpg"));
			chooser.addChoosableFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().endsWith(".text") || f.getName().endsWith(".png") || f.getName().endsWith(".gif") || f.getName().endsWith(".jpg");
				}

				@Override
				public String getDescription() {
					return "Images";
				}
			});


			int returnVal = chooser.showDialog(new JFrame(), "Load Texture");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				for(File text: chooser.getSelectedFiles()) {
					if(text.getName().endsWith("text")) {
						lastOpened = text;
						File image = new File(text.getAbsolutePath().substring(0, text.getAbsolutePath().length() - 4) + "png");

						if (text.exists() && image.exists()) {
							TextureHandler.loadImagePngSpriteSheet(image.getName().substring(0, image.getName().length() - 5), text.getAbsolutePath());
						} else {
							JOptionPane.showMessageDialog(new JFrame(), "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
						}
					} else if(text.getName().endsWith(".png")) {
						TextureHandler.loadImagePng(text.getName().split("\\.")[0], text.getAbsolutePath());
					}
				}
			}

			for(String s: TextureHandler.getAllImageNames()) {
				boolean contains = false;
				for(Entity en: images) {
					if(en.getName().equals(s)) {
						contains = true;
					}
				}

				if(!contains) {
					images.add(new Entity(s, r.nextInt(Integer.parseInt(sizeInput.getText())), r.nextInt(Integer.parseInt(sizeInput.getText())), TextureHandler.getImagePng(s).getWidth()+2, TextureHandler.getImagePng(s).getHeight()+2));
				}
			}

		});

		export = new JButton("Export");
		controllPanel.add(export);
		export.setBounds(0, 400, buttonWidth, 50);
		export.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser(){
				public void approveSelection() {
					File f = getSelectedFile();
					if(!f.getName().endsWith(".text")) setSelectedFile( new File(f.getAbsolutePath() + ".text"));
					f = getSelectedFile();

					if(f.exists()) {
						int n = JOptionPane.showOptionDialog(this, "The file already exists, should it be replaced?", "File exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "No");
						if(n == 0) super.approveSelection();
					} else super.approveSelection();
				}
			};

			chooser.setOpaque(true);

			if(lastOpened != null) chooser.setCurrentDirectory(lastOpened);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory();
				}

				@Override
				public String getDescription() {
					return ".text files";
				}
			});

			int returnVal = chooser.showDialog(new JButton(""), "Save File");
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File f = chooser.getSelectedFile();
				File img = new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().length()-4) + "png");

				try {
					PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"), true);
					w.write(images.size() + "\n");
					for(Entity en: images) {
						w.write(en.getName().substring("texture_".length()) + " " + ((int)en.getPosition().x+1) + " " + ((int)en.getPosition().y+1) + " " + ((int)en.getWidth()-2) + " " + ((int)en.getHeight()-2) + "\n");
					}
					w.close();

					BufferedImage image = new BufferedImage(Integer.parseInt(sizeInput.getText()), Integer.parseInt(sizeInput.getText()), BufferedImage.TYPE_INT_ARGB);
					Graphics g = image.getGraphics();
					for(Entity en: images) {
						BufferedImage i = TextureHandler.getImagePng(en.getName());

						int x = (int)en.getPosition().x+1;
						int y = (int)en.getPosition().y+1;
						int width = (int)en.getWidth()-2;
						int height = (int) en.getHeight()-2;

						//g.drawImage(i, x-1, y+height+1, x-1+1, y+height+1+1, 0, i.getHeight(), 0+1, i.getHeight()+1, null);

						g.drawImage(i, x-1, y-1, x-1+1, y-1+1, 0, 0, 0+1, 0+1, null);
						g.drawImage(i, x-1, y+height, x-1+1, y+height+1, 0, i.getHeight()-1, 0+1, i.getHeight(), null);
						g.drawImage(i, x+width, y-1, x+width+1, y-1+1, i.getWidth()-1, 0, i.getWidth(), 0+1, null);
						g.drawImage(i, x+width, y+height, x+width+1, y+height+1, i.getWidth()-1, i.getHeight()-1, i.getWidth(), i.getHeight(), null);


						g.drawImage(i, x-1, y, x-1+1, y+height-1+1, 0, 0, 0+1, i.getHeight()-1+1, null);
						g.drawImage(i, x, y-1, x+width-1+1, y-1+1, 0, 0, i.getWidth()-1+1, 0+1, null);
						g.drawImage(i, x+width, y, x+width+1, y+height-1+1, i.getWidth()-1, 0, i.getWidth(), i.getHeight()-1+1, null);
						g.drawImage(i, x, y+height, x+width-1+1, y+height+1, 0, i.getHeight()-1, i.getWidth()-1+1, i.getHeight(), null);

						g.drawImage(i, x, y, null);
					}
					ImageIO.write(image, "PNG", img);

				} catch (Exception e1) {
					e1.printStackTrace();
				}


			}
		});

		this.add(controllPanel, BorderLayout.LINE_END);

		viewPanel = new CoolPane(sizeInput);
		this.add(viewPanel, BorderLayout.CENTER);
		viewPanel.setBackground(Color.WHITE);
		viewPanel.setFocusable(true);

		this.pack();

		draw();
	}

	public void random() {
		for(Entity en: images) en.setPosition( r.nextInt(Integer.parseInt(sizeInput.getText())), r.nextInt(Integer.parseInt(sizeInput.getText())));
	}

	public void draw() {
		new Thread(()->{
			long lastDrawing;
			while (true) {
				String size = sizeInput.getText();
				if(size == "") return;

				lastDrawing = System.currentTimeMillis();

				try {
					viewPanel.paintComponent(images, Integer.parseInt(size), wire);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(Math.max(1000/60 - (System.currentTimeMillis() - lastDrawing), 0));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}