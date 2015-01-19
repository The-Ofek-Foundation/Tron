/*	Ofek Gila
	May 29th, 2014
	Tron.java
	This program will play the game tron from the movie
*/

import java.awt.*;			// Imports
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Tron extends JApplet	{
	public JFrame frame;
	public static void main(String[] pumpkins) {
		Tron T = new Tron();
		T.run();
	}
	public void run(){
		frame = new JFrame("Tron");	// ask why I extend JApplet or implement all of those things ^_^
		frame.setContentPane(new TronPanel());
		frame.setSize(1500, 750);		// Sets size of frame
		frame.setResizable(true);						// Makes it so you can't resize the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public void init()	{
		setContentPane(	new TronPanel());
	}
}
class TronPanel	extends JPanel implements ActionListener, KeyListener, MouseListener	{
	int blockSize = 15;
	int width, height;
	char[][] field;
	int d1, d2;
	int md1, md2;
	boolean initial = true;
	Graphics g;
	Timer t;
	Color blockColor;
	boolean readyToDraw = false;
	int count;
	int corrosiveDR, corrosiveSR, corrosiveDPR;
	boolean corrosiveR, corrosiveB;
	JPanel sliders, gametypes;
	JSlider speed, boxsize, corrosivedr, corrosivesr, corrosivedpr;
	JCheckBox corrosionpickup, doublemove;
	JComboBox chooseslider;
	boolean AIB;
	boolean CoPiUp, DoMo;
	CardLayout cl;
	TronPanel()	{
		setLayout(new BorderLayout());
		t = new Timer(100, this);
		addKeyListener(this);
		addMouseListener(this);
		corrosiveDR = 100;
		corrosiveSR = 15;
		corrosiveDPR = 50;
		AIB = false;
		CoPiUp = false;
		DoMo = false;
		//Timer f = new Timer(2000, new FocusTimer());
		//f.start();
	}
	class FocusTimer implements ActionListener	{
		public void actionPerformed(ActionEvent e)	{
			if (!hasFocus())	requestFocus();
		}
	}
	class MenuItems implements ChangeListener, ActionListener {
		public void stateChanged(ChangeEvent e)	{	// changes text when moved
			Object source = e.getSource();
			if (source == speed)	{
				t.setDelay(speed.getValue());
			}
			if (source == boxsize)	{
				blockSize = boxsize.getValue();
			}
			if (source == corrosivedr)	{
				corrosiveDR = corrosivedr.getValue();
			}
			if (source == corrosivesr)	{
				corrosiveSR = corrosivesr.getValue();
			}
			if (source == corrosivedpr)	{
				corrosiveDPR = corrosivedpr.getValue();
			}
		}
		public void actionPerformed(ActionEvent	e)	{
			Object source = e.getSource();
			if (source == chooseslider)	{
				cl.show(sliders, chooseslider.getSelectedItem().toString());
			}
			if (source == corrosionpickup)	{
				CoPiUp = corrosionpickup.isSelected();
			}
		}
	}
	void createSpeed()	{
		speed = new JSlider(0, 400, 100);
		speed.setMajorTickSpacing(50);
		speed.setMinorTickSpacing(10);
		speed.setPaintTicks(true);
		speed.setPaintLabels(true);
		speed.addChangeListener(new MenuItems());
	}
	void createChooseSlider()	{
		chooseslider = new JComboBox();
		chooseslider.addItem("Speed");
		chooseslider.addItem("Box Size");
		chooseslider.addItem("Corrosive Drop Wait");
		chooseslider.addItem("Corrosive Spread Wait");
		//chooseslider.addItem("Corrosion Wait");
		chooseslider.addItem("Toggle Corrosion Pickup");
		chooseslider.addItem("Game Type");
		chooseslider.addActionListener(new MenuItems());
	}
	void createBoxSize()	{
		boxsize = new JSlider(5, 50, blockSize);
		boxsize.setMajorTickSpacing(10);
		boxsize.setMinorTickSpacing(2);
		boxsize.setPaintTicks(true);
		boxsize.setPaintLabels(true);
		boxsize.addChangeListener(new MenuItems());
	}
	void createCorrosiveDropRate()	{
		corrosivedr = new JSlider(1, 501, corrosiveDR);
		corrosivedr.setMajorTickSpacing(100);
		corrosivedr.setMinorTickSpacing(25);
		corrosivedr.setPaintTicks(true);
		corrosivedr.setPaintLabels(true);
		corrosivedr.addChangeListener(new MenuItems());
	}
	void createCorrosiveSpreadRate()	{
		corrosivesr = new JSlider(1, 101, corrosiveSR);
		corrosivesr.setMajorTickSpacing(20);
		corrosivesr.setMinorTickSpacing(5);
		corrosivesr.setPaintTicks(true);
		corrosivesr.setPaintLabels(true);
		corrosivesr.addChangeListener(new MenuItems());
	}
	void createCorrosiveDespawnRate()	{
		corrosivedpr = new JSlider(1, 101, corrosiveDPR);
		corrosivedpr.setMajorTickSpacing(25);
		corrosivedpr.setMinorTickSpacing(5);
		corrosivedpr.setPaintTicks(true);
		corrosivedpr.setPaintLabels(true);
		corrosivedpr.addChangeListener(new MenuItems());
	}
	void createCorrosionPickUp()	{
		corrosionpickup = new JCheckBox("Enable Corrosion Pickup");
		corrosionpickup.addActionListener(new MenuItems());
	}
	void createMenuPanel()	{
		JMenuBar pane = new JMenuBar();
		pane.setPreferredSize(new Dimension(width, 40));

		sliders = new JPanel();
		cl = new CardLayout();
		sliders.setLayout(cl);

		gametypes = new GameTypePanel();

		createSpeed();
		createBoxSize();
		createCorrosiveDropRate();
		createCorrosiveSpreadRate();
		createCorrosiveDespawnRate();
		createCorrosionPickUp();

		createChooseSlider();

		sliders.add(speed, "Speed");
		sliders.add(boxsize, "Box Size");
		sliders.add(corrosivedr, "Corrosive Drop Wait");
		sliders.add(corrosivesr, "Corrosive Spread Wait");
		//sliders.add(corrosivedpr, "Corrosion Wait");
		sliders.add(corrosionpickup, "Toggle Corrosion Pickup");
		sliders.add(gametypes, "Game Type");
		pane.add(sliders);
		pane.add(chooseslider);
		add(pane, BorderLayout.NORTH);
	}
	class GameTypePanel extends JPanel implements ActionListener	{
		JRadioButton normal, fight, disco, corrosionlover, real, easy;
		GameTypePanel()	{
			ButtonGroup BG = new ButtonGroup();
			setLayout(new FlowLayout());
			normal = new JRadioButton("Normal");
			normal.setSelected(true);
			normal.addActionListener(this);

			fight = new JRadioButton("Fight!");
			fight.setSelected(false);
			fight.addActionListener(this);

			disco = new JRadioButton("Disco!");
			disco.setSelected(false);
			disco.addActionListener(this);

			corrosionlover = new JRadioButton("Corrosion Lover!");
			corrosionlover.setSelected(false);
			corrosionlover.addActionListener(this);

			real = new JRadioButton("Real Tron!");
			real.setSelected(false);
			real.addActionListener(this);

			easy = new JRadioButton("Easy");
			easy.setSelected(false);
			easy.addActionListener(this);

			BG.add(normal);
			BG.add(fight);
			BG.add(disco);
			BG.add(corrosionlover);
			BG.add(real);
			BG.add(easy);

			add(normal);
			add(easy);
			add(fight);
			//add(disco);
			add(corrosionlover);
			add(real);
		}
		public void actionPerformed(ActionEvent e)	{
			Object source = e.getSource();
			if (source == normal)			setSliderValues(100, 15, 100,   15, 50, false, false);
			if (source == fight)			setSliderValues(75,  13, 1,     2,  50, false, false);
			if (source == disco)			setSliderValues(100, 15, 1,     1,  50, false, false);
			if (source == corrosionlover)	setSliderValues(100, 15, 5,     5,  20, true , false);
			if (source == real)				setSliderValues(30,  10, 99999, 1,  1,  false, true);
			if (source == easy)				setSliderValues(150, 25, 100,   20, 40, true,  false);
		}
	}
	void setSliderValues(int Speed, int BoxSize, int DR, int SR, int DPR, boolean pickup, boolean doublemove)	{
		speed.setValue(Speed);
		boxsize.setValue(BoxSize);
		corrosivedr.setValue(DR);
		corrosivesr.setValue(SR);
		corrosiveDPR = DPR;
		CoPiUp = pickup;
		DoMo = doublemove;
	}
	void constructor()	{
		if (!hasFocus())	requestFocus();
		width = getWidth();
		height = getHeight() - 40;
		s = "";
		field = new char[width / blockSize - (width / blockSize) % 2 - 1][height / blockSize - (height / blockSize) % 2 - 1];
		for (int i = 0; i < field.length; i++)
			for (int a = 0; a < field[i].length; a++)
				field[i][a] = 'w';
		d1 = 0;
		d2 = 1;
		count = 0;
		corrosiveR = corrosiveB = false;
		field[0][field[0].length / 2] = 'R';
		field[field.length - 1][field[0].length / 2] = 'B';
		if (t.isRunning()) t.stop();
		t.start();
		readyToDraw = true;
	}
	public void paintComponent(Graphics a)	{
		super.paintComponent(a);
		g = a;
		if (initial)	{
			initial = false;
			width = getWidth();
			height = getHeight();
			createMenuPanel();
			constructor();
		}
		if (readyToDraw)	drawBoard();
	}
	void drawBoard()	{
		for (int i = 0; i < field.length; i++)
			for (int a = 0; a < field[i].length; a++)	{
				if (field[i][a] == 'w')							blockColor = new Color(0, 0, 0);
				if (field[i][a] == 'r' || field[i][a] == 'R')	blockColor = new Color(255, 0 ,0);
				if (field[i][a] == 'b' || field[i][a] == 'B')	blockColor = new Color(0, 0, 255);
				if (field[i][a] == 'C')							blockColor = new Color(0, 255, 0);
				if (field[i][a] == 'c')							blockColor = new Color(0, 100, 0);
				g.setColor(blockColor);
				g.fillRect(i * blockSize, a * blockSize + 40, blockSize, blockSize);
			}
		if (!s.equals(""))	{
			g.setColor(Color.green);
			g.setFont(new Font("Arial", Font.BOLD, 60));
			g.drawString(s, 30, height / 2 + 10);
		}

	}
	boolean safe(int x, int y)	{
		if (x < 0 || x > field.length - 1)				return false;
		if (y < 0 || y > field[0].length - 1)			return false;
		if (field[x][y] == 'w') 						return true;
		if (field[x][y] == 'C' && CoPiUp)				return true;

		return false;
	}
	String s;
	char winner;
	void endGame(int x, int y, char w)	{
		winner = getWinner(x, y, w);
		switch (winner)	{
			case 'b':
			case 'B':	s = "Blue wins!!!";	break;
			case 'r':
			case 'R': s = "Red wins!!!";	break;
			case 'T': s = "Tie game!!!";	break;
		}
		t.stop();
		repaint();
	}
	char getWinner(int x, int y, char w)	{
		winner = getThingHere(x, y);
		if (winner == 'w') return w;
		switch (md1)	{
			case 0:	if (md2 == 1 && winnerIsCaps(winner)) return 'T';	break;
			case 1:	if (md2 == 0 && winnerIsCaps(winner)) return 'T';	break;
			case 2:	if (md2 == 3 && winnerIsCaps(winner)) return 'T';	break;
			case 3:	if (md2 == 2 && winnerIsCaps(winner)) return 'T';	break;
		}
		return w;
	}
	char getThingHere(int x, int y)	{
		try {
			return field[x][y];
		}	catch (java.lang.ArrayIndexOutOfBoundsException e)	{
			return 'w';
		}
	}
	boolean winnerIsCaps(char winner)	{
		if (winner == 'R') return true;
		if (winner == 'B') return true;
		return false;
	}
	public void actionPerformed(ActionEvent e)	{
		boolean m1 = false, m2 = false;
		for (int i = 0; i < field.length; i++)
			for (int a = 0; a < field[i].length; a++)	{
				if (field[i][a] == 'R' && !m1)	{
					int xt = i, yt = a;
					if (corrosiveR) field[i][a] = 'c';
					else field[i][a] = 'r';
					m1 = true;
					int in = 1;
					if (DoMo) in = 2;
					switch (d1)	{
						case 0: xt+=in;	break;
						case 1: xt-=in;	break;
						case 2: yt+=in;	break;
						case 3:	yt-=in;	break;
						default:	System.out.println("Boso Alert!");
					}
					if (safe(xt, yt))	{
						if (field[xt][yt] == 'C') corrosiveR = true;
						field[xt][yt] = 'R';
						if (in == 2)	{
							switch (d1)	{
								case 0: field[xt-1][yt] = 'r';	break;
								case 1: field[xt+1][yt] = 'r';	break;
								case 2: field[xt][yt-1] = 'r';	break;
								case 3:	field[xt][yt+1] = 'r';	break;
							}
						}
					}
					else {
						endGame(xt, yt, 'b');
						return;
					}
					md1 = d1;
				}
				if (field[i][a] == 'B' && !m2)	{
					if (corrosiveB) field[i][a] = 'c';
					else field[i][a] = 'b';
					m2 = true;
					int xt = i, yt = a;
					if (AIB) d2 = AIMove(i, a);
					int in = 1;
					if (DoMo) in = 2;
					switch (d2)	{
						case 0: xt+=in;	break;
						case 1: xt-=in;	break;
						case 2: yt+=in;	break;
						case 3:	yt-=in;	break;
						default:	System.out.println("Boso Alert!");
					}
					if (safe(xt, yt))	{
						if (field[xt][yt] == 'C') corrosiveB = true;
						field[xt][yt] = 'B';
						if (in == 2)	{
							switch (d2)	{
								case 0: field[xt-1][yt] = 'b';	break;
								case 1: field[xt+1][yt] = 'b';	break;
								case 2: field[xt][yt-1] = 'b';	break;
								case 3:	field[xt][yt+1] = 'b';	break;
							}
						}
					}
					else 	{
						endGame(xt, yt, 'r');
						return;
					}
					md2 = d2;
				}
			}
		count++;
		if (count % corrosiveDPR == 0)	removeCorrosivness();
		if (count % corrosiveSR == 0)	spreadCorrosive();
		if (count % corrosiveDR == 0)	placeCorrosive();
		repaint();
	}
	int AIMove(int x, int y)	{
		int md = 0;
		return md;
	}
	void removeCorrosivness()	{
		corrosiveB = corrosiveR = false;
	}
	void spreadCorrosive()	{
		for (int i = 0; i < field.length; i++)
			for (int a = 0; a < field[i].length; a++)
				if (field[i][a] == 'c') {
					spreadCorrosion(i, a);
					field[i][a] = 'w';
				}
				else if (field[i][a] == 'C' && !CoPiUp)	{
					spreadCorrosion(i, a);
					field[i][a] = 'w';
				}
		for (int i = 0; i < field.length; i++)
			for (int a = 0; a < field[i].length; a++)
				if (field[i][a] == 't')
					field[i][a] = 'c';
	}
	void spreadCorrosion(int x, int y)	{
		if (x > 0)					tryCorrodeHere(x-1, y);
		if (x < field.length - 1)	tryCorrodeHere(x+1, y);
		if (y > 0)					tryCorrodeHere(x, y-1);
		if (y < field[x].length - 1)tryCorrodeHere(x, y+1);
	}
	void tryCorrodeHere(int x, int y)	{
		if (field[x][y] == 'r' || field[x][y] == 'b') field[x][y] = 't';
	}
	void placeCorrosive()	{
		int ran1, ran2;
		do {
			ran1 = (int)(Math.random() * field.length);
			ran2 = (int)(Math.random() * field[ran1].length);
		}	while (!okToPlaceCorrosive(ran1, ran2));
		field[ran1][ran2] = 'C';
	}
	boolean okToPlaceCorrosive(int x, int y)	{
		if (field[x][y] != 'w')								return false;
		if (x > 0) if (noFace(x-1, y))						return true;
		if (x < field.length - 1) if (noFace(x+1, y))		return true; 
		if (y > 0)	if (noFace(x, y-1))						return true;
		if (y < field[x].length - 1)	if (noFace(x, y+1))	return true;
		return false;
	}
	boolean noFace(int x, int y)	{
		if (field[x][y] == 'B')	return false;
		if (field[x][y] == 'R')	return false;
		return true;
	}
	public void keyTyped(KeyEvent evt) {	
		char key = evt.getKeyChar();
		if (key == 'p')	{
			if (t.isRunning()) t.stop();
			else t.start();
		}
		if (key == 'a' && md1 != 0)			d1 = 1;
		else if (key == 'w' && md1 != 2)	d1 = 3;
		else if (key == 's' && md1 != 3)	d1 = 2;
		else if (key == 'd' && md1 != 1)	d1 = 0;
	}
	public void keyPressed(KeyEvent evt) {
		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_SPACE)		constructor();
		else if (key == KeyEvent.VK_LEFT && md2 != 0)	d2 = 1;
		else if (key == KeyEvent.VK_UP && md2 != 2)		d2 = 3;
		else if (key == KeyEvent.VK_DOWN && md2 != 3)	d2 = 2;
		else if (key == KeyEvent.VK_RIGHT && md2 != 1)	d2 = 0;	

	}
	public void keyReleased(KeyEvent evt) {	}
	public void mouseDragged(MouseEvent evt)	{	}
	public void mouseMoved(MouseEvent evt)	{	}
	public void mouseEntered(MouseEvent evt) {	} 
	public void mousePressed(MouseEvent evt) {	
		requestFocus();
	}
    public void mouseExited(MouseEvent evt) {	} 
    public void mouseReleased(MouseEvent evt) {  } 
    public void mouseClicked(MouseEvent evt) { }
}