package com.hypermedia.HyperMediaPlayer;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.lang.Math;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Hello world!
 *
 */
public class App extends JFrame {
    private static final String FILE_EXTENSION = "json";

    private JPanel contentPane;

    private JButton btnImport;
    private ImagePlayer player;

    private ArrayList<LinkContext> listContext;
    
    private JButton btnForward;
    private JButton btnBackward;
    
    private JLabel lblLinkName;
    private JLabel lblDestination;
    
    private JList list;
    
    private JTextArea textArea;
    private JCheckBox chkShowLinkBounding;
    
    private int curContextIndex;


    /**
	 * Create the frame.
	 */
	public App() {
        contentPane = new JPanel();

        btnImport = new JButton();
        contentPane.add(btnImport);
        
        player = new ImagePlayer();
        contentPane.add(player);
        
        JLabel lbl1 = new JLabel("Link name :");
        lbl1.setFont(new Font("Dialog", Font.BOLD, 15));
        lbl1.setBounds(430, 125, 90, 15);
        contentPane.add(lbl1);
        
        JLabel lbl2 = new JLabel("Destination :");
        lbl2.setFont(new Font("Dialog", Font.BOLD, 15));
        lbl2.setBounds(430, 150, 100, 15);
        contentPane.add(lbl2);
        
        JLabel lblLinkHistory = new JLabel("Link History");
        lblLinkHistory.setFont(new Font("Dialog", Font.BOLD, 15));
        lblLinkHistory.setBounds(430, 175, 100, 15);
        contentPane.add(lblLinkHistory);
        
        btnBackward = new JButton();
        contentPane.add(btnBackward);
        
        btnForward = new JButton();
        contentPane.add(btnForward);
        
        lblLinkName = new JLabel();
        contentPane.add(lblLinkName);
        
        lblDestination = new JLabel();
        contentPane.add(lblDestination);
        
        list = new JList();
        DefaultListModel<String> dlm = new DefaultListModel<String>();
        list.setModel(dlm);
        contentPane.add(list);
        
        JLabel lblLinkInformation = new JLabel("Link Information");
        lblLinkInformation.setFont(new Font("Dialog", Font.BOLD, 15));
        lblLinkInformation.setBounds(430, 315, 135, 15);
        contentPane.add(lblLinkInformation);
        
        textArea = new JTextArea();
        JScrollPane scrollpane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
    			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setBounds(430, 335, 220, 150);
        contentPane.add(scrollpane);
        
        chkShowLinkBounding = new JCheckBox();
        contentPane.add(chkShowLinkBounding);
        
        listContext = new ArrayList<LinkContext>();

        InitializeFrame();
    }

    private void InitializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 560);
        
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
        btnImport.setText("Open");
        btnImport.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		btnImport.setBounds(70, 25, 95, 75);
        
        player.setBounds(50, 110, ImagePlayer.PANEL_DEFAULT_WIDTH, ImagePlayer.PANEL_DEFAULT_HEIGHT);
        player.Initialize();
        
        btnBackward.setText("<");
        btnBackward.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        btnBackward.setBounds(195, 25, 95, 75);
        
        btnForward.setText(">");
        btnForward.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        btnForward.setBounds(297, 25, 95, 75);
        
        lblLinkName.setText("");
        lblLinkName.setFont(new Font("Dialog", Font.BOLD, 15));
        lblLinkName.setBounds(530, 125, 200, 15);
        
        lblDestination.setText("");
        lblDestination.setFont(new Font("Dialog", Font.BOLD, 15));
        lblDestination.setBounds(540, 150, 200, 15);
        
        list.setBounds(430, 200, 220, 100);
        
        textArea.setEditable(false);
        textArea.setBounds(430, 335, 220, 150);
        textArea.setLineWrap(true);
        
        chkShowLinkBounding.setText("Show Link Bounding");
        chkShowLinkBounding.setSelected(true);
        chkShowLinkBounding.setBounds(430, 485, 160, 23);
        
        listContext.clear();
        
        // ActionListener
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(contentPane)) {
                    try {
                        File f = fc.getSelectedFile();
                        if (!f.isDirectory()) {
                            String msg = f.toString() + " is not directory. The input path should be a directory.";
                            System.err.println(msg);
                            JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        LinkContext context = getLinkContextFromMediaPath(f.getAbsolutePath(), "", 0, 0);
                        
                        listContext = new ArrayList<LinkContext>();
                        listContext.add(context);
                        
                        DefaultListModel<String> dlm = (DefaultListModel<String>)list.getModel();
                        dlm.clear();
                        dlm.addElement(context.getMediaName());
                        list.setSelectedValue(context.getMediaName(), true);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        btnBackward.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (0 < curContextIndex) {
        			if (player.isPlaying()) {
                    	player.stop();
                    }
                    
					LinkContext context = listContext.get(curContextIndex);
					context.setCurFrame(player.getCurFrameNum());
        			list.setSelectedIndex(curContextIndex - 1);
        		}
        	}
        });
        
        btnForward.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (curContextIndex < listContext.size() - 1) {
        			if (player.isPlaying()) {
                    	player.stop();
                    }
                    
					LinkContext context = listContext.get(curContextIndex);
					context.setCurFrame(player.getCurFrameNum());
					
        			list.setSelectedIndex(curContextIndex + 1);
        		}
        	}
        });

        player.setMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println("Mouse Moved"
                + " (" + e.getX() + "," + e.getY() + ")"
                + " detected on "
                + e.getComponent().getClass().getName());
            }
        
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("Mouse Dragged"
                + " (" + e.getX() + "," + e.getY() + ")"
                + " detected on "
                + e.getComponent().getClass().getName());
            }
        });

        player.setMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                String linkName = null;
                Rectangle linkRect = null;
                double min = 0;
                
                LinkContext context = listContext.get(list.getSelectedIndex());
                HashMap<Integer, HashMap<String, Rectangle>> linkFrames = context.getLinkFrames();

                int nbCurFrame = player.getCurFrameNum();
                if (linkFrames.containsKey(nbCurFrame)) {                	
                	
                    HashMap<String, Rectangle> hm = linkFrames.get(nbCurFrame);
                    Set<String> keys = hm.keySet();
                    for (String key : keys) {
                        Rectangle rect = hm.get(key);

                        if (!(rect.x <= x && x <= rect.x + rect.width && rect.y <= y && y < rect.y + rect.height)) continue;
                        
                        if (null == linkName) {
                        	linkName = key;
                        	linkRect = rect;
                        	min = Math.sqrt(Math.pow(x - rect.x + rect.width / 2, 2) + Math.pow(y - rect.y + rect.height / 2, 2));
                        } else {
                        	double length = Math.sqrt(Math.pow(x - rect.x + rect.width / 2, 2) + Math.pow(y - rect.y + rect.height / 2, 2));
                        	if (length < min) {
                        		linkName = key;
                        		linkRect = rect;
                        		min = length;
                        	}
                        }
                    }
                            
                    System.out.println("clicked in " + linkName);
                    
                    if (player.isPlaying()) {
                    	player.stop();
                    }
                    
                    context.setCurFrame(player.getCurFrameNum());
                    LinkInfoVO info = context.getLinks().get(linkName);
                    
                    LinkContext destContext = null;
                    try {
						destContext = getLinkContextFromMediaPath(info.getDestinationPathName(), linkName, 0, 0);
					} catch (IOException ex) {
						System.err.println(ex.getMessage());
						
						String destPathName = info.getDestinationPathName();
						String mediaName = destPathName.substring(destPathName.lastIndexOf(File.separator) + 1);
						
						destContext = new LinkContext();
						destContext.setLinkName(linkName);
						destContext.setMediaName(mediaName);
						destContext.setPathName(destPathName);
						destContext.setCurFrame(info.getDestinationFrameFrom());
						destContext.setStartFrame(info.getDestinationFrameFrom());
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        System.err.println(ex.getMessage());
                        ex.printStackTrace();
                        return;
					}
                    
                    lblLinkName.setText(linkName);
                    lblDestination.setText(destContext.getMediaName());
                    
                    DefaultListModel<String> dlm = (DefaultListModel<String>)list.getModel();
                    
                    for (int i = dlm.size() - 1; list.getSelectedIndex() < i; i--) {
                    	listContext.remove(i);
                    	dlm.removeElementAt(i);
                    }
                    
                    listContext.add(destContext);
                    dlm.addElement(destContext.getMediaName());
                    list.setSelectedValue(destContext.getMediaName(), true);
                    
                }
            }
        });
        
        player.setImagePlayerEventListener(new ImagePlayerEventListener() {
			@Override
			public void playDone() {
				if (0 < curContextIndex) {
					LinkContext context = listContext.get(curContextIndex);
					LinkContext prev = listContext.get(curContextIndex - 1);
					context.setCurFrame(prev.links.get(context.getLinkName()).getDestinationFrameFrom());
        			list.setSelectedIndex(curContextIndex - 1);
        		}
			}
        });
        
        player.getPanel().setImagePanelEventListener(new ImagePanelEventListener() {
            @Override
            public void beforeDrawImage(BufferedImage image) {
                LinkContext context = listContext.get(list.getSelectedIndex());
                if (null == context.getLinkFrames()) return;
                
                HashMap<Integer, HashMap<String, Rectangle>> linkFrames = context.getLinkFrames();
                if (linkFrames.containsKey(player.getCurFrameNum())) {
                    HashMap<String, Rectangle> hm = linkFrames.get(player.getCurFrameNum());

                    Set<String> keys = hm.keySet();
                    Graphics graph = image.getGraphics();
                    for (String key : keys) {
                    	Rectangle rect = hm.get(key);
                        if (0 == rect.getWidth() || 0 == rect.getHeight()) continue;

                        if (chkShowLinkBounding.isSelected()) {
                        	graph.setColor(Color.CYAN);
                        	graph.drawRect(rect.x, rect.y, rect.width, rect.height);
                        }
                        
                        textArea.append(String.format("%s link at %d frame(%d, %d, %d, %d)\n", key, player.getCurFrameNum(), rect.x, rect.y, rect.width, rect.height));
                    }

                    graph.dispose();
                }
            }
        });
        
        list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					if (player.isPlaying()) {
                    	player.stop();
                    }
                    
					LinkContext context = listContext.get(curContextIndex);
					context.setCurFrame(player.getCurFrameNum());
				} else {
					curContextIndex = list.getSelectedIndex();
					if (curContextIndex < 0) return;
					
					LinkContext context = listContext.get(curContextIndex);
					
					try {
						context.setEndFrame(player.loadImages(context.getPathName()));
						lblLinkName.setText(context.getLinkName());
						lblDestination.setText(0 < curContextIndex ? context.getMediaName() : "");
						textArea.setText("");
						player.setFramePos(context.getCurFrame());
						player.play();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
        	
        });
    }
    
    private LinkContext getLinkContextFromJson(final String jsonPath, final String linkName, final int startFrame, final int endFrame) throws Exception {
    	
    	HashMap<Integer, HashMap<String, Rectangle>> linkFrames = new HashMap<Integer, HashMap<String, Rectangle>>();
    	HashMap<String, LinkInfoVO> links = new HashMap<String, LinkInfoVO>();
    	
    	LinkContext context = new LinkContext();
    	
    	try {
    		FileInputStream fis;
			fis = new FileInputStream(jsonPath);
			BufferedInputStream bis = new BufferedInputStream(fis);
			String json = new String(bis.readAllBytes());
			
			ArrayList<LinkInfoVO> list = LinkInfoVO.toLinkInfoVO(json);
			for (LinkInfoVO info : list) {
				links.put(info.getLinkName(), info);
				
				HashMap<Integer, Rectangle> hm = info.getFrame();
				Set<Integer> keys = hm.keySet();
				for (Integer key : keys) {
					HashMap<String, Rectangle> hmRect = null;
					if (linkFrames.containsKey(key)) {
						hmRect = linkFrames.get(key);
					} else {
						hmRect = new HashMap<String, Rectangle>();
						linkFrames.put(key, hmRect);
					}
					
					hmRect.put(info.getLinkName(), hm.get(key));
				}
			} 
			
			LinkInfoVO info = list.get(0);
			
			String srcPathName = info.getOriginPathName();
			context.setLinkName(linkName);
			context.setPathName(srcPathName);
			context.setMediaName(srcPathName.substring(srcPathName.lastIndexOf(File.separator) + 1));
			context.setStartFrame(startFrame);
			context.setEndFrame(endFrame);
			context.setCurFrame(startFrame);
			context.setLinkFrames(linkFrames);
			context.setLinks(links);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw e;
		}
        
        return context;
    }
    
    private LinkContext getLinkContextFromMediaPath(final String linkPath, final String linkName, final int startFrame, final int endFrame) throws Exception {
    	String linkFilePath = linkPath + File.separator + "link." + FILE_EXTENSION;
        File linkFile = new File(linkFilePath);
        if (!linkFile.exists()) {
            throw new IOException("Can't find link file from " + linkPath.toString());                            
        }

        return getLinkContextFromJson(linkFilePath, linkName, 0, 0);
    }


    public static void main( String[] args ) {
        EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App frame = new App();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}
