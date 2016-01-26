package build.games.wraithaven;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ChipsetPreview extends JFrame{
	private final WorldBuilder worldBuilder;
	private final ChipsetImporter chipset;
	public ChipsetPreview(WorldBuilder worldBuilder, ChipsetImporter chipset){
		this.worldBuilder = worldBuilder;
		this.chipset = chipset;
		chipset.unwrap();
		init();
		addComponents();
		setVisible(true);
	}
	private void addComponents(){
		TileList tileList = new TileList(chipset.getPreviewImage());
		JScrollPane scrollPane = new JScrollPane(tileList);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		getContentPane().add(scrollPane, BorderLayout.WEST);
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout());
		JTextField textField = new JTextField(chipset.getName());
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new BorderLayout());
		panel_2.add(textField, BorderLayout.NORTH);
		panel.add(panel_2, BorderLayout.NORTH);
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout)panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_1, BorderLayout.SOUTH);
		JButton okButton = new JButton("Import");
		JButton cancelButton = new JButton("Cancel");
		panel_1.add(okButton);
		panel_1.add(cancelButton);
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				chipset.saveImages();
				Chipset c = chipset.asChipset();
				c.setName(textField.getText());
				worldBuilder.getChipsetList().addChipset(c);
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				dispose();
			}
		});
	}
	private void init(){
		setTitle("Preview Chipset");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(640, 480);
		setResizable(false);
		setLocationRelativeTo(null);
	}
}
