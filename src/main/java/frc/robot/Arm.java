package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Framework.ISystem;
import frc.util.LogitechAxis;
import frc.util.LogitechButton;
import frc.util.LogitechF310;

public class Arm implements ISystem {
  // region Objects
  CANSparkMax shoulder;
  // endregion
  // region Variables
  Double highCap, lowCap;
  double armMax, armMin;
  double encoder;
  boolean isControled = true;
  boolean isDefense = false;
  double targetEncoder = 0;
  long lastPosTime;
  long lastDefenseSwitch;

  // endregion
  public Arm() {
    shoulder = new CANSparkMax(SystemMap.Arm.ARM_LEFT, MotorType.kBrushless);
    shoulder.setIdleMode(IdleMode.kBrake);
    SmartDashboard.putNumber("Arm: Max", 90);
    SmartDashboard.putNumber("Arm: Min", 2);
    armMax = SmartDashboard.getNumber("Arm: Max", 90);
    armMin = SmartDashboard.getNumber("Arm: Min", 2);
    lastPosTime = System.currentTimeMillis();
    lastDefenseSwitch = lastPosTime;
  }

  @Override
  public void teleopUpdate(LogitechF310 driver, LogitechF310 operator) {
    encoder = shoulder.getEncoder().getPosition();
    if (System.currentTimeMillis() - lastPosTime > 500) {
      if (operator.getRawButton(LogitechButton.START)) {
        lastPosTime = System.currentTimeMillis();

        if (targetEncoder < armMax) {
          targetEncoder = armMax;
        } else {
          targetEncoder = 0;
        }

        isControled = false;

      }
    }

    UpdateCap();

    if (driver.getRawButton(LogitechButton.START) && System.currentTimeMillis() - lastDefenseSwitch > 500) {
      isDefense = !isDefense;
      lastDefenseSwitch = System.currentTimeMillis();
    }

    if (isControled && !isDefense) {
      if (encoder < -5) {
        goUp();
      } else if (-operator.getAxis(LogitechAxis.RY) > .1 && encoder < armMax) {
        startMotor(-operator.getAxis(LogitechAxis.RY) * highCap * .75);
      } else if (-operator.getAxis(LogitechAxis.RY) < -.1 && encoder > armMin) {
        startMotor(-operator.getAxis(LogitechAxis.RY) * lowCap * .5);
      } else {
        stop();
      }
    } else {

      if (encoder > targetEncoder + 15) {
        goDown();
      } else if (encoder < targetEncoder) {
        goUp();
      } else {
        stop();
      }
    }

    if (Math.abs(-operator.getAxis(LogitechAxis.RY)) > .1) {
      isControled = true;
    }
  }

  public void goUp() {
    shoulder.set(1 * highCap);

  }

  public void goDown() {
    shoulder.set(-.75 * lowCap);

  }

  public void startMotor(double speed) {
    shoulder.set(speed);

  }

  public void stop() {
    if (!isDefense)
      shoulder.set(.02);
    else
      shoulder.set(-.03);
  }

  public void check() {
    while (shoulder.getEncoder().getPosition() < 1) {
      goUp();
    }
    stop();
  }

  public void compUpdateCap() {
    if (encoder > 40) {
      highCap = .25;
    } else if (encoder > 25) {
      highCap = .5;
    } else if (encoder > 10) {
      highCap = .75;
    } else {
      highCap = 1.0;
    }

    if (encoder < 30) {
      lowCap = .25;
    } else if (encoder < 45) {
      lowCap = .75;
    } else {
      lowCap = 1.0;
    }
  }

  public void UpdateCap() {
    if (encoder > 80) {
      highCap = .25;
    } else if (encoder > 55) {
      highCap = .5;
    } else if (encoder > 20) {
      highCap = .75;
    } else {
      highCap = 1.0;
    }

    if (encoder < 25) {
      lowCap = .25;
    } else if (encoder < 60) {
      lowCap = .75;
    } else {
      lowCap = 1.0;
    }
  }

  public void update() {
    SmartDashboard.putNumber("Arm: Pos", shoulder.getEncoder().getPosition());
    armMax = SmartDashboard.getNumber("Arm: Max", 90);
    armMin = SmartDashboard.getNumber("Arm: Min", 2);

    SmartDashboard.putNumber("Arm: Current", shoulder.getOutputCurrent());
  }
}