package com.hypermedia.HyperMediaPlayer;

import java.awt.Color;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImagePlayer extends JPanel implements ChangeListener {

    public static final int PANEL_DEFAULT_WIDTH = 366;
    public static final int PANEL_DEFAULT_HEIGHT = 385;

    private ImagePanel panel;

    private JButton btnPrev;
    private JButton btnNext;

    private JSlider slider;
    private JLabel lblFrameStr;

    private JButton btnPlay;
    private JButton btnStop;

    private String pathName;
    
    private FrameController frameCtl;
    private AudioController audioCtl;
    
    private Boolean bPlayed;

    private int nbCurFrame;

    private long period;
    
    private ImagePlayerEventListener eventListener;

    /**
	 * Create the panel.
	 */
	public ImagePlayer() {
		setLayout(null);
		
        panel = new ImagePanel();
        
        btnPrev = new JButton();
        btnNext = new JButton();

        slider = new JSlider();
		lblFrameStr = new JLabel();

        btnPlay = new JButton();
        btnStop = new JButton();

        pathName = null;
        frameCtl = null;
    }
    
    public void Initialize() {
        panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel.setBounds(6, 6, 352, 288);
        add(panel);

        btnPrev.setText("<<");
        btnPrev.setBounds(6, 300, 30, 29);
        add(btnPrev);

        btnNext.setText(">>");
        btnNext.setBounds(328, 300, 30, 29);
        add(btnNext);
        
        slider.setValue(0);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
        slider.setBounds(36, 300, 292, 29);
        slider.addChangeListener(this);
        slider.setValue(0);
        add(slider);

        lblFrameStr.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrameStr.setBounds(6, 326, 352, 16);
        add(lblFrameStr);
        
        btnPlay.setText("Play");
		btnPlay.setBounds(40, 354, 117, 29);
        add(btnPlay);
        
        btnStop.setText("Stop");
		btnStop.setBounds(200, 354, 117, 29);
        add(btnStop);

        bPlayed = false;

        nbCurFrame = 0;

        slider.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseReleased(MouseEvent e) {
                try {
                    audioCtl.setMillisecondPosition(getMillisecondFromFrame(nbCurFrame));
                    period = System.currentTimeMillis();
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                }
        	}
        });
        
        // ActionListener
        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isLoaded()) return;

                if (slider.getMinimum() < slider.getValue()) {
                    slider.setValue(slider.getValue() - 1);
                    
                    try {
                        audioCtl.setMillisecondPosition(getMillisecondFromFrame(nbCurFrame));
                        period = System.currentTimeMillis();
                    } catch (Exception ex) {
                        System.err.println(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });

        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isLoaded()) return;

                if (slider.getValue() < slider.getMaximum()) {
                    slider.setValue(slider.getValue() + 1);
                }

                try {
                    audioCtl.setMillisecondPosition(getMillisecondFromFrame(nbCurFrame));
                    period = System.currentTimeMillis();
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				play();
			}
        });

        btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                stop();
			}
        });
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if ((JSlider) e.getSource() == slider) {
        	if (null == frameCtl) return;
        	try {
        		nbCurFrame = slider.getValue();
        		if (frameCtl.getCurFrameNum() != nbCurFrame) {
        			lblFrameStr.setText(getMediaDurationString() + " (" + (nbCurFrame + 1) + " th / " + frameCtl.getTotalFrameCnt() + " Total)");
        			setFramePos(nbCurFrame);
        			
        			if (nbCurFrame % 100 == 1)
        				audioCtl.setMillisecondPosition(getMillisecondFromFrame(nbCurFrame));
        		}
        	} catch (Exception ex) {
        		JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        		System.out.println(ex.getMessage());
        		ex.printStackTrace();
        	}            
        }
    }

    public int loadImages(String pathName) throws Exception {
        this.pathName = pathName;

        int nbTotalFrame = 0;

        try {
            frameCtl = new FrameController(pathName); 
            nbTotalFrame = frameCtl.getTotalFrameCnt();

            slider.setMinimum(0);
            slider.setMaximum(nbTotalFrame - 1);
            lblFrameStr.setText(toMillisecondToString(0) + " (1 th / " + nbTotalFrame + " Total");

            nbCurFrame = 0;
            setFramePos(nbCurFrame);

            audioCtl = new AudioController();
            audioCtl.load(pathName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            throw e;
        }

        return nbTotalFrame;
    }

    public boolean isLoaded() {
        return null != frameCtl;
    }

    public String getPathName() {
        return pathName;
    }

    public ImagePanel getPanel() {
        return panel;
    }

    public FrameController getFrameController() {
        return frameCtl;
    }

    public int getCurFrameNum() {
        return slider.getValue();
    }

    public int getTotalFrameCnt() {
        return frameCtl.getTotalFrameCnt();
    }

    public void setMouseMotionListener(MouseMotionListener listener) {
        removeMouseMotionListener();

        panel.addMouseMotionListener(listener);
    }

    public void removeMouseMotionListener() {
        MouseMotionListener[] listeners = panel.getMouseMotionListeners();
        if (0 < listeners.length) {
            for (MouseMotionListener l : listeners) {
                panel.removeMouseMotionListener(l);
            }
        }
    }

    public void setMouseListener(MouseListener listener) {
        removeMouseListener();

        panel.addMouseListener(listener);
    }

    public void removeMouseListener() {
        MouseListener[] listeners = panel.getMouseListeners();
        if (0 < listeners.length) {
            for (MouseListener l : listeners) {
                panel.removeMouseListener(l);
            }
        }
    }

    public void updateImage() throws Exception {
        setFramePos(slider.getValue());
    }

    public void setFramePos(int nbFrame) throws Exception {
        if (slider.getValue() != nbFrame) {
    		slider.setValue(nbFrame);
    	} else {
    		BufferedImage image = frameCtl.getFrameImage(nbFrame, panel.getWidth(), panel.getHeight());
    		panel.setImage(image);    		
    	}
    }
    
    public void play() {
    	if (!isLoaded()) {
            return;
        }

        if (bPlayed) return;

        bPlayed = true;
        nbCurFrame = slider.getValue();

        period = System.currentTimeMillis();
        
        // Runs outside of the Swing UI thread
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    audioCtl.play(getMillisecondFromFrame(nbCurFrame));
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                }

                period = System.currentTimeMillis();

                while (bPlayed) {
                    if (nbCurFrame + 1 == frameCtl.getTotalFrameCnt()) {
                        bPlayed = false;
                        break;
                    } 

                    try {       
                        long current = System.currentTimeMillis();
                        long interval = current - period;
                        if (interval < (0 == nbCurFrame % 3 ? 34 : 33)) {
                            Thread.sleep(0);
                            continue;
                        }

                        period = current;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Runs inside of the Swing UI thread
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                slider.setValue(nbCurFrame + 1);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });
                }
        
                System.out.println("Thread end "+Thread.currentThread().getName());
                if (null != eventListener) {
                	if (nbCurFrame == getTotalFrameCnt() - 1)
                		eventListener.playDone();
                }
            }
        }).start();
    }
    
    public void stop() {
    	bPlayed = false;
        
        audioCtl.pause();
        
        try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    public boolean isPlaying() {
        return bPlayed;
    }
    
    public void setImagePlayerEventListener(ImagePlayerEventListener listener) {
    	eventListener = listener;
    }

    private long getMillisecondFromFrame(long nbFrame) {
        if (nbFrame < 1) return 0;

        return (nbFrame * 33) + ((long)(nbFrame / 3) + 1);
    }
    
    private String getMediaDurationString() {
    	return toMillisecondToString(getMillisecondFromFrame(nbCurFrame + 1));
    }
    
    private String toMillisecondToString(long milliseconds) {
    	long totalSeconds = milliseconds / 1_000;
    	long hours = totalSeconds / 3_600;
    	long minutes = totalSeconds / 60;
    	long seconds = totalSeconds % 60;
    	
    	if (hours < 1) {
    		return String.format("%02d:%02d", minutes, seconds);
    	}
    	
    	return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}