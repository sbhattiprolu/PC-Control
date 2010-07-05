package gui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteExtension;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiDeviceDiscoveredEvent;
import wiiremotej.event.WiiDeviceDiscoveryListener;
import wiiremotej.event.WiiRemoteListener;

public class Controls extends JFrame implements WiiDeviceDiscoveryListener,
		WiiRemoteListener, ActionListener {
	JTextArea event_trace = null;
	JButton read = null;
	JButton write = null;
	JTextArea data = null;
	WiiRemote remote = null;
	JSplitPane main_panel = null;
	JPanel control_panel = null;
	JPanel sensor_panel = null;
	JProgressBar linear_acceleration_x = null;
	JProgressBar linear_acceleration_y = null;
	JProgressBar linear_acceleration_z = null;
	JProgressBar rot_acceleration_x = null;
	JProgressBar rot_acceleration_y = null;
	JProgressBar rot_acceleration_z = null;
	JLabel l1 = null;
	JLabel l2 = null;
	JLabel l3 = null;
	JLabel l4 = null;
	JLabel l5 = null;
	JLabel l6 = null;
	JButton b1 = null;
	JButton b2 = null;
	JButton b3 = null;
	JButton b4 = null;
	Controller control = null;
	Timer t;
	double offset=0.0;
	public static double ANGLE_MIN = 50;
	public static double ANGLE_MAX = 130;
	public static double ENGINE_MIN = 0;
	public static double ENGINE_MAX = 255;
	public static double ANTITORQUE_MIN = -30;
	public static double ANTITORQUE_MAX = 30;
	public boolean paused = true;

	public Controls() {
		super("Helicopter Control");
		setBounds(50, 50, 1400, 800);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container c = getContentPane();
		event_trace = new JTextArea();
		data = new JTextArea();
		read = new JButton("Read Data");
		write = new JButton("Write Data");
		main_panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		main_panel.setOneTouchExpandable(true);
		main_panel.setDividerLocation(400);
		control_panel = new JPanel();
		control_panel.setLayout(new FlowLayout());
		b1 = new JButton("Up");
		b2 = new JButton("Down");
		b3 = new JButton("Left");
		b4 = new JButton("Right");
		control_panel.add(b1);
		control_panel.add(b2);
		control_panel.add(b3);
		control_panel.add(b4);
		control_panel.add(event_trace);
		control_panel.add(read);
		control_panel.add(write);
		control_panel.add(data);
		sensor_panel = new JPanel();
		sensor_panel.setLayout(new GridLayout(2, 3, 50, 320));
		linear_acceleration_x = new JProgressBar(0, 100);
		linear_acceleration_y = new JProgressBar(0, 100);
		linear_acceleration_z = new JProgressBar(0, 100);
		rot_acceleration_x = new JProgressBar(0, 255);
		rot_acceleration_y = new JProgressBar(0, 255);
		rot_acceleration_z = new JProgressBar(0, 255);
		l1 = new JLabel("Linear Acceleration X");
		l2 = new JLabel("Linear Acceleration y");
		l3 = new JLabel("Linear Acceleration Z");
		l4 = new JLabel("Rotational Acceleration X");
		l5 = new JLabel("Rotational Acceleration Y");
		l6 = new JLabel("Rotational Acceleration Z");
		sensor_panel.add(l1);
		sensor_panel.add(linear_acceleration_x);
		sensor_panel.add(l2);
		sensor_panel.add(linear_acceleration_y);
		sensor_panel.add(l3);
		sensor_panel.add(linear_acceleration_z);
		sensor_panel.add(l4);
		sensor_panel.add(rot_acceleration_x);
		sensor_panel.add(l5);
		sensor_panel.add(rot_acceleration_y);
		sensor_panel.add(l6);
		sensor_panel.add(rot_acceleration_z);
		main_panel.add(control_panel);
		main_panel.add(sensor_panel);
		c.add(main_panel);
		read.addActionListener(this);
		write.addActionListener(this);
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		b4.addActionListener(this);
		Controller coo[] = ControllerEnvironment.getDefaultEnvironment()
				.getControllers();
		control = coo[0];
		t = new Timer(10, this);
		t.start();
	}

	@Override
	public void findFinished(int arg0) {
		// TODO Auto-generated method stub
		String s = event_trace.getText();
		event_trace.setText(s
				+ "\nthat's all the wiimotes found in the vicinity");
	}

	@Override
	public void wiiDeviceDiscovered(WiiDeviceDiscoveredEvent arg0) {
		// TODO Auto-generated method stub
		String s = event_trace.getText();
		event_trace.setText(s + "\nfound wiimote with the bluetooth address "
				+ arg0.getWiiDevice().getBluetoothAddress());
		remote = (WiiRemote) arg0.getWiiDevice();
		remote.addWiiRemoteListener(this);
		if (!remote.isAccelerometerEnabled())
			try {
				remote.setAccelerometerEnabled(true);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public void IRInputReceived(WRIREvent arg0) {
		// TODO Auto-generated method stub
		String s = event_trace.getText();
		event_trace.setText(s + "\nfound wiimote with the bluetooth address "
				+ arg0.getSource().isWritingData());

	}

	@Override
	public void accelerationInputReceived(WRAccelerationEvent arg0) {
		// TODO Auto-generated method stub
		double x_g = arg0.getXAcceleration();
		double y_g = arg0.getYAcceleration();
		double z_g = arg0.getZAcceleration();
		int x_percentage = (int) (((x_g + 6.0) * (100)) / (12));
		int y_percentage = (int) (((y_g + 6.0) * (100)) / (12));
		int z_percentage = (int) (((z_g + 6.0) * (100)) / (12));
		this.linear_acceleration_x.setValue(x_percentage);
		this.linear_acceleration_y.setValue(y_percentage);
		this.linear_acceleration_z.setValue(z_percentage);
		// System.out.println(x_percentage+"  "+y_percentage+"  "+z_percentage);
	}

	@Override
	public void buttonInputReceived(WRButtonEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void combinedInputReceived(WRCombinedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void extensionConnected(WiiRemoteExtension arg0) {
		String s = event_trace.getText();
		event_trace.setText(s + "\nextension connected ");
		// TODO Auto-generated method stub

	}

	@Override
	public void extensionDisconnected(WiiRemoteExtension arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void extensionInputReceived(WRExtensionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void extensionPartiallyInserted() {
		String s = event_trace.getText();
		event_trace.setText(s + "\nextension wasnt properly inserted");
		// TODO Auto-generated method stub

	}

	@Override
	public void extensionUnknown() {
		String s = event_trace.getText();
		event_trace.setText(s + "\nunknown extension inserted");
		// TODO Auto-generated method stub

	}

	@Override
	public void statusReported(WRStatusEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void actionPerformed(ActionEvent arg0) {
		try {
			if (remote != null) {
				if (arg0.getSource() == t) {
					control.poll();
					Component components[] = control.getComponents();
					double roll_input=0.0;
					double pitch_input=0.0;
					double engine_power_input=0.0;
					double tail_power_input=0.0;
					double roll_angle=0.0;
					double pitch_angle=0.0;
					double engine_power=0.0;
					double tail_power=0.0;
					boolean first=false;
					for (Component component : components) {
						if (component.getName().equals("Top")
								&& component.getPollData() == 1.0 && (!paused)) {
							 System.out.println("shutdown");
							this.paused = true;
							byte data1[] = new byte[1];
							byte address[] = new byte[4];
							address[0] = 0x04;
							address[1] = (byte) 0xa4;
							address[2] = 0x00;
							address[3] = 0x01;
							data1[0] = 0x09;
							remote.writeData(address, data1);
							first=false;
						}
						if (!paused) {
							first=true;
							if (component.getName().equals("z")) {
								roll_input = component.getPollData();
							}
							if (component.getName().equals("rz")) {
								pitch_input = -1.0 * component.getPollData();
							}
							if (component.getName().equals("y")) {
								engine_power_input = -1.0* component.getPollData();
							}
							if (component.getName().equals("x")) {
								tail_power_input = component.getPollData();
							}
						}
						if (component.getName().equals("Thumb")
								&& component.getPollData() == 1.0 && paused) {
							 System.out.println("startup");
							this.paused = false;
							byte data1[] = new byte[1];
							byte address[] = new byte[4];
							address[0] = 0x04;
							address[1] = (byte) 0xa4;
							address[2] = 0x00;
							address[3] = 0x01;
							data1[0] = 0x08;
							remote.writeData(address, data1);
						}
						if(component.getName().equals("Pinkie") && component.getPollData()==1.0 && (paused)){

							offset-=2.0;
						}
						if(component.getName().equals("Top 2") && component.getPollData()==1.0 && (paused)){

							offset+=2.0;
						}
					}
					 if(first){
						  roll_angle=0.5*(roll_input+1)*(ANGLE_MAX-ANGLE_MIN)+ANGLE_MIN;
						  pitch_angle=0.5*(pitch_input+1)*(ANGLE_MAX-ANGLE_MIN)+ANGLE_MIN;
						  engine_power=0.5*(engine_power_input+1)*(ENGINE_MAX-ENGINE_MIN)+ENGINE_MIN;
						  tail_power=engine_power-(offset+(0.5*(tail_power_input+1)*(ANTITORQUE_MAX-ANTITORQUE_MIN)+ANTITORQUE_MIN));
					      first=false;
							byte data1[] = new byte[8];
							byte address[] = new byte[4];
							address[0] = 0x04;
							address[1] = (byte) 0xa4;
							address[2] = 0x00;
							address[3] = 0x01;
							data1[0] = 0x01;
							data1[2] = 0x02;
							data1[4] = 0x03;
							data1[6] = 0x04;
							int temp=(int)roll_angle;
							data1[1] =(byte) (temp>127?temp-131:temp);
							temp=(int)pitch_angle;
							data1[3] = (byte) (temp>127?temp-131:temp);
							temp=(int)engine_power;
							data1[5] = (byte) (temp>127?temp-256:temp);
							temp=(int)tail_power;
							data1[7] = (byte) (temp>127?temp-256:temp);
							remote.writeData(address, data1);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/*
	 * public void actionPerformed(ActionEvent arg0) { // TODO Auto-generated
	 * method stub double roll_angle=previous_roll_angle; double
	 * pitch_angle=previous_pitch_angle; double
	 * engine_power=previous_engine_power; double
	 * tail_power=previous_tail_rotor_power; if (arg0.getSource() == t) {
	 * control.poll(); double roll_input=0.0; double pitch_input=0.0; double
	 * engine_power_input=0.0; double tail_power_input=0.0; Component
	 * components[] = control.getComponents(); for (Component temp : components)
	 * { if (temp.getName().equals("z")){ roll_input=temp.getPollData(); }
	 * if(temp.getName().equals("rz")){ pitch_input=-1.0*temp.getPollData(); }
	 * if(temp.getName().equals("y")){
	 * engine_power_input=-1.0*temp.getPollData(); }
	 * if(temp.getName().equals("x")){ tail_power_input=temp.getPollData(); }
	 * 
	 * }
	 * 
	 * roll_angle=0.5*(roll_input+1)*(ANGLE_MAX-ANGLE_MIN)+ANGLE_MIN;
	 * pitch_angle=0.5*(pitch_input+1)*(ANGLE_MAX-ANGLE_MIN)+ANGLE_MIN;
	 * engine_power
	 * =0.5*(engine_power_input+1)*(ENGINE_MAX-ENGINE_MIN)+ENGINE_MIN;
	 * tail_power
	 * =engine_power-(0.5*(tail_power_input+1)*(ANTITORQUE_MAX-ANTITORQUE_MIN
	 * )+ANTITORQUE_MIN); }
	 * 
	 * if (remote == null) { String s = event_trace.getText(); //
	 * event_trace.setText(s + "\n" // +
	 * "no wiimote is presently connected to the pc"); } else {
	 * if(previous_roll_angle!=roll_angle || previous_pitch_angle!=pitch_angle
	 * || previous_engine_power!=engine_power){ int temp=0; byte data1[] = new
	 * byte[8]; data1[0]=0x01; data1[2]=0x02; data1[4]=0x03; data1[6]=0x04;
	 * temp=(int)roll_angle; data1[1]=(byte) (temp>127?temp-131:temp);
	 * temp=(int)pitch_angle; data1[3]=(byte)(temp>127?temp-131:temp);
	 * temp=(int)engine_power; data1[5]=(byte)(temp>127?temp-256:temp);
	 * temp=(int)tail_power; data1[7]=(byte)(temp>127?temp-256:temp);
	 * System.out.
	 * println("roll input "+data1[1]+" pitch input "+data1[3]+" engine power "
	 * +data1[5]); byte address[] = new byte[4]; address[0] = 0x04; address[1] =
	 * (byte) 0xa4; address[2] = 0x00; address[3] = 0x01; try {
	 * remote.writeData(address, data1); } catch (IllegalArgumentException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * } catch (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } previous_roll_angle=roll_angle;
	 * previous_pitch_angle=pitch_angle; previous_engine_power=engine_power;
	 * previous_tail_rotor_power=tail_power; } } }
	 */
}