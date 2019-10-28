package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Framework.ISystem;
import frc.util.LogitechAxis;
import frc.util.LogitechButton;
import frc.util.LogitechF310;

public class Elevator implements ISystem {
  // region Objects
  CANSparkMax liftBoi;
  Arm arm;
  // endregion
  // region Variables
  long lastTime;
  double maxPos = 122, minPos = 0;
  boolean isControled;
  double targetEncoder = maxPos;
  double lowCargoPos = 35;
  double highHatch = 100;
  long lastPosTime;

  int hatchLevel = 0;

  // endregion
  public void setupDashboard() {
    SmartDashboard.putNumber("Elevator: Max", maxPos);
    SmartDashboard.putNumber("Elevator: Min", minPos);
    SmartDashboard.putNumber("High Hatch", highHatch);
    SmartDashboard.putNumber("Low Cargo", lowCargoPos);

    maxPos = SmartDashboard.getNumber("Elevator: Max", 122);
    minPos = SmartDashboard.getNumber("Elevator: Min", 5);

  }

  public Elevator(Arm arm) {
    this.arm = arm;
    liftBoi = new CANSparkMax(SystemMap.Elevator.LIFT, MotorType.kBrushless);
    liftBoi.setIdleMode(IdleMode.kBrake);
    liftBoi.setInverted(true);
    lastTime = System.currentTimeMillis();
    lastPosTime = System.currentTimeMillis();

    isControled = true;
    targetEncoder = 30;

    setupDashboard();
  }

  @Override
  public void teleopUpdate(LogitechF310 driver, LogitechF310 operator) {
    double currentEncoder = -liftBoi.getEncoder().getPosition();
    if (System.currentTimeMillis() - lastPosTime > 500) {
      if (operator.getPOV() == 0) {
        lastPosTime = System.currentTimeMillis();
        targetEncoder = highHatch;
        arm.targetEncoder = arm.armMax;
        arm.isControled = false;
        isControled = false;

      } else if (operator.getPOV() == 180) {
        lastPosTime = System.currentTimeMillis();

        targetEncoder = 0;
        arm.targetEncoder = 0;

        arm.isControled = false;
        isControled = false;

      } else if (operator.getPOV() == 270) {
        targetEncoder = maxPos;
        arm.targetEncoder = 0;
        arm.isControled = false;
        isControled = false;
      } else if (operator.getRawButton(LogitechButton.Y)) {
        lastPosTime = System.currentTimeMillis();
        if (targetEncoder != lowCargoPos) {
          targetEncoder = lowCargoPos;
        } else {
          targetEncoder = 0;
        }

        arm.isControled = false;
        arm.targetEncoder = 0;
        isControled = false;
      } else if (operator.getRawButton(LogitechButton.BACK)) {
        lastPosTime = System.currentTimeMillis();
        if (targetEncoder != maxPos) {
          targetEncoder = maxPos;
        } else {
          targetEncoder = 0;
        }

        isControled = false;
      }

    }

    if (isControled) {
      targetEncoder = 0;
      hatchLevel = 0;
      if ((-operator.getAxis(LogitechAxis.LY) > .1) && currentEncoder < maxPos) {
        if (arm.shoulder.getEncoder().getPosition() < 5) {
          arm.goUp();
        } else {
          arm.stop();
        }
        startMotor(-operator.getAxis(LogitechAxis.LY));
      } else if ((-operator.getAxis(LogitechAxis.LY) < -.1) && currentEncoder > minPos) {
        startMotor(-operator.getAxis(LogitechAxis.LY) * .75);
      } else {
        stop();
      }
    } else {
      if (currentEncoder > targetEncoder + 5) {
        goDown();
      } else if (currentEncoder < targetEncoder - 5) {
        goUp();
      } else {
        stop();
      }

    }
    if (Math.abs(-operator.getAxis(LogitechAxis.LY)) > .1) {
      isControled = true;
    }
  }

  public void goUp() {
    liftBoi.set(1);
  }

  public void goDown() {
    liftBoi.set(-.75);
  }

  public void startMotor(double speed) {
    liftBoi.set(speed);
  }

  public void stop() {
    liftBoi.set(.02);
  }

  public void updateTarget() {
    switch (hatchLevel) {
    case 0:
      targetEncoder = 0;
      arm.targetEncoder = 0;
      break;
    case 1:
      targetEncoder = maxPos;
      arm.targetEncoder = 0;
      break;
    case 2:
      targetEncoder = highHatch;
      arm.targetEncoder = arm.armMax;
      break;
    default:
      targetEncoder = 0;
      break;
    }
  }

  public void update() {
    SmartDashboard.putNumber("Elevator: Encoder", -liftBoi.getEncoder().getPosition());
    maxPos = SmartDashboard.getNumber("Elevator: Max", 122);
    minPos = SmartDashboard.getNumber("Elevator: Min", 0);
    lowCargoPos = SmartDashboard.getNumber("Low Cargo", 0);
    highHatch = SmartDashboard.getNumber("High Hatch", maxPos);
  }
}