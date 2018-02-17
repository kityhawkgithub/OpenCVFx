package wxjump;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class WxModel 
{
	public static final String tmpfolderPath = "D:/jumpCheat";
	public static final String tmpPhoneImgPath = "/sdcard/";
	public static final String ScreenPicName = "tmp.png";
	
	public static final String phoneImg = tmpPhoneImgPath+ScreenPicName;
	public static final String pcImg = tmpfolderPath+"/" +ScreenPicName;
	public static final String tmpImg = (WxModel.class.getResource("")+"template.jpg").substring(6);
	
	public static Mat mat_src;
	public static Mat mat_match;
	public static Mat mat_temp;
	
	public static Point chessPoint;
}
