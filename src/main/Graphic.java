package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

/*
 * This class deals with translating the physics bodies and producing a graphical representation of them
 */

class Graphic extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Window window;
	private String mode;
	private static Simulator simulator;
	private Graphics2D g2d;
	private View view;
	private Body groundBody;
	private Body ballBody1, ballBody2;
	private Configuration c = new Configuration();

	private long startMillis;

	private int creatureID;

	private Color componentFillColour;
	private Color componentOutlineColour;

	private static ArrayList<Color> componentFillColours = new ArrayList<Color>();
	
	static {
		Color creatureColour1 = new Color(106, 29, 68, 100);
		Color creatureColour2 = new Color(148, 190, 229, 100);
		
		componentFillColours.add(creatureColour1);
		componentFillColours.add(creatureColour2);
	}

	//Colours and strokes
	private static final Color groundColour = new Color(140, 140, 140, 100);
	private static final Color backgroundColour = Color.WHITE;
	private static final BasicStroke componentOutlineStroke = new BasicStroke(1f);
	private static final BasicStroke groundStroke = new BasicStroke(1f);

	//Fonts
	private static final Color infoColor = new Color(0, 0, 0);
	private static final Font infoFont = new Font(Font.SANS_SERIF, Font.PLAIN,13);
	private static final BasicStroke infoStroke = new BasicStroke(1.5f);
	private static final Font creatureFont = new Font(Font.SANS_SERIF, Font.PLAIN,12);

	public Graphic(Window window, String mode) {
		this.window = window;
		this.mode = mode;
		view = new View(this);

		startMillis = System.currentTimeMillis();

		addMouseListener(new CreatureMouseAdapter());
		addMouseMotionListener(new CreatureMouseMotionAdapter());
		addMouseWheelListener(new CreatureMouseWheelListener());

		setFocusable(true);

		componentFillColour = componentFillColours.get(creatureID);

		initialiseUI();
	}

	private class CreatureMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			view.initialDragging(e.getX(), e.getY());
		}

	}

	private class CreatureMouseMotionAdapter extends MouseMotionAdapter {
		public void mouseDragged(MouseEvent e) {
			view.continueDragging(e.getX(), e.getY());
		}

	}

	private class CreatureMouseWheelListener implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			view.userScroll(e.getWheelRotation());
		}
	}

	private void initialiseUI() {
		setDoubleBuffered(true);
	}

	private void setupRenderingHints() {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	private void drawBackground() {
		g2d.setColor(backgroundColour);
		g2d.fillRect(0, 0, window.getWidth(), window.getHeight());
	}

	private void fillShape(PolygonShape shape) {
		GeneralPath path = new GeneralPath();
		Vec2 point = shape.getVertex(0);
		Position<Integer> converted = view.convertRelative(point);

		path.moveTo(converted.x, converted.y);

		for (int i = 1; i < shape.getVertexCount(); ++i) {
			point = shape.getVertex(i);
			converted = view.convertRelative(point);
			path.lineTo(converted.x, converted.y);
		}

		path.closePath();
		g2d.fill(path);
	}

	private void fillShape(PolygonShape shape, Color color) {
		g2d.setColor(color);
		fillShape(shape);
	}

	private void outlineShape(PolygonShape shape) {
		Vec2 point1 = shape.m_vertices[0];

		int i = 1;

		for (; i < shape.m_count; i++) {
			Vec2 point2 = shape.m_vertices[i];

			Position<Integer> converted1 = view.convertRelative(point1);
			Position<Integer> converted2 = view.convertRelative(point2);

			Line2D line = new Line2D.Float(converted1.x, converted1.y,
					converted2.x, converted2.y);

			g2d.draw(line);

			point1 = point2;
		}

		point1 = shape.m_vertices[i - 1];
		Vec2 point2 = shape.m_vertices[0];

		Position<Integer> converted1 = view.convertRelative(point1);
		Position<Integer> converted2 = view.convertRelative(point2);

		Line2D line = new Line2D.Float(converted1.x, converted1.y,
				converted2.x, converted2.y);

		g2d.draw(line);
	}

	private void outlineShape(PolygonShape shape, Color color) {
		g2d.setColor(color);
		outlineShape(shape);
	}

	private void drawComponent(Body body) {
		Position<Integer> position = view.convert(body.getPosition());
		g2d.translate(position.x, position.y);
		g2d.rotate(body.getAngle());
		
		for (Fixture fixture = body.getFixtureList(); fixture != null; fixture = fixture
				.getNext()) {
			Shape shape = fixture.getShape();
			fillShape((PolygonShape) shape, componentFillColour);
			g2d.setStroke(componentOutlineStroke);
			outlineShape((PolygonShape) shape, componentOutlineColour);
		}

		g2d.rotate(-body.getAngle());
		g2d.translate(-position.x, -position.y);
	}

	private void drawGround(Body body) {
		Position<Integer> position = view.convert(body.getPosition());
		Line2D line = new Line2D.Float(0, position.y, window.getWidth(),
				position.y);
		g2d.setStroke(groundStroke);
		g2d.setColor(groundColour);
		g2d.draw(line);
		g2d.fill(line);
	}

	private void drawBall1(Body body) {
		Position<Integer> position = view.convert(body.getPosition());
		Ellipse2D.Float circle = new Ellipse2D.Float(position.x - simulator.getRadius(),position.y - simulator.getRadius(),
				simulator.getRadius() * 2 * view.getZoom(),
				simulator.getRadius() * 2 * view.getZoom());
		g2d.setStroke(groundStroke);
		g2d.setColor(componentFillColours.get(0));
		g2d.draw(circle);
		g2d.fill(circle);
	}
	
	private void drawBall2(Body body) {
		Position<Integer> position = view.convert(body.getPosition());
		Ellipse2D.Float circle = new Ellipse2D.Float(position.x - simulator.getRadius(),position.y - simulator.getRadius(),
				simulator.getRadius() * 2 * view.getZoom(),
				simulator.getRadius() * 2 * view.getZoom());
		g2d.setStroke(groundStroke);
		g2d.setColor(componentFillColours.get(1));
		g2d.draw(circle);
		g2d.fill(circle);
	}

	private void drawWorld() {
		if (simulator == null) {
			return;
		}

		creatureID = 0;

		HashMap<Individual, Color> colorMapFill = new HashMap<Individual, Color>();
		HashMap<Individual, Color> colorMapOutline = new HashMap<Individual, Color>();

		for (Body body = simulator.getWorld().getBodyList(); body != null; body = body
				.getNext()) {
			UserData userData = (UserData) body.getUserData();

			if (userData instanceof ComponentUserData) {
				ComponentUserData componentUserData = (ComponentUserData) userData;

				if (colorMapFill.containsKey(componentUserData.individual)) {
					componentFillColour = colorMapFill.get(componentUserData.individual);
				} else {
					componentFillColour = componentFillColours.get(creatureID);

					colorMapFill.put(componentUserData.individual, componentFillColour);
					colorMapOutline.put(componentUserData.individual, componentFillColour.darker());

					if (++creatureID >= componentFillColours.size()) {
						creatureID = 0;
					}
				}

				drawComponent(body);
			} else if (userData instanceof Ground) {
				drawGround(body);
				groundBody = body;
			} else if (userData instanceof Ball1UserData){
				drawBall1(body);
				ballBody1 = body;
			} else {
				drawBall2(body);
				ballBody2 = body;
			}
		}
	}

	private Position<Integer> getGroundPosition() {
		return view.convert(groundBody.getPosition());
	}

	public static Position<Float> getBallPosition(Body ball) {
		Position<Float> position = new Position<Float>(0f, 0f);
		position.x = ball.getPosition().x;
		position.y = ball.getPosition().y;
		return position;
	}

	private void drawUpLeftInfo() {
		g2d.setFont(infoFont);

		int infoOffset = 0;
		FontMetrics fontMetrics = g2d.getFontMetrics();

		float seconds = (System.currentTimeMillis() - startMillis) / 1000f;

		String timeString = "Time: " + String.format("%.1f", seconds) + "s";
		String populationString = mode == "simulation" ? "Population no. " + Population.populationNumber : "";

		g2d.drawString(timeString, 10, fontMetrics.getHeight()
				+ infoOffset);
		g2d.drawString(populationString, 10,
				fontMetrics.getHeight() * 2 + infoOffset);
	}

	private void drawCreatureInfo() {
		g2d.setFont(creatureFont);
		g2d.setColor(infoColor);

		FontMetrics fm = g2d.getFontMetrics();
		Position<Integer> groundPosition = getGroundPosition();

		for (Creature creature : simulator.getCreatures()) {
			Position<Float> position = creature.getAveragePosition();
			Position<Integer> converted = view.convert(position);
			converted.y = groundPosition.y + fm.getHeight() + 10;

			String positionString = String.format("%.2f", position.x) + "m";

			g2d.drawString(positionString, converted.x - fm.stringWidth(positionString) / 2, converted.y);
		}

	}

	private void drawBallInfo(Body ball) {
		g2d.setFont(creatureFont);
		g2d.setColor(infoColor);

		FontMetrics fm = g2d.getFontMetrics();
		Position<Float> position = getBallPosition(ball);
		Position<Integer> converted = view.convert(position);
		converted.y = getGroundPosition().y + fm.getHeight() + 10;
		String positionString = String.format("%.2f", position.x - simulator.getDistanceToBall()) + "m";

		g2d.drawString(positionString, converted.x - fm.stringWidth(positionString) / 5, converted.y);

	}

	private void drawStartingPoint() {
		Position<Integer> startPoint = view.convert(new Vec2(0, 0));
		g2d.fillOval(startPoint.x - 2, startPoint.y - 2, 4, 4);
	}

	private void drawScale() {
		Position<Integer> groundPosition = getGroundPosition();
		Position<Integer> from = view.convert(new Vec2(2, 0));
		Position<Integer> to = view.convert(new Vec2(3, 0));
		int offset = 50;
		int height = view.convertRelative(20);

		Line2D legend = new Line2D.Float(from.x, groundPosition.y + offset,
				to.x, groundPosition.y + offset);

		g2d.draw(legend);

		g2d.setColor(new Color(250, 250, 250, 100));
		g2d.setStroke(new BasicStroke(1f));

		Line2D measureOne = new Line2D.Float(from.x, groundPosition.y + offset
				- height / 2, from.x, groundPosition.y + offset + height / 2);
		Line2D measureTwo = new Line2D.Float(to.x, groundPosition.y + offset
				- height / 2, to.x, groundPosition.y + offset + height / 2);

		g2d.draw(measureOne);
		g2d.draw(measureTwo);

		g2d.setColor(infoColor);

		FontMetrics fontMetrics = g2d.getFontMetrics();

		g2d.drawString("1m",
				(from.x + to.x) / 2 - fontMetrics.stringWidth("1m") / 2, from.y
				+ offset + fontMetrics.getHeight());
	}

	private void drawInfo() {
		g2d.setColor(infoColor);
		g2d.setStroke(infoStroke);

		drawUpLeftInfo();
		drawCreatureInfo();
		drawBallInfo(ballBody2);
		if(c.getLoad().equals("f")) {
			drawBallInfo(ballBody1);
		}
		drawScale();
		drawStartingPoint();
	}

	@Override
	public void paintComponent(Graphics g) {
		if (simulator == null) {
			return;
		}

		super.paintComponent(g);

		g2d = (Graphics2D) g;

		setupRenderingHints();
		drawBackground();
		drawWorld();
		drawInfo();
	}

	public Simulator getSimulator() {
		return simulator;
	}

	public static float getGravity() {
		return Simulator.getGravity();
	}
	
	public void setSimulator(Simulator simulator) {
		Graphic.simulator = simulator;
	}
	
	public void actionPerformed(ActionEvent e) {
		
	}

}