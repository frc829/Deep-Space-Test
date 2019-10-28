package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Framework.ISystem;

public class Gyro extends AHRS implements ISystem {

  public Gyro() {
    super(Port.kMXP);
  }

  public void update() {
    SmartDashboard.putNumber("Gyro: Angle", this.getAngle());
    SmartDashboard.putNumber("Gyro: X Displacement", this.getDisplacementX());
    SmartDashboard.putNumber("Gyro: Y Displacement", this.getDisplacementY());
    SmartDashboard.putNumber("Gyro: Z Displacement", this.getDisplacementZ());
    SmartDashboard.putBoolean("Gyro: Calibrating", this.isCalibrating());
    SmartDashboard.putBoolean("Gyro: Connected", this.isConnected());
  }
}
