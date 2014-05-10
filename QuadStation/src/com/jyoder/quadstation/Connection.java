package com.jyoder.quadstation;

public class Connection extends Thread {
  private static final int
  MSP_IDENT                =100,
  MSP_STATUS               =101,
  MSP_RAW_IMU              =102,
  MSP_SERVO                =103,
  MSP_MOTOR                =104,
  MSP_RC                   =105,
  MSP_RAW_GPS              =106,
  MSP_COMP_GPS             =107,
  MSP_ATTITUDE             =108,
  MSP_ALTITUDE             =109,
  MSP_ANALOG               =110,
  MSP_RC_TUNING            =111,
  MSP_PID                  =112,
  MSP_BOX                  =113,
  MSP_MISC                 =114,
  MSP_MOTOR_PINS           =115,
  MSP_BOXNAMES             =116,
  MSP_PIDNAMES             =117,
  MSP_SERVO_CONF           =120,
    
  
  MSP_SET_RAW_RC           =200,
  MSP_SET_RAW_GPS          =201,
  MSP_SET_PID              =202,
  MSP_SET_BOX              =203,
  MSP_SET_RC_TUNING        =204,
  MSP_ACC_CALIBRATION      =205,
  MSP_MAG_CALIBRATION      =206,
  MSP_SET_MISC             =207,
  MSP_RESET_CONF           =208,
  MSP_SELECT_SETTING       =210,
  MSP_SET_HEAD             =211, // Not used
  MSP_SET_SERVO_CONF       =212,
  MSP_SET_MOTOR            =214,
  
  
  MSP_BIND                 =240,

  MSP_EEPROM_WRITE         =250,
  
  MSP_DEBUGMSG             =253,
  MSP_DEBUG                =254
;
  private int step = 0;
  private int[][]requests = {
      {MSP_STATUS /*11*/, MSP_RAW_IMU/*18*/},
      {MSP_SERVO/*32*/}, 
      {MSP_ANALOG/*7*/},
      {MSP_MOTOR/*32*/},
      {MSP_RC/*32*/},
     {MSP_ATTITUDE/*6*/, MSP_ALTITUDE/*10*/}      
  };
  private QuadStation station;
  private Character[] rcPayload = null;
  private boolean newRcPayload = false;
  public Connection(QuadStation station) {
    this.station = station;
  }
  public void run() {
    while(true) {
      for (int[] request : requests) { //send all of the status requests, throttled(!)
        if(station.init_com == 1 && station.graph_on ==1) {
          station.sendRequestMSP(station.requestMSP(request)); 
        } else {
        }
        try {
          sleep(10);
        } catch (InterruptedException e) {}
      }
      synchronized(this) {
        if(newRcPayload && station.init_com == 1 && station.graph_on == 1) { //send new rc data if available, and still throttle.
          station.sendRequestMSP(station.requestMSP(MSP_SET_RAW_RC, rcPayload));
          System.out.print("+");
          newRcPayload = false;
        }
        try {
          sleep(10);
        } catch (InterruptedException e) {}
      }
      
   }
  }
  
  public synchronized void sendRcPayload(Character[] payload) {
    rcPayload = payload;
    newRcPayload = true;
  }
  
}
