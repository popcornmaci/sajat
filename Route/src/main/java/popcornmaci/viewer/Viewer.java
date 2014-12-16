package popcornmaci.viewer;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;


/**
 * This example demonstrate the use of different {@link TileFactory} elements.
 * 
 * @author Martin Steiger
 */
public class Viewer {
	private static final JXMapViewer mapViewer = new JXMapViewer();
	private static final List<GeoPosition> csm = new ArrayList<>();
	private static final Set<Waypoint> wayPoints = new HashSet<>();
	static private javax.swing.JButton startButton;
	static GeoPosition point_1 = new GeoPosition(0, 0); 
	static GeoPosition point_2 = new GeoPosition(0, 0);
	static JFrame frame = new JFrame("Route");
	private static final WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
	private static final List<Painter<JXMapViewer>> painters = new ArrayList<>();
	
	

	public static void savePoint(String point_action, GeoPosition gp) {
		if (point_action == "BUTTON1") {
			point_1 = gp;
			point_2 = new GeoPosition(0, 0);
		} else if (point_action == "BUTTON3"){
			point_2 = gp;
		}

		update();
	}

	public static void update() {
		wayPoints.clear();
		
		if (!point_1.equals(new GeoPosition(0, 0))) {
			wayPoints.add(new DefaultWaypoint(point_1));
		}
		if (!point_2.equals(new GeoPosition(0, 0))) {
			wayPoints.add(new DefaultWaypoint(point_2));
		}

		waypointPainter.setWaypoints(wayPoints);
		
		painters.clear();
		painters.add(waypointPainter);
		
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
		mapViewer.setOverlayPainter(painter);
		}

	public static void draw() {
		waypointPainter.setWaypoints(wayPoints); 

		RoutePainter routePainter = new RoutePainter(csm);
		
		painters.clear();
		painters.add(waypointPainter); 
		painters.add(routePainter); 
		
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
		mapViewer.setOverlayPainter(painter);
		}

	public static void startmouse2clicked() {

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			String cmd = "./../routing/dist/routing";

			CommandLine cmdLine = CommandLine.parse(cmd);
			cmdLine.addArgument("../debrecen.osm");
			cmdLine.addArgument(String.valueOf(point_1.getLatitude()));
			cmdLine.addArgument(String.valueOf(point_1.getLongitude()));
			cmdLine.addArgument(String.valueOf(point_2.getLatitude()));
			cmdLine.addArgument(String.valueOf(point_2.getLongitude()));
		
			PumpStreamHandler streamHandler = new PumpStreamHandler(
					outputStream);
			DefaultExecutor executor = new DefaultExecutor();
			executor.setStreamHandler(streamHandler);
			executor.execute(cmdLine);
			
			String result = outputStream.toString();
			String[] result_arr = result.split("\n");
			if (result_arr[0].equals("Route not found")) {
				JOptionPane.showMessageDialog(frame, result);
			} else {

				csm.clear();
				
				for (String items : result_arr) {
					String[] temp = items.split("\t");
					String[] temp_res_1 = temp[0].split("=");
					double Latitude = Double.parseDouble(temp_res_1[1]);
					String[] temp_res_2 = temp[1].split("=");
					double Longitude = Double.parseDouble(temp_res_2[1]);
					csm.add(new GeoPosition(Latitude, Longitude));
				}

				wayPoints.clear();
				wayPoints.add(new DefaultWaypoint(csm.get(0)));
				wayPoints.add(new DefaultWaypoint(csm.get(csm.size() - 1)));
				draw();
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	
	public static void readXML() {

		try {
			csm.clear();

			XMLRouteRead r_read = new XMLRouteRead();
			InputStream inputstr = new FileInputStream(
					"../routing/dist/Route.xml");
			r_read.parseXmlFile(inputstr);
		
			for (GeoPosition loc : r_read.getLocations()) {
				csm.add(loc);
			}

			wayPoints.clear();
			wayPoints.add(new DefaultWaypoint(csm.get(0)));
			wayPoints.add(new DefaultWaypoint(csm.get(csm.size() - 1)));
			draw();

			inputstr.close();
			
		}

		catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException {

		startButton = new javax.swing.JButton();
		startButton.setText("Read from XML file");
		startButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evn) {
				readXML();
			}
		});

		/* © OpenStreetMap contributors 
		 * http://www.openstreetmap.org/copyright
		 * 
		 * Microsoft® Bing™ Maps
		 * http://www.microsoft.com/maps/product/terms.html
		 */
		
		final List<TileFactory> factories = new ArrayList<TileFactory>();
		TileFactoryInfo osmInfo = new OSMTileFactoryInfo();
		TileFactoryInfo virtearthInfo = new VirtualEarthTileFactoryInfo(
				VirtualEarthTileFactoryInfo.MAP);

		factories.add(new DefaultTileFactory(osmInfo));
		factories.add(new DefaultTileFactory(virtearthInfo));

		// Setup JXMapViewe
		mapViewer.setTileFactory(factories.get(0));
		mapViewer.setZoom(5);
		mapViewer.setAddressLocation(new GeoPosition(47.532130, 21.624180));

		// Add interactions
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(
				mapViewer));

		mapViewer.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {

				GeoPosition click = mapViewer.convertPointToGeoPosition(e
						.getPoint());
				if (e.getButton() == MouseEvent.BUTTON1)
					savePoint("BUTTON1", click);
				else if (e.getButton() == MouseEvent.BUTTON3) {
					savePoint("BUTTON3", click);
					startmouse2clicked();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// pass
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// pass
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// pass
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// pass
			}
		});

		JPanel panel = new JPanel();
		JLabel label = new JLabel("Choose a view:");

		String[] tfLabels = new String[2];

		tfLabels[0] = factories.get(0).getInfo().getName();
		tfLabels[1] = "Virtual Earth Bing";

		final JComboBox<String> combo = new JComboBox<String>(tfLabels);
		combo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				TileFactory factory = factories.get(combo.getSelectedIndex());
				mapViewer.setTileFactory(factory);
			}
		});

		panel.add(label);
		panel.add(combo);

		// Display the viewer in a JFrame
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.NORTH);
		frame.add(mapViewer);

		frame.add(startButton, BorderLayout.SOUTH);
		frame.add(mapViewer);

		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}
}
