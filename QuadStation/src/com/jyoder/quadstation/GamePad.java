package com.jyoder.quadstation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
 
public class GamePad {
	private static Controller controller;
	public static final Axis ROLL = new Axis(Identifier.Axis.X, false, false,0.04); //roll
	public static final Axis PITCH = new Axis(Identifier.Axis.Y, true, false,0.04); //pitch
	public static final Axis YAW = new Axis(Identifier.Axis.Z, true, false,0.04); //yaw
	public static final Axis THROTTLE = new Axis(Identifier.Axis.RY, true, true,0.04); //throttle
	//public static final Axis AUX1 = new Axis(Identifier.Axis.Z, false, false); //???
	public static class Axis {
		Identifier id;
		boolean negate = false;
		boolean throttley = false;
		double deadband = 0;
		public Axis(Identifier id, boolean negate, boolean throttley, double deadband) {
			this.id = id;
			this.negate = negate;
			this.throttley = throttley;
			this.deadband = deadband;
		}
	}
	public static final Button AUX1 = new Button(Identifier.Button._0, true);
	public static final Button AUX2 = new Button(Identifier.Button._1, true);
	public static final Button AUX3 = new Button(Identifier.Button._2, true);
	
	public static class Button {
	  Identifier id;
	  boolean toggle;
	  boolean wasPressed;
	  boolean val;
	  public Button(Identifier id, boolean toggle) {
	    this.id = id;
	    this.toggle = toggle;
	  }
	  
	  public void poll() {
	    if(toggle) {
	      if (isPressed() && !wasPressed) { //if pressed and wasn't before, toggle.
	        val = !val;
	      }
	      wasPressed = isPressed();
	    }
	  }
	  
	  private boolean isPressed() {
	    return (controller.getComponent(id).getPollData() == 1.0f);
	  }
	  
	  public boolean getVal() {
	    if (toggle) {
	      return val;
	    } else {
	      return isPressed();
	    }
	  }
	}
	
	public static synchronized void initialize() {
		//dirty dirty hack to get JInput to refresh the controllers list
		if (System.getProperty("os.name").equals("Windows 7")) //&&
			   // System.getProperty("os.arch").equals("amd64"))
			        try {
			            Class<?> clazz = Class.forName("net.java.games.input.DefaultControllerEnvironment");
			            Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
			            defaultConstructor.setAccessible(true); // set visibility to public

			            Field defaultEnvironementField = ControllerEnvironment.class.getDeclaredField("defaultEnvironment");
			            defaultEnvironementField.setAccessible(true);
			            defaultEnvironementField.set(ControllerEnvironment.getDefaultEnvironment(), defaultConstructor.newInstance());
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
	
	    for(int i =0;i<ca.length;i++){ 
	
	        /* Get the name of the controller */
	   //     System.out.println(ca[i].getName());
	        
	   //     System.out.println("Type: "+ca[i].getType().toString());
	        if (ca[i].getType() == Controller.Type.GAMEPAD)  //find the first GamePad
	        	controller = ca[i]; //and store it
	    }
    }
	public static synchronized void poll() {
		if(controller != null) {
			boolean success = controller.poll();
			if(!success) {
			  controller = null;
			  return;
			}
			AUX1.poll();
			AUX2.poll();
			AUX3.poll();
		} 
	}
	
	public static synchronized boolean getButton(Identifier id) {
		if(controller != null) return controller.getComponent(id).getPollData() > 0.5;
		else return false;
	}
	
	public static synchronized float getAxis(Axis axis) {
		if(controller != null) {
			float val = controller.getComponent(axis.id).getPollData();
			if(axis.negate) val *= -1.0f;
			if(axis.throttley) val = Math.abs(val);
			if(Math.abs(val) <= axis.deadband) {
				val = 0;
			}
			return val;
		}
		else return 0.0f;
	}
	
	public static boolean isConnected() {
		return (controller != null);
	}
	
		
	
}
