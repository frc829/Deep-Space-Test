package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Framework.ISystem;
import frc.util.LogitechButton;
import frc.util.LogitechF310;

public class PushUp implements ISystem {
  // region Objects
  CANSparkMax pushUp;
  TalonSRX scoot;
  Solenoid stabby;
  // endregion
  // region Variables
  int targetLevel = 0;
  double targetEncoder = 0;
  boolean zipTiesBroke = false;
  long pushLastTime = System.currentTimeMillis();
  long frontLastTime = System.currentTimeMillis();
  boolean frontStatus = false;

  boolean isWork = true;
  double levelOneEncode, levelTwoEncode;

  // endregion
  public PushUp() {
    try {
      pushUp = new CANSparkMax(SystemMap.Pushup.PUSHUP, MotorType.kBrushless);
      pushUp.setIdleMode(IdleMode.kCoast);
      stabby = new Solenoid(SystemMap.Pushup.PCM, SystemMap.Pushup.heartEmoji);
      stabby.set(frontStatus);
      levelOneEncode = 40;
      levelTwoEncode = 50;
    } catch (Exception e) {
      isWork = false;
    }

  }

  @Override
  public void teleopUpdate(LogitechF310 driver, LogitechF310 operator) {
    if (!isWork)
      return;
    if (driver.getPOV() == 0) {
      if (!zipTiesBroke) {
        zipTiesBroke = true;
      }
      increaseTarget();
    } else if (driver.getPOV() == 180) {
      decreaseTarget();
    }

    if (driver.getRawButton(LogitechButton.LB) && driver.getRawButton(LogitechButton.RB)) {
      toggleFront();
    }

    pushUpdate();

  }

  public void goUp() {
    pushUp.set(.65);
  }

  public void goDown() {
    pushUp.set(-.9);
  }

  public void pushMotor(double speed) {
    pushUp.set(speed);
  }

  public void pushStop() {

    if (targetLevel == 0 && !zipTiesBroke) {
      pushUp.set(0);
    } else if (targetLevel == 0) {
      pushUp.set(-.01);
    } else {
      pushUp.set(.03);
    }

  }

  public void pushUpdate() {
    switch (targetLevel) {
    case 0:
      targetEncoder = 0;

      break;
    case 1:
      targetEncoder = levelOneEncode;
      break;
    case 2:
      targetEncoder = levelTwoEncode;
      break;
    default:
      targetEncoder = 0;
    }

    if (pushUp.getEncoder().getPosition() > targetEncoder + 5) {
      goDown();
    } else if (pushUp.getEncoder().getPosition() < targetEncoder - 5) {
      goUp();
    } else {
      pushStop();
    }
  }

  public void increaseTarget() {
    if (targetLevel != 2 && System.currentTimeMillis() - pushLastTime > 500) {
      targetLevel++;
      pushLastTime = System.currentTimeMillis();
      stabby.set(false);
    }

  }

  public void decreaseTarget() {
    if (targetLevel != 0 && System.currentTimeMillis() - pushLastTime > 500) {
      targetLevel = 0;
      pushLastTime = System.currentTimeMillis();

    }
  }

  public void toggleFront() {
    if (System.currentTimeMillis() - frontLastTime > 500) {
      stabby.set(!stabby.get());
      frontLastTime = System.currentTimeMillis();
    }
  }

  public void update() {
    SmartDashboard.putNumber("Pushup: Level", targetLevel);
    levelOneEncode = SmartDashboard.getNumber("Pushup: Level 1 Encoder", 40);
    levelTwoEncode = SmartDashboard.getNumber("Pushup: Level 2 Encoder", 50);

  }
}