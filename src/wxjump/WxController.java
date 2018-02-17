package wxjump;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WxController 
{
	@FXML
	private Button getImgBtn;
	@FXML
	private Button procImgBtn;
	@FXML
	private Button jumpBtn;

	@FXML
	private ImageView srcImg;	
	@FXML
	private ImageView procImg;
	@FXML
	private ImageView destImg;
	
	
	/**
	 * The action triggered by pushing the button on the GUI
	 *
	 * @param event
	 *            the push button event
	 */
	@FXML
	protected void getImgFromPhone(ActionEvent event)
	{		
		//创建本地临时目录
		File file = new File(WxModel.tmpfolderPath);
		if (!file.exists())	file.mkdir();
		
		try 
		{
			// 截图后保存到本地
			executeCommand("adb shell screencap -p "+WxModel.phoneImg);
			executeCommand("adb pull "+WxModel.phoneImg+" " +WxModel.pcImg);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		WxModel.mat_src = Imgcodecs.imread(WxModel.pcImg,Imgcodecs.CV_LOAD_IMAGE_COLOR);
		
		// convert and show the frame
		updateImageView(srcImg, WxService.mat2Image(WxModel.mat_src));
	}
	
	private static void executeCommand(String command) 
	{
		Process process = null;
		try 
		{
			process = Runtime.getRuntime().exec(command);
			System.out.println("exec command start: " + command);
			process.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line = bufferedReader.readLine();
			if (line != null)		System.out.println(line);
			System.out.println("exec command end: " + command);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		} 
		finally
		{
			if (process != null)	process.destroy();			
		}
	}
	
	private void updateImageView(ImageView view, Image image) {
		WxService.onFXThread(view.imageProperty(), image);
	}
	
	
	/**
	 * 获取棋子所在的坐标
	 */
	@FXML
	public void getChessPoint(ActionEvent event) 
	{
		Mat mat_match = Imgcodecs.imread(WxModel.pcImg,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat mat_temp = Imgcodecs.imread(WxModel.tmpImg, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat result = new Mat(mat_match.rows() - mat_temp.rows() + 1, mat_match.cols() - mat_temp.cols() + 1, CvType.CV_32FC1);
		// 读取图像到矩阵中
		Imgproc.matchTemplate(mat_match, mat_temp, result, Imgproc.TM_CCOEFF);
		Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
		Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
		
		WxModel.chessPoint = minMaxLocResult.maxLoc;
		
		Mat mat_procTmp = WxModel.mat_src.clone();
		Imgproc.rectangle(mat_procTmp, WxModel.chessPoint, new Point(WxModel.chessPoint.x + mat_temp.cols(),WxModel.chessPoint.y + mat_temp.rows()), new Scalar(0, 255, 0), 5);
		
		updateImageView(procImg, WxService.mat2Image(mat_procTmp));
		//return maxLoc;
	}
	
	@FXML
	public void getDistance(ActionEvent event)
	{
				// 为转换的灰度图申请空间

				Mat mat_src =  Imgcodecs.imread(WxModel.pcImg,Imgcodecs.CV_LOAD_IMAGE_COLOR);
				Mat mat_temp = Imgcodecs.imread(WxModel.tmpImg, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
				Rect roiArea=new Rect(0, 300 ,mat_src.width() , (int)(WxModel.chessPoint.y+ mat_temp.rows() - 300));  
		        
				mat_src=new Mat(mat_src,roiArea);  
				//Mat mat_display = new Mat(mat_src.rows(), mat_src.cols(), 0);
				Mat mat_display = mat_src.clone();

				//将处理好的彩色图片转换为灰度图，放到srcGrey
				Imgproc.cvtColor(mat_src, mat_src, Imgproc.COLOR_RGB2GRAY);
				
				//Imgproc.blur(srcGrey, srcGrey, new Size(3, 3));
				
				// 用来放边缘检测后的图片
				//Mat dst = srcGrey.clone();

				// 通过Canny来进行边缘检测
				Imgproc.Canny(mat_src, mat_src, 5.0, 10.0);
					
				//Imgproc.morphologyEx(srcGrey, srcGrey, Imgproc.MORPH_CLOSE, new Mat(5,5,CvType.CV_8U));
				//Imgproc.morphologyEx(srcGrey, srcGrey, Imgproc.MORPH_OPEN, new Mat(5,5,CvType.CV_8U));
				Imgproc.dilate(mat_src,mat_src,new Mat());
				Imgproc.dilate(mat_src,mat_src,new Mat());
				
			    // finding the contours
			    ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			    Mat hierarchy = new Mat();

	            //查看每个轮廓的信息 
	            //Imgproc.RETR_LIST：从最内层到最外层遍历
	            //Imgproc.RETR_TREE：从最外层到最内层遍历
	            //Imgproc.RETR_CCOMP：从最内层，然后是最外层，然后说中间层
	            //Imgproc.RETR_EXTERNAL：只显示最外层轮廓
	            //Imgproc.CHAIN_APPROX_SIMPLE：最外层轮廓数据只得到顶点的坐标
			    Imgproc.findContours(mat_src, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			
			    //for (MatOfPoint contour : contours)
			    
			    double maxarea = 0;
			    int maxcontour_id  = 0;
			    for(int i=0;i<contours.size();i++)
			    {
		            //System.out.println(contours.get(i).size()+"   rows: "+contours.get(i).rows()+"   cols: "+contours.get(i).cols());
		            //for(int j=0;j<contours.get(i).total();j++) System.out.println("轮廓 "+(i+1)+"的坐标信息："+contours.get(i).toList().get(j));
		            double ConArea = Imgproc.contourArea(contours.get(i), true);
		            if(ConArea >= maxarea) 
		            {
		            	maxarea = ConArea;
		            	maxcontour_id = i;
		            }
		            //System.out.println("------------------------------");
		        }
			    MatOfPoint temp_largest = contours.get(maxcontour_id);
			    ArrayList<MatOfPoint> largest_contours = new ArrayList<MatOfPoint>();
			    largest_contours.add(temp_largest);
			    Imgproc.drawContours(mat_display,largest_contours, -1, new Scalar(0, 255, 0), 5);
			    
			    System.out.println(contours.get(maxcontour_id).size()+"   rows: "+contours.get(maxcontour_id).rows()+"   cols: "+contours.get(maxcontour_id).cols() + " maxArea:" + maxarea);
				updateImageView(destImg, WxService.mat2Image(mat_display));
				
	}
	
	
}
