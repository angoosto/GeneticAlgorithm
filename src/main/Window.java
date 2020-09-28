package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

/*
 * This class creates the window in which the application runs
 */

@SuppressWarnings("serial")
public class Window extends JFrame {
    Graphic graphic;
    private JPanel panel1;
    private JPanel panel2;
    private JTextArea textArea;
    private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
  
    public Window(int width, int height, String mode) {
        setTitle("Creature Evolution");
        setSize(width, height);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setPanel2();
        add(panel2, BorderLayout.PAGE_END);
        
        setPanel1();
        add(panel1, BorderLayout.LINE_START);
        
        graphic = new Graphic(this, mode);
        add(graphic, BorderLayout.CENTER);
        graphic.revalidate();
    }

    public void updateDisplay() {
        graphic.repaint();
    }

    public void setSimulator(Simulator simulator) {
        graphic.setSimulator(simulator);
    }
    
    private void setPanel1() {
    	panel1 = new JPanel();
    	panel1.setLayout(new GridLayout(12, 1));
    	panel1.add(getGravityBox());
    	panel1.add(getLoadBox());
    	panel1.add(getMutationBox());
    	panel1.add(getSaveCheck());
    	JLabel label = new JLabel("Population Size:", JLabel.LEFT);
    	panel1.add(label);
    	panel1.add(getCreatureSlider());
    	panel1.add(getResetButton());
    }
    
    private void setPanel2() {
    	panel2 = new JPanel();
    	setTextArea(panel2);
    }
    
    public void appendString(String s) {
    	textArea.append(s);
    }
    
    private void setTextArea(JPanel panel) {
    	textArea = new JTextArea();
    	textArea.setEditable(false);
    	textArea.setFont(font);
//    	textArea.setPreferredSize(new Dimension(1000, 100));
    	JScrollPane jsp = new JScrollPane(textArea);
    	jsp.setPreferredSize(new Dimension(1000, 100));
    	panel.add(jsp);
    }
    
    private Component getResetButton() {
    	JButton resetButton = new JButton("Reset");
    	resetButton.setFont(font);
    	resetButton.setPreferredSize(new Dimension(100, 30));
    	
    	resetButton.addActionListener(e -> {
			try {
				Main.restart(new Thread());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
			
			
    	return resetButton;
    }
    
    private Component getGravityBox() {
    	String[] gravities = {"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto"};
    	JComboBox<String> gravitiesBox = new JComboBox<String>(gravities);
    	gravitiesBox.setFont(font);
    	gravitiesBox.setRenderer(new GravityBoxRenderer("Set Gravity"));
    	gravitiesBox.setSelectedIndex(-1);
    	gravitiesBox.setPreferredSize(new Dimension(150, 30));
    	
    	gravitiesBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				String gravity = (String) cb.getSelectedItem();
				graphic.getSimulator().setGravity(gravity);
				
			}
		});
    	
    	return gravitiesBox;
    }
    
    private Component getLoadBox() {
    	String[] loads = {"0","1", "2", "3", "4", "5", "6", "7", "8", "9"};
    	JComboBox<String> loadBox = new JComboBox<String>(loads);
    	loadBox.setFont(font);
    	loadBox.setRenderer(new GravityBoxRenderer("Select Save"));
    	loadBox.setSelectedIndex(-1);
    	loadBox.setPreferredSize(new Dimension(150, 30));
    	
    	return loadBox;
    }
    
    private Component getSaveCheck() {
    	JCheckBox saveBox = new JCheckBox("Save");
    	saveBox.setSelected(false);
    	saveBox.setFont(font);
    	
    	return saveBox;
    }
    
    private Component getMutationBox() {
    	String[] mutations = {"0%", "1%", "2%", "3%", "4%", "5%", "10%", "20%", "30%", "40%", "50%", "75%", "100%"};
    	JComboBox<String> mutationBox = new JComboBox<>(mutations);
    	mutationBox.setRenderer(new GravityBoxRenderer("Mutation Rate"));
    	mutationBox.setSelectedIndex(-1);
    	mutationBox.setPreferredSize(new Dimension(150, 30));
    	
    	return mutationBox;
    }
    
    private Component getCreatureSlider() {
    	JSlider slider = new JSlider();
    	
    	slider.setMaximum(250);
    	slider.setMinimum(10);
    	slider.setPaintTicks(true);
    	slider.setMajorTickSpacing(50);
    	slider.setPaintLabels(true);
    	slider.setValue(150);
    	
    	return slider;
    }
    
    private class GravityBoxRenderer extends JLabel implements ListCellRenderer<String> {

    	private String title;
    	
    	public GravityBoxRenderer(String title) {
			this.title = title;
		}
    	
		@Override
		public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (index==-1 && value==null) {
				setText(title);
			} else {
				setText(value.toString());
			}
			return this;
		}
    	
    }

}
