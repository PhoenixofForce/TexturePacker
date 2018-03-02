import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TextureHandler {
	private TextureHandler() {}

	public static Map<String, BufferedImage> textures_png;
	public static Map<String, Rectangle> textures_sprite_sheet;
	public static Map<String, String> textures_sprite_sheet_texture;
	public static Map<String, Boolean> isSheet;

	static {
		textures_png = new HashMap<>();
		textures_sprite_sheet = new HashMap<>();
		textures_sprite_sheet_texture = new HashMap<>();
		isSheet = new HashMap<>();
	}

	/**Loads an image
	 *
	 * @param textureName name with which the image can be called
	 * @param fileName name of the file in which the image is located in
	 */
	public static void loadImagePng(String textureName, String fileName) {
		try {
			textures_png.put(textureName, ImageIO.read(new File(fileName)));
		} catch (IOException e) {
			System.err.println("Error loading texture: " + textureName);
			System.exit(-1);
		}
	}

	/**Loads an spritesheet from a .text-file
	 * A .png with the same name has to be in the same folder
	 *
	 * @param spriteSheetName
	 * @param fileName file that links to the .text file
	 */
	public static void loadImagePngSpriteSheet(String spriteSheetName, String fileName) {
		if(textures_png.containsKey(spriteSheetName)) return;

		try {
			isSheet.put(spriteSheetName, true);
			loadImagePng(spriteSheetName, fileName.substring(0, fileName.length()-4)+"png");
			Scanner s = new Scanner(new File(fileName), "UTF-8");

			int amount = Integer.valueOf(s.nextLine());

			for (int i = 0; i < amount; i++) {
				String[] line = s.nextLine().split(" ");

				String texture = line[0];
				int x = Integer.valueOf(line[1]);
				int y = Integer.valueOf(line[2]);
				int width = Integer.valueOf(line[3]);
				int height = Integer.valueOf(line[4]);

				textures_sprite_sheet.put(spriteSheetName + "_" + texture, new Rectangle(x, y, width, height));
				textures_sprite_sheet_texture.put(spriteSheetName + "_" + texture, spriteSheetName);
			}
		} catch (Exception e) {
			System.err.println("Error loading spritesheet: " + spriteSheetName);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static BufferedImage getImagePng(String textureName) {
		if (textures_png.containsKey(textureName))
			return textures_png.get(textureName);
		else if(textures_sprite_sheet.containsKey(textureName)) {
			Rectangle rec = textures_sprite_sheet.get(textureName);
			return textures_png.get(textures_sprite_sheet_texture.get(textureName)).getSubimage(rec.x, rec.y, rec.width, rec.height);
		}
		throw new RuntimeException("No such image: " + textureName);
	}

	public static List<String> getAllImageNames() {
		List<String> out = new ArrayList<>();

		out.addAll(textures_sprite_sheet.keySet());
		for(String s: textures_png.keySet()) {
			if(!isSheet.containsKey(s)) {
				out.add(s);
			}
		}

		return out;
	}
}