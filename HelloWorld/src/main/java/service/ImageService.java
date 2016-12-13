package service;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;

public class ImageService {
	
	public BufferedImage resizeImage(BufferedImage image){
		BufferedImage bufferedImage = null;
		if(image.getWidth()>image.getHeight()){
			bufferedImage = Scalr.resize(image,  Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH,
					720, 480, Scalr.OP_ANTIALIAS);
		}else {
			bufferedImage = Scalr.resize(image,  Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH,
					360, 240, Scalr.OP_ANTIALIAS);
		}
		
		return bufferedImage;		
	}

}
