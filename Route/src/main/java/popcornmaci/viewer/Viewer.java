package popcornmaci.viewer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;
/**
 * This example demonstrate the use of different {@link TileFactory} elements.
 * 
 * @author Martin Steiger
 */
public class Viewer {
	private static final JXMapViewer mapViewer = new JXMapViewer();
	static private javax.swing.JButton startButton;
	final static private List<GeoPosition> csm = new ArrayList<>();
	final static private Set<Waypoint> wayPoints = new HashSet<>();
	static GeoPosition p1 = new GeoPosition(0, 0);
	static GeoPosition p2 = new GeoPosition(0, 0);
	static JFrame frame = new JFrame("Route");
	
	
	public static void savePoint(int i, GeoPosition gp) {
		if (i == 1) {
			p1 = gp;
			p2 = new GeoPosition(0, 0);
		} else if (i == 2) {
			p2 = gp;
		}

		frissit();
	}

	public static void frissit() {

		Set<Waypoint> waypoints = new HashSet<>();
		if (!p1.equals(new GeoPosition(0, 0))) {
			waypoints.add(new DefaultWaypoint(p1));
		}
		if (!p2.equals(new GeoPosition(0, 0))) {
			waypoints.add(new DefaultWaypoint(p2));
		}

		WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
		waypointPainter.setWaypoints(waypoints);

		List<Painter<JXMapViewer>> painters = new ArrayList<>();
		painters.add(waypointPainter);

		CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
		mapViewer.setOverlayPainter(painter);
	}

	public static void draw() {
		WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
		waypointPainter.setWaypoints(wayPoints);

		RoutePainter routePainter = new RoutePainter(csm);

		List<Painter<JXMapViewer>> painters = new ArrayList<>();
		painters.add(routePainter);
		painters.add(waypointPainter);

		CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
		mapViewer.setOverlayPainter(painter);

	}

	
	
	public static void startauto() {

		/* if ((p1.getLatitude() == 0 && p1.getLongitude() == 0)
				|| (p2.getLatitude() == 0 && p2.getLongitude() == 0)) {
			JOptionPane.showMessageDialog(frame,
					"Please add start and end point"); 
		} else {*/
			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				String cmd = "./../routing/dist/routing";

				CommandLine cmdLine = CommandLine.parse(cmd);
				cmdLine.addArgument("../debrecen.osm");
				cmdLine.addArgument(String.valueOf(p1.getLatitude()));
				cmdLine.addArgument(String.valueOf(p1.getLongitude()));
				cmdLine.addArgument(String.valueOf(p2.getLatitude()));
				cmdLine.addArgument(String.valueOf(p2.getLongitude()));

				PumpStreamHandler streamHandler = new PumpStreamHandler(
						outputStream);
				DefaultExecutor executor = new DefaultExecutor();
				executor.setStreamHandler(streamHandler);

				executor.execute(cmdLine);

				String kiir = outputStream.toString();
				String[] ki = kiir.split("\n");
				if (ki[0].equals("Route not found")) {
					JOptionPane.showMessageDialog(frame, kiir);
				} else {

					csm.clear();

					for (String a : ki) {
						String[] temp = a.split("\t");
						String[] temp2 = temp[0].split("=");
						double tempLat = Double.parseDouble(temp2[1]);
						temp2 = temp[1].split("=");
						double tempLong = Double.parseDouble(temp2[1]);
						csm.add(new GeoPosition(tempLat, tempLong));
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
/*	}*/

	
	public static void readXML(){
		
		 try {
			 csm.clear();
			
			 RouteReader rr = new RouteReader();
             InputStream inputs = new FileInputStream("../routing/dist/Route.xml");
             rr.parseXmlFile(inputs);
             
             for(RouteReader.Location loc: RouteReader.locs) {
		GeoPosition point = new GeoPosition(loc.x,loc.y);                    
                 csm.add(point);
             }
             

         	wayPoints.clear();
			wayPoints.add(new DefaultWaypoint(csm.get(0)));
			wayPoints.add(new DefaultWaypoint(csm.get(csm.size() - 1)));
			draw();
             
			inputs.close();
	}
		 
		 catch (FileNotFoundException e) {
				System.out.println(e);
			} 
		 catch (IOException e) {
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

		
		final List<TileFactory> factories = new ArrayList<TileFactory>();

		TileFactoryInfo osmInfo = new OSMTileFactoryInfo();
		TileFactoryInfo veInfo = new VirtualEarthTileFactoryInfo(
				VirtualEarthTileFactoryInfo.MAP);

		factories.add(new DefaultTileFactory(osmInfo));
		factories.add(new DefaultTileFactory(veInfo));

		// Setup JXMapViewe
		mapViewer.setTileFactory(factories.get(0));

		mapViewer.setZoom(5);
		mapViewer.setAddressLocation(new GeoPosition(47.532130, 21.624180));

		// Add interactions
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

		
		mapViewer.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {

				GeoPosition click1 = mapViewer.convertPointToGeoPosition(e
						.getPoint());
				if (e.getButton() == MouseEvent.BUTTON1)
					savePoint(1, click1);
				else if (e.getButton() == MouseEvent.BUTTON3){
					savePoint(2, click1);
					startauto();
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

		String[] tfLabels = new String[factories.size()];
	
		tfLabels[0] = factories.get(0).getInfo().getName();
		tfLabels[1] = "Visual Earth Bing";
	

		final JComboBox<String> combo = new JComboBox<String>(tfLabels);
		combo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				TileFactory factory = factories.get(combo.getSelectedIndex());
				mapViewer.setTileFactory(factory);
			}
		});

		panel.setLayout(new GridLayout());
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