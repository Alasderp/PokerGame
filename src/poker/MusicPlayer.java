package poker;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.SceneBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Font;
import javafx.stage.StageBuilder;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.awt.List;
import java.io.File;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSlider;
import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;

public class MusicPlayer extends JFrame {

	private JPanel contentPane;
	
	private Media hit;
	private static MediaPlayer mediaPlayer;
	private static boolean paused = false;
	private static boolean playing = false;
	private List songList;
	private static JSlider volume;
	private static JSlider timeBar;
	private static ChangeListener seekListener;
	private static ChangeListener songTimeListener;
	
	private int lastSongIndex = 0;
	
	private boolean init = false;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MusicPlayer frame = new MusicPlayer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}); 	
		  	
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JFXPanel(); // this will prepare JavaFX toolkit and environment
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        StageBuilder.create()
                                .scene(SceneBuilder.create()
                                        .width(0)
                                        .height(0)
                                        .root(LabelBuilder.create()
                                                .font(Font.font("Arial", 54))
                                                .text("JavaFX")
                                                .build())
                                        .build())
                                		.style(StageStyle.UTILITY)
                                		.maxHeight(0.00)
                                		.maxWidth(0.00)
                                		.opacity(0f)
                                .onCloseRequest(new EventHandler<WindowEvent>() {
                                    @Override
                                    public void handle(WindowEvent windowEvent) {
                                        System.exit(0);
                                    }
                                })
                                .build()
                                .show();
                    }
                });
            }
        });
	}

	/**
	 * Create the frame.
	 */
	public MusicPlayer() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 464, 359);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		List list = new List();
		songList = list;
		list.setEnabled(false);
		list.select(0);
		
		JButton btnPrevious = new JButton("Previous");
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Previous");

				if(list.getSelectedIndex() == 0) {
					list.select(lastSongIndex - 1);
				}
				else {
					list.select(list.getSelectedIndex() - 1);
				}
				paused = false;
				if(playing) {
					mediaPlayer.dispose();
				}
				
				if(!init) {
					init();
				}
				else {
					timeBar.setValue(0);
					
					hit = new Media(new File("Music/" + list.getSelectedItem()).toURI().toString());
					mediaPlayer = new MediaPlayer(hit);
					mediaPlayer.setOnReady(new Runnable() {
			            @Override
			            public void run() {
			        		timeBar.setMaximum((int)hit.getDuration().toSeconds());
			        		setListener();		            }
			        });
					setVolume();
					mediaPlayer.play();
					playing = true;
					playOne();
				}
				
			}
		});
		
		File folder = new File("Music/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        System.out.println(file.getName());
		        list.add(file.getName());
		    }
		}
		
		lastSongIndex = listOfFiles.length;
		
		JButton btnStop = new JButton("Pause");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Pause");
					if(playing) {
						mediaPlayer.pause();
						paused = true;
					}
				
			}
		});
		
		JSlider timeSlider = new JSlider();
		timeSlider.setValue(0);
		timeBar = timeSlider;
		
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Next Song");
				if(list.getSelectedIndex() == (lastSongIndex - 1)) {
					list.select(0);
				}
				else {
					list.select(list.getSelectedIndex() + 1);
				}
				paused = false;

				if(playing) {
					mediaPlayer.dispose();
				}
	
				if(!init) {
					init();
				}
				else {
					timeSlider.setValue(0);
					
					hit = new Media(new File("Music/" + list.getSelectedItem()).toURI().toString());
					mediaPlayer = new MediaPlayer(hit);
					
					mediaPlayer.setOnReady(new Runnable() {
			            @Override
			            public void run() {
			        		timeBar.setMaximum((int)hit.getDuration().toSeconds());
			        		setListener();
			            }
			        });				
	
					setVolume();
					mediaPlayer.play();
					playing = true;
					playOne();
				}
			}
		});
		
		JButton btnPlay = new JButton("Play");
		btnPlay.setSize(new Dimension(25, 40));
		btnPlay.setMaximumSize(new Dimension(40, 23));
		btnPlay.setPreferredSize(new Dimension(40, 23));
		btnPlay.setMinimumSize(new Dimension(40, 23));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!init) {
					init();
				}
				else if(!list.getSelectedItem().equals(null)) {
					System.out.println("Play");
					if(!paused && !playing) {
						hit = new Media(new File("Music/" + list.getSelectedItem()).toURI().toString());
						mediaPlayer = new MediaPlayer(hit);
					}
					setVolume();
					mediaPlayer.play();
					playing = true;
				}
				
			}
		});
		
		JSlider slider = new JSlider();
		
		volume = slider;
		
		slider.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent event) {
			        int value = volume.getValue();
			        double value2 = value + 0.0;
			        if(playing || paused) {
			        	mediaPlayer.setVolume(value2/100);
				     }
		      }
	  	});
		
		
		seekListener = new ChangeListener() {
		      public void stateChanged(ChangeEvent event) {
		    	  mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
		      }
	  	};
	  	
		timeSlider.addChangeListener(seekListener);
		
		
		JLabel lblSeek = new JLabel("Seek");
		
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(list, GroupLayout.PREFERRED_SIZE, 429, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
							.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
								.addContainerGap()
								.addComponent(timeSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addComponent(btnStop, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
								.addComponent(btnPrevious)
								.addGap(5)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
									.addComponent(btnPlay, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(slider, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnNext)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(201)
							.addComponent(lblSeek)))
					.addContainerGap(9, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(list, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(btnPrevious, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(btnPlay, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addComponent(btnNext, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnStop)
					.addPreferredGap(ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
					.addComponent(lblSeek)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(timeSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		contentPane.setLayout(gl_contentPane);
		
	}
	
	public void init() {
		
		init = true;
		
		/*
		songTimeListener = new ChangeListener() {
		      public void stateChanged(ChangeEvent event) {
      		    // Temporarily remove the listener on the slider, so it doesn't respond to the change in playback time
      		    // I thought timeSlider.isValueChanging() would be useful for this, but it seems to get stuck at true
      		    // if the user slides the slider instead of just clicking a position on it.
      		    timeBar.removeChangeListener(seekListener);

      		    // Keep timeText's text up to date with the slider position.
      		    Duration currentTime = mediaPlayer.getCurrentTime();
      		    int value = (int) currentTime.toSeconds();
      		    timeBar.setValue(value);    

      		    // Re-add the slider listener
      		    timeBar.addChangeListener(seekListener);
		      }
	  	};	
	  	*/
		
		hit = new Media(new File("Music/" + songList.getSelectedItem()).toURI().toString());			
		mediaPlayer = new MediaPlayer(hit);
		
		mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
        		timeBar.setMaximum((int)hit.getDuration().toSeconds());
        		setListener();
            }
        });
		
		setVolume();
		mediaPlayer.play();
		playing = true;
		playOne();
	}
	
	public void playOne(){
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.dispose();
                timeBar.setValue(0);
				if(songList.getSelectedIndex() == (lastSongIndex - 1)) {
					songList.select(0);
				}
				else {
					songList.select(songList.getSelectedIndex() + 1);
				}
                hit = new Media(new File("Music/" + songList.getSelectedItem()).toURI().toString());
                mediaPlayer = new MediaPlayer(hit);
                
        		mediaPlayer.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                		timeBar.setMaximum((int)hit.getDuration().toSeconds());
                		setListener();
                    }
                });
              
                setVolume();
                mediaPlayer.play();
                playTwo();
            }
        });
	}
	
	public void playTwo(){
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.dispose();
                timeBar.setValue(0);
				if(songList.getSelectedIndex() == (lastSongIndex - 1)) {
					songList.select(0);
				}
				else {
					songList.select(songList.getSelectedIndex() + 1);
				}
                hit = new Media(new File("Music/" + songList.getSelectedItem()).toURI().toString());
                mediaPlayer = new MediaPlayer(hit);
        		mediaPlayer.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                		timeBar.setMaximum((int)hit.getDuration().toSeconds());
                		setListener();
                    }
                });
                
                setVolume();
                mediaPlayer.play();
                playOne();
            }
        });
	}
	
	public void pause() {
		if(playing) {
			mediaPlayer.pause();
			paused = true;
		}
	}
	
	public void resume() {
		if(paused) {
			paused = false;
			playing = true;
			setVolume();
			mediaPlayer.play();
		}
	}
	
	public void setVolume() {
		double newVolume = volume.getValue() + 0.0;
		mediaPlayer.setVolume(newVolume/100);
	}
	
	public void setListener() {
		
		mediaPlayer.currentTimeProperty().addListener(l-> {
			
  		    timeBar.removeChangeListener(seekListener);
  		    Duration currentTime = mediaPlayer.getCurrentTime();
  		    int value = (int) currentTime.toSeconds();
  		    timeBar.setValue(value);    
  		    timeBar.addChangeListener(seekListener);
			
		});
		
	}
}
