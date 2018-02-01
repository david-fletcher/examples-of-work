import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WormPanel extends JPanel {

	private JPanel wormscore;
	private JLabel title, score;
	private JButton pause, quit;
	private PlayArea playarea;
	private Timer t;
	private int points;

	public WormPanel() {

		setLayout(new BorderLayout());

		wormscore = new JPanel(new GridLayout(1, 4));
			score = new JLabel("Score: 0");
			score.setFont(new Font("Serif", Font.ITALIC, 15));
			wormscore.add(score);

			title = new JLabel("WORM");
			title.setFont(new Font("Serif", Font.BOLD, 20));
			wormscore.add(title);

			pause = new JButton("Pause");
			pause.setFont(new Font("Dialog", Font.PLAIN, 14));
			pause.addActionListener(new PauseList());
			wormscore.add(pause);

			quit = new JButton("Quit");
			quit.addActionListener(new QuitList());
			wormscore.add(quit);
		add(wormscore, BorderLayout.SOUTH);

		playarea = new PlayArea();
		add(playarea, BorderLayout.CENTER);

		t = new Timer(10, new UpdateList());
		t.start();

		points = 0;


	}

	private class PauseList implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			playarea.switchTimerState();

			if(playarea.gameOver() == true) {
				playarea.newGame();
				score.setText("Score: 0");
				points = 0;
				playarea.worm.reset();
				pause.setFont(new Font("Dialog", Font.PLAIN, 14));
				pause.setText("Pause");
				t.start();}

			if(playarea.getTimerState() == 0) {
				pause.setFont(new Font("Dialog", Font.PLAIN, 14));
				pause.setText("Unpause");}
			else {
				pause.setFont(new Font("Dialog", Font.PLAIN, 14));
				pause.setText("Pause");}
			playarea.requestFocusInWindow();

		}

	}

	private class QuitList implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			System.exit(0);

		}

	}

	private class UpdateList implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if(playarea.gameOver() == true) {

				playarea.switchTimerState();
				score.setText("Game Over!");
				pause.setFont(new Font("Dialog", Font.PLAIN, 11));
				pause.setText("Try Again?");
				t.stop();


			}

			if(playarea.worm.getX() == playarea.dot.getX() &&
			   playarea.worm.getY() == playarea.dot.getY()) {

				points += 10;
				score.setText("Score: " + points);
				playarea.dot.move(playarea.FRAME-20,
						  playarea.FRAME-20);
				playarea.worm.addBlock();

			}

		}

	}

}
