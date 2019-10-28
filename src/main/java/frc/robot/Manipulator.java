package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Framework.ISystem;
import frc.util.LogitechAxis;
import frc.util.LogitechButton;
import frc.util.LogitechF310;

public class Manipulator implements ISystem {
  // region Objects
  TalonSRX wrist, intake;
  Solenoid claw1, claw2;
  // endregion
  // region Variables
  Long lastTime = System.currentTimeMillis();
  long autoTime;
  int wristLevel = 1;
  double holdingSpeed = -.04;
  double currentHoldingSpeed = holdingSpeed;
  double targetPot = 0;
  boolean isHatchSecured = false;
  double degrees;
  long lastStopTime;

  int wristMode = 0;

  // endregion
  public Manipulator() {
    wrist = new TalonSRX(SystemMap.Manipulator.WRIST);
    intake = new TalonSRX(SystemMap.Manipulator.INTAKE);
    claw1 = new Solenoid(SystemMap.Manipulator.PCM, SystemMap.Manipulator.CLAW_CHANNEL_ON);
    claw2 = new Solenoid(SystemMap.Manipulator.PCM, SystemMap.Manipulator.CLAW_CHANNEL_OFF);
    claw1.set(true);
    claw2.set(false);
    wrist.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

    wrist.setNeutralMode(NeutralMode.Brake);
   
  }

  @Override
  public void teleopUpdate(LogitechF310 driver, LogitechF310 operator) {
    degrees = wrist.getSelectedSensorPosition();

    if (operator.getPOV() == 0 || operator.getPOV() == 180 || operator.getPOV() == 270) {
      wristMode = 1;
    }

    if (operator.getRawButton(LogitechButton.RB)) {
      in();
    } else if (operator.getRawButton(LogitechButton.LB)) {
      out();
    } else {
      intake.set(ControlMode.PercentOutput, .1);
    }

    if (operator.getRawButton(LogitechButton.A)) {
      toggleClaw();
      // wrist.setSelectedSensorPosition(0, 0, 10);
    }

    if (wristMode == 1) {
      compHatchLevel();
    } else {
      if (operator.getRawButton(LogitechButton.B)) {
        compHatchLevel();
      } else if (operator.getAxis(LogitechAxis.RT) > .2) {
        wrist.set(ControlMode.PercentOutput, -operator.getAxis(LogitechAxis.RT) * 1);
      } else if (operator.getAxis(LogitechAxis.LT) > .2) {
        wrist.set(ControlMode.PercentOutput, operator.getAxis(LogitechAxis.LT) * 1);
      } else {
        stop();
      }
    }

    if (operator.getAxis(LogitechAxis.LT) > .2 || operator.getAxis(LogitechAxis.RT) > .2) {
      wristMode = 0;
    }

  }

  public void up() {
    wrist.set(ControlMode.PercentOutput, -.5);
  }

  public void down() {
    wrist.set(ControlMode.PercentOutput, .5);
  }

  public void stop() {
    // comp is <500
    if (wrist.getSelectedSensorPosition() < -1800) {
      wrist.set(ControlMode.PercentOutput, currentHoldingSpeed);
    } else {
      wrist.set(ControlMode.PercentOutput, 0);
    }

  }

  public void in() {
    intake.set(ControlMode.PercentOutput, 1);
  }

  public void out() {
    intake.set(ControlMode.PercentOutput, -1);
  }

  public void toggleClaw() {
    if (System.currentTimeMillis() - lastTime > 500) {
      claw1.set(!claw1.get());
      claw2.set(!claw2.get());
      lastTime = System.currentTimeMillis();
    }
  }

  public void compHatchLevel() {
    if (wrist.getSelectedSensorPosition() < -2900) {
      up();
    } else if (wrist.getSelectedSensorPosition() > -2800) {
      down();
    } else {
      stop();
    }
    
  }

  public void practiceHatchLevel() {
    if (wrist.getSelectedSensorPosition() > 510) {
      up();
    } else if (wrist.getSelectedSensorPosition() < 490) {
      down();
    } else {
      stop();
    }
    
  }


  public void update() {
    SmartDashboard.putNumber("Pot:", wrist.getSelectedSensorPosition());
  }

  public void sandstorm(LogitechF310 driver, LogitechF310 operator) {

    if (!isHatchSecured) {
      if (wrist.getSelectedSensorPosition() > -1500) {
        wrist.set(ControlMode.PercentOutput, .75);
        claw1.set(false);
        claw2.set(true);
      } else {
        isHatchSecured = true;
        stop();
      }

    } else {
      teleopUpdate(driver, operator);
    }

  }

}