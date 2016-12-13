
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;

import service.ImageService;

import org.bytedeco.javacv.Java2DFrameConverter;



/**
 * Main class
 * 
 * @author Ivan Marchenko
 */
public class Main {

    /** The frame of user interface */
    private JFrame frame;
    
    /** The video panel to display video */
    private JPanel videoPanel;
    
    /** Label to display one frame*/
    private JLabel video;
    
    /** The start/stop button to start or stop video */
    private JButton startStop;
    
    /** Button to choose file for analysis */
    private JButton chooseVideoFile;
    
    private JButton chooseImageFile;
    
    /** Play video from beginning */
    private JButton playFromStart;
    
    /** The tool bar*/
    private JToolBar toolBar;
    
    /** The separators to separate tools on tool bar */
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JSeparator jSeparator3;
    private JSeparator jSeparator4;
    
    /** The video source */
    private String videoSource;
    private String imageSource;
    
    /** Flag used to reinit video stream */
    private boolean reinitVideoStream = false;
    
    /** button group to group radio buttons */
    private ButtonGroup group;
    
    
    /** Parameter to start/stop video */
    private boolean stop = true;
    
    /** Parameter to run loop for play video */
    private boolean play = true;
    
    private FFmpegFrameGrabber frameGrabber;
    
    private Java2DFrameConverter java2dFrameConverter;
    
    private ImageService imageService = new ImageService();
    /** Loger */
    private static final Logger LOG = Logger.getLogger(Main.class.getSimpleName());
    

    /**
     * Method needed to create user interface and run image analysis based on method chosen by user.
     */
    public void createWindow() {
    	java2dFrameConverter = new Java2DFrameConverter();
      //initialize parameters
        frame = new JFrame("Hello World");
        toolBar = new javax.swing.JToolBar();
        video = new JLabel();
        videoPanel = new JPanel();
        jSeparator1 = new JToolBar.Separator();
        jSeparator2 = new JToolBar.Separator();
        jSeparator3 = new JToolBar.Separator();
        jSeparator4 = new JToolBar.Separator();
        startStop = new JButton();
        chooseVideoFile = new JButton();
        chooseImageFile = new JButton();
        group = new ButtonGroup();
        playFromStart =new JButton();
      //set up tool bar
        toolBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.add(jSeparator1);
           
      //set up chooseVideoFile button
        chooseVideoFile.setText("Load video file");
        chooseVideoFile.setFocusable(false);
        chooseVideoFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chooseVideoFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chooseVideoFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseVideoFileButtonActionPerformed(evt);
            }
        });
        toolBar.add(chooseVideoFile);
        toolBar.add(jSeparator2);
        //Set up 
      //set up chooseVideoFile button
        chooseImageFile.setText("Load image file");
        chooseImageFile.setFocusable(false);
        chooseImageFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chooseImageFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chooseImageFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseImageFileButtonActionPerformed(evt);
            }            
        });
        toolBar.add(chooseImageFile);
        toolBar.add(jSeparator3);
      //set up startStop button
        startStop.setText("Start/Stop");
        startStop.setFocusable(false);
        startStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        startStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        startStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStopButtonActionPerformed(evt);
            }
        });
        toolBar.add(startStop);
        toolBar.add(jSeparator4);
        
        playFromStart.setText("Play from beginning");
        playFromStart.setFocusable(false);
        playFromStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playFromStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        playFromStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playFromStartButtonActionPerformed(evt);
            }
        });
        toolBar.add(playFromStart);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //set up videoPanel
        videoPanel.setBackground(Color.GRAY);
        videoPanel.setPreferredSize(new java.awt.Dimension(720, 480)); 
        videoPanel.setLayout(new javax.swing.BoxLayout(videoPanel, javax.swing.BoxLayout.LINE_AXIS));
      //set up video        
        video.setPreferredSize(new java.awt.Dimension(720, 480));        
        video.setLayout(new javax.swing.BoxLayout(video, javax.swing.BoxLayout.LINE_AXIS));
        videoPanel.add(video);
      //set up horizontal and vertical layouts
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
            .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        
        frame.pack();
        frame.setVisible(true);
        //start play loop 
        while(play){
            LOG.log(Level.INFO,"play "+stop);
            videoLoop();
        }
    }
    
    /**
     * Video loop.
     *
     * @param readImageMat the image to analysis
     */
    private void videoLoop(){
		if (!stop) {
			//reinit Video Stream
			try {
				if (reinitVideoStream) {
					frameGrabber = new FFmpegFrameGrabber(videoSource);
					frameGrabber.start();
					reinitVideoStream = false;
				}
				Frame img = frameGrabber.grab();
				LOG.log(Level.INFO, "message");
				if (img != null) {
					BufferedImage bufferedImage = java2dFrameConverter.convert(img);
					imageAnalysis(bufferedImage);
					frame.pack();
					frame.setVisible(true);
				}else{
					frameGrabber.stop();
				}
			} catch (Exception e) {
				LOG.log(Level.INFO, e.toString());
			}
		}
    }
    
    private void imageAnalysis(BufferedImage image){
    		BufferedImage thumbnail = imageService.resizeImage(image);
            video.setIcon(new ImageIcon(thumbnail));
    }
       
    
    /**
     * Start stop button action performed. Run than user push start/stop button
     *
     * @param evt the action event
     */
    private void startStopButtonActionPerformed(java.awt.event.ActionEvent evt) {
        LOG.log(Level.INFO, evt.paramString());
        if (stop) {
            stop = false;
        } else {
            stop = true;
        }
    }
    
    /**
     * Chose file button action performed. Used to select file for analysis.
     *
     * @param evt the action event than user push choose file button
     */
    private void chooseVideoFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
        LOG.log(Level.INFO, evt.paramString());
        videoSource = selectFile();        
        reinitVideoStream=true;
        stop=false;
    }
    
    private void chooseImageFileButtonActionPerformed(java.awt.event.ActionEvent evt){
    	LOG.log(Level.INFO, evt.paramString());
    	String imageSource = selectFile();
    	try {
			BufferedImage img = ImageIO.read(new File(imageSource));
			if(img!=null){
				reinitVideoStream=false;
		        stop=true;
				imageAnalysis(img);
				frame.pack();
                frame.setVisible(true);
			}
		} catch (IOException e) {
			LOG.log(Level.INFO,e.toString());
		}
    }
    
    
    /**
     * Set reinitVideoSteam flag on true to play video from beginning
     *
     * @param evt the action event than user push cplayFromStart button
     */
    private void playFromStartButtonActionPerformed(java.awt.event.ActionEvent evt) {
        LOG.log(Level.INFO, evt.paramString());
        reinitVideoStream = true;
    }
    
    private String selectFile(){
    	String filePath = "";
    	JFileChooser fileopen = new JFileChooser();             
        int ret = fileopen.showDialog(null, "Open file");                
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            filePath = file.getAbsolutePath();            
        }
    	return filePath;
    }

    /**
     * The main method which run all program
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        Main t = new Main();
        t.createWindow();
    }
}


