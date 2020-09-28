package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import javax.swing.JPanel;


public class Graph extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Graph() {
		setSize(600, 400);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 =  (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Line2D line  = new Line2D.Double(50, 50, 100, 100);
		g2.draw(line);
	}
}