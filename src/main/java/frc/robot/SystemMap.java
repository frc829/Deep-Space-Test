package frc.robot;

public class SystemMap {
  public static final class Drive {
    public static final int PCM = 1;
    public static final int TRANS_CHANNEL_ON = 1;
    public static final int TRANS_CHANNEL_OFF = 0;
    public static final int FRONT_RIGHT = 11;
    public static final int MID_RIGHT = 10;
    public static final int BACK_RIGHT = 12;
    public static final int FRONT_LEFT = 14;
    public static final int MID_LEFT = 13;
    public static final int BACK_LEFT = 15;

    // encoder ports 1 and 4
  }

  public static final class Elevator {
    public static final int LIFT = 19;
  }

  public static final class Arm {
    public static final int ARM_LEFT = 20;
    public static final int ARM_RIGHT = 24;
  }

  public static final class Pushup {
    public static final int PUSHUP = 18;

    public static final int PCM = 1;
    public static final int heartEmoji = 2;
  }

  public static final class Manipulator {
    public static final int WRIST = 21;
    public static final int INTAKE = 23;
    public static final int PCM = 1;
    public static final int CLAW_CHANNEL_ON = 6;
    public static final int CLAW_CHANNEL_OFF = 7;
  }

}
