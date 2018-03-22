package poker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.SceneBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.StageBuilder;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import poker.ChatWindow;
import poker.ClientGUI;
import poker.MusicPlayer;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClientGUI extends JFrame {

	private JPanel contentPane;

	String[] playersHand = new String[5];
	JLabel[] cardArray = new JLabel[5];
	JCheckBox[] tickBoxArray = new JCheckBox[5];
	
	JTextArea infoBox;
	
	JButton swapConfirm;
	JLabel chipsLabel;
	JButton betBtn;
	JTextField betValue;
	JButton btnAllIn;
	
	JButton btnCheck;
	JButton makeBet;
	JButton btnFold;
	JButton swapBtn;
	
	private static MusicPlayer musicPlayer;
	private static ChatWindow chatField;
	
	String command = "";
	private static int PORT = 8181;
	private Socket socket;
	private BufferedReader in;
	private static PrintWriter out;
	private static String address = "127.0.0.1";
	private boolean swapCards = false;
	public boolean betMade = false;
	int chips = 0;
	boolean folded = false;
	String[] betInfo;
	private JTextField betInput;
	private JTextField nameField;

	
	public ClientGUI() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 525);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel Card1 = new JLabel("Card");
		Card1.setBounds(5, 312, 112, 153);
		Card1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JLabel Card2 = new JLabel("Card");
		Card2.setBounds(124, 312, 112, 153);
		Card2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JLabel Card3 = new JLabel("Card");
		Card3.setBounds(241, 312, 112, 154);
		Card3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JLabel Card4 = new JLabel("Card");
		Card4.setBounds(365, 312, 112, 154);
		Card4.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JLabel Card5 = new JLabel("Card");
		Card5.setBounds(489, 312, 112, 153);
		Card5.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JScrollPane scrollPane = new JScrollPane((Component) null);
		scrollPane.setBounds(5, 12, 612, 160);
		
		JCheckBox jCheckBox1 = new JCheckBox("");
		jCheckBox1.setBounds(44, 284, 25, 25);
		
		JCheckBox jCheckBox2 = new JCheckBox("");
		jCheckBox2.setBounds(174, 284, 25, 25);
		
		JCheckBox jCheckBox3 = new JCheckBox("");
		jCheckBox3.setBounds(282, 284, 25, 25);
		
		JCheckBox jCheckBox4 = new JCheckBox("");
		jCheckBox4.setBounds(407, 284, 25, 25);
		
		JCheckBox jCheckBox5 = new JCheckBox("");
		jCheckBox5.setBounds(528, 284, 25, 25);
		
		JLabel chipsDisplay = new JLabel("0");
		chipsDisplay.setBounds(5, 244, 96, 19);
		
		JLabel lblNewLabel = new JLabel("Chips");
		lblNewLabel.setBounds(0, 226, 57, 16);
		
		betInput = new JTextField();
		betInput.setBounds(113, 242, 116, 22);
		betInput.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Input Bet");
		lblNewLabel_1.setBounds(124, 221, 51, 16);
		
		JButton btnAllIn2 = new JButton("All In");	
		btnAllIn2.setBounds(380, 241, 61, 25);
		btnAllIn2.setVisible(false);
		btnAllIn = btnAllIn2;
		
		JButton btnCheck2 = new JButton("Check");
		btnCheck2.setBounds(304, 241, 67, 25);
		btnCheck2.setVisible(false);
		btnCheck = btnCheck2;
		
		JButton btnFold2 = new JButton("Fold");
		btnFold2.setBounds(453, 241, 57, 25);
		btnFold2.setVisible(false);
		btnFold = btnFold2;
		
		JButton makeBet2 = new JButton("Bet");
		makeBet2.setBounds(241, 241, 51, 25);
		makeBet2.setVisible(false);
		makeBet = makeBet2;
		
		btnAllIn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println("ALLIN " + chips);
				btnCheck2.setVisible(false);
				makeBet2.setVisible(false);
				btnFold2.setVisible(false);
				btnAllIn.setVisible(false);
				chips -= chips;
				chipsLabel.setText(chips + "");
			}
		});
		btnAllIn2.setVisible(false);
		
		btnFold2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				command = "";
				folded = true;
				out.println("FOLDED");
				btnCheck2.setVisible(false);
				makeBet2.setVisible(false);
				btnFold2.setVisible(false);
			}
		});

		btnCheck2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				command = "";
				out.println("CHECK");
				btnCheck2.setVisible(false);
				makeBet2.setVisible(false);
				btnFold2.setVisible(false);
			}
		});
	
		makeBet2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					if (Integer.parseInt(betValue.getText()) > chips || Integer.parseInt(betValue.getText()) < 0) {
						JOptionPane.showMessageDialog(null, "Bets must be within your limits and greater than 0");
	
					} else if (Integer.parseInt(betValue.getText()) < Integer.parseInt(betInfo[2])) {
						JOptionPane.showMessageDialog(null, "Bet not large enough");
					}
					else if(Integer.parseInt(betValue.getText()) == chips){
						btnCheck2.setVisible(false);
						makeBet2.setVisible(false);
						btnFold2.setVisible(false);
						btnAllIn.setVisible(false);
						out.println("ALLIN " + chips);
						chips -= chips;
						chipsLabel.setText(chips + "");
					}
					else {
						betMade = true;
					}
				}
				catch (NumberFormatException error){
					JOptionPane.showMessageDialog(null, "You must enter a whole number");
				}
				
			}
		});
		
		nameField = new JTextField();
		nameField.setBounds(5, 186, 116, 22);
		nameField.setColumns(10);
		
		JButton btnSetName = new JButton("Set Name");
		btnSetName.setBounds(133, 185, 89, 25);
		btnSetName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println("SETNAME " + nameField.getText());
				btnSetName.setVisible(false);
				nameField.setVisible(false);
			}
		});
		
		JButton swapBtn2 = new JButton("Swap");
		swapBtn2.setBounds(518, 241, 65, 25);
		swapBtn = swapBtn2;
		swapConfirm = swapBtn2;
		swapBtn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				swapCards = true;
			}
		});
		swapBtn2.setVisible(false);
		
		JTextArea infoBox2 = new JTextArea();
		infoBox2.setEditable(false);
		scrollPane.setViewportView(infoBox2);
		
		DefaultCaret caret = (DefaultCaret)infoBox2.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		infoBox = infoBox2;

		//Create a connection to the server which will then create a ClientThread class on acceptance
		try {
			socket = new Socket(address, PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));		
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (Exception e) {

		}

		cardArray[0] = Card1;
		cardArray[1] = Card2;
		cardArray[2] = Card3;
		cardArray[3] = Card4;
		cardArray[4] = Card5;

		tickBoxArray[0] = jCheckBox1;
		tickBoxArray[1] = jCheckBox2;
		tickBoxArray[2] = jCheckBox3;
		tickBoxArray[3] = jCheckBox4;
		tickBoxArray[4] = jCheckBox5;

		chipsLabel = chipsDisplay;

		betBtn = makeBet2;
		betValue = betInput;
		contentPane.setLayout(null);
		contentPane.add(swapBtn2);
		contentPane.add(nameField);
		contentPane.add(btnSetName);
		contentPane.add(Card1);
		contentPane.add(Card2);
		contentPane.add(Card3);
		contentPane.add(jCheckBox1);
		contentPane.add(jCheckBox2);
		contentPane.add(jCheckBox3);
		contentPane.add(chipsDisplay);
		contentPane.add(lblNewLabel);
		contentPane.add(lblNewLabel_1);
		contentPane.add(betInput);
		contentPane.add(makeBet2);
		contentPane.add(jCheckBox4);
		contentPane.add(Card4);
		contentPane.add(btnCheck2);
		contentPane.add(btnFold2);
		contentPane.add(jCheckBox5);
		contentPane.add(Card5);
		contentPane.add(btnAllIn2);
		contentPane.add(scrollPane);

	}
	
	public static void main(String args[]) {
		
		musicPlayer = new MusicPlayer();
		musicPlayer.setVisible(true);
		
	//Needed to play sound effects
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
        
        
	
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
		// (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}


		

					ClientGUI frame = new ClientGUI();
					frame.setVisible(true);
					frame.play();

	}
	
	public void play() {
		
		chatField = new ChatWindow(this);
		chatField.setVisible(true);
		
		musicPlayer.init();
		
		boolean buttonHit = false;

		try {

			while (true) {
				if (in.ready()) {
					dealCards(in.readLine());
					break;
				}
			}
			
			ArrayList<String> infoList = new ArrayList<>();
			
			String tempCommand = "";
			ArrayList<String> chatList = new ArrayList<>();
			while (true) {

				if (in.ready()) {
					tempCommand = in.readLine();
					if(tempCommand.startsWith("MSG")){
						
						String[] values = tempCommand.split(" ");
						String msg = "";
						
						for(int x = 0;x < values.length;x++){
							if(!values[x].equals("MSG")){
								msg += values[x] + " ";
							}
						}
						msg.trim();
						
						
						chatList.add(msg);
						tempCommand = "";
					}
					else if(!tempCommand.startsWith("INFO")) {
						command = tempCommand;
						tempCommand = "";
					}
					else if(tempCommand.startsWith("INFO")) {
						String[] infoTokens = tempCommand.split(" ");
						tempCommand = "";
						for(int x = 1; x < infoTokens.length;x++) {
							tempCommand += infoTokens[x] + " ";
						}
						infoList.add(tempCommand);
						tempCommand = "";
					}

					// JOptionPane.showMessageDialog(null, "Command received: " + command);
				}

				if (!infoList.isEmpty()) {
					for(int x = 0;x < infoList.size();x++) {
						infoBox.append(infoList.get(x) + "\n");
						infoList.remove(x);
					}
				}
				if(!chatList.isEmpty()){
					for(int x = 0;x < chatList.size();x++) {
						chatField.addMessage(chatList.get(x) + "\n");
						chatList.remove(x);
					}
				}
				else if(command.startsWith("EXIT")) {
					break;
				}
				else if(command.equals("BUSTED")) {
					
					String bip = "Sounds/Game Over.mp3";
					Media hit = new Media(new File(bip).toURI().toString());
					MediaPlayer mediaPlayer = new MediaPlayer(hit);
					
					musicPlayer.pause();
					
			        mediaPlayer.setOnEndOfMedia(new Runnable() {
			            @Override
			            public void run() {
			            	musicPlayer.resume();
			            }
			        });
					
					mediaPlayer.play();
					JOptionPane.showMessageDialog(null, "Oh no! You ran out of chips.");
					
					command = "";
				}
				else if(command.startsWith("GETCHIPS")) {
					out.println(this.chips);
					command = "";
				}
				else if(command.startsWith("NEWDEAL")) {
					
					btnCheck.setVisible(false);
					makeBet.setVisible(false);
					btnFold.setVisible(false);
					btnAllIn.setVisible(false);
					swapBtn.setVisible(false);
					
					//infoBox.setText("");
					String newHand = "";
					String[] newDealArray = command.split(" ");
					
					for(int x = 1;x < newDealArray.length;x++) {
						newHand += newDealArray[x] + " ";
					}
					newHand.trim();
					dealCards(newHand);
					folded = false;
					buttonHit = false;
					command = "";
				}
				else if(command.startsWith("ADDCHIPS")) {
					String[] newChips = command.split(" ");
					chips += Integer.parseInt(newChips[1]);
					chipsLabel.setText(chips + "");
					command = "";
				}
				else if(command.startsWith("ANTE")) {
					String[] fee = command.split(" ");
					int num = Integer.parseInt(fee[1]);
					this.chips -= num;
					this.chipsLabel.setText("" + chips);
					command = "";
				}
				else if (command.startsWith("BET")) {
					betInfo = command.split(" ");
					if (betInfo[1].equals("false")) {
						btnCheck.setVisible(true);
					} else {
						btnCheck.setVisible(false);
					}
					
					//If the players chips is less than the required amount they can only fold or go all in
					if(chips <= Integer.parseInt(betInfo[2])) {
						makeBet.setVisible(false);
						betBtn.setVisible(false);
						btnCheck.setVisible(false);
						btnFold.setVisible(true);
						btnAllIn.setVisible(true);
					}
					else {
						makeBet.setVisible(true);
						btnFold.setVisible(true);
						betBtn.setVisible(true);
						btnAllIn.setVisible(false);
					}
					
					if (betMade) {
						btnCheck.setVisible(false);
						makeBet.setVisible(false);
						btnFold.setVisible(false);
						chips -= Integer.parseInt(betValue.getText());
						chipsLabel.setText(chips + "");
						out.println("BETDONE " + betValue.getText());
						command = "";
						betMade = false;
					}

				} else if (command.startsWith("SWAP") && buttonHit == false && folded == false) {
					btnCheck.setVisible(false);
					makeBet.setVisible(false);
					btnFold.setVisible(false);
					swapConfirm.setVisible(true);
					buttonHit = true;
					command = "";
				} else if (command.equals("HIDEBUTTONS")) {
					btnCheck.setVisible(false);
					makeBet.setVisible(false);
					btnFold.setVisible(false);
					btnAllIn.setVisible(false);
					command = "";
				}
				else if(command.startsWith("HAND")) {
					String finalHand = "";
					for(int x = 0;x < playersHand.length;x++) {
						finalHand += playersHand[x] + " ";
					}
					finalHand.trim();
					out.println(finalHand);
					command = "";
				}

				if (swapCards) {
					swapCards = false;
					swapCards();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			System.exit(0);
		}
	}

	// Handles dealing a fresh hand
	public void dealCards(String cards) {

		
		//TODO - Add sound effect for dealing cards

		String[] newHand = cards.split(" ");

		String card;

		ImageIcon pic;
		Image newPic;

		for (int x = 0; x < cardArray.length; x++) {
			card = newHand[x];
			playersHand[x] = card;
			pic = new ImageIcon("Cards/PNG/" + card + ".png");
			newPic = pic.getImage().getScaledInstance(112, 153, java.awt.Image.SCALE_SMOOTH);
			pic = new ImageIcon(newPic);
			cardArray[x].setIcon(pic);
		}

	}

	// Handles the swapping of cards
	public void swapCards() {

		int swaps = 0;
		ArrayList<Integer> indexes = new ArrayList<>();

		try {
			for (int x = 0; x < tickBoxArray.length; x++) {

				if (tickBoxArray[x].isSelected()) {

					swaps++;
					indexes.add(x);
					tickBoxArray[x].setSelected(false);

				}

			}

			out.println("SWAPPING " + swaps);

			if (swaps > 0) {
				String newCards = "";
				while (true) {
					if (in.ready()) {
						newCards = in.readLine();
						break;
					}
				}

				String[] newCardArray = newCards.split(" ");

				for (int x = 0; x < indexes.size(); x++) {

					playersHand[indexes.get(x)] = newCardArray[x];

					// System.out.println("Replacing card " + indexes.get(x) + " with " +
					// newCardArray[x]);

					ImageIcon pic = new ImageIcon("Cards/PNG/" + playersHand[indexes.get(x)] + ".png");
					Image newPic = pic.getImage().getScaledInstance(112, 153, java.awt.Image.SCALE_SMOOTH);
					pic = new ImageIcon(newPic);
					cardArray[indexes.get(x)].setIcon(pic);
				}
			}

			swapConfirm.setVisible(false);
			
			repaint();
			revalidate();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void addMessage(String message) {
		out.println(message);
	}
}
