package com.hypermedia.HyperMediaAuthor;

import java.awt.Color;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    
    private Boolean bPlayed;

    private int nbCurFrame;

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
        
        // ActionListener
        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isLoaded()) return;

                if (slider.getMinimum() < slider.getValue()) {
                    slider.setValue(slider.getValue() - 1);
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
            }
        });

        btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                if (!isLoaded()) {
                    return;
                }

                if (bPlayed) return;

                bPlayed = true;
                nbCurFrame = slider.getValue();
                
                // Runs outside of the Swing UI thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (bPlayed) {
                            // Runs inside of the Swing UI thread

                            if (nbCurFrame + 1 == frameCtl.getTotalFrameCnt()) {
                                bPlayed = false;
                                break;
                            } 
                            try {
                                if (0 == nbCurFrame % 3) {
                                    Thread.sleep(34);
                                } else {
                                    Thread.sleep(33);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

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
                    }

                    
                }).start();
			}
        });

        btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                bPlayed = false;

			}
        });
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if ((JSlider) e.getSource() == slider) {
            if (null == frameCtl) return;
            try {
                nbCurFrame = slider.getValue();
                lblFrameStr.setText((nbCurFrame + 1) + " th / " + frameCtl.getTotalFrameCnt() + " Total");
                setFramePos(nbCurFrame);
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
            lblFrameStr.setText("1 th / " + nbTotalFrame + " Total");
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
        MouseMotionListener[] listeners = panel.getMouseMotionListeners();
        if (0 < listeners.length) {
            for (MouseMotionListener l : listeners) {
                panel.removeMouseMotionListener(l);
            }
        }
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

    public void updateImage() throws Exception {
        setFramePos(slider.getValue());
    }

    public void setFramePos(int nbFrame) throws Exception {
        BufferedImage image = frameCtl.getFrameImage(nbFrame, panel.getWidth(), panel.getHeight());
        panel.setImage(image);
    }

    public boolean isPlaying() {
        return bPlayed;
    }
}