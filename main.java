import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import java.io.IOException;
import org.firmata4j.I2CDevice;
import	org.firmata4j.ssd1306.SSD1306;

public class Major {
    //	Pin	definitions
    static	final	int	A0	=	14;	//	Potentiometer
    static	final	int	A2	=	16;	//	Sound
    static	final	int	D6	=		6;	//	Button
    static	final	int	D4	=		4;	//	LED
    static	final	byte	I2C0	=	0x3C;	//	OLED	Display
    static	final	int	D3	=	3;  // Motion sensor
    static	final	int	D2	=	2;  // Fan
    static final    int A6 =    20;  // Light sensor
    static	final	int	A1	=	15;  // Moisture sensor

    public static void main(String[] args) throws	InterruptedException,	IOException{
        var	device	=	new	FirmataDevice("COM3");
        device.start();
        device.ensureInitializationIsDone();

        // set up the button
        var myButton = device.getPin(6);
        myButton.setMode(Pin.Mode.INPUT);
        // set up the Light sensor
        var mylight = device.getPin(20);
        mylight.setMode(Pin.Mode.ANALOG);
        // set up the moisture sensor
        var myMoist = device.getPin(15);
        myMoist.setMode(Pin.Mode.ANALOG);
        // set up the Motion sensor
        var myMotion = device.getPin(3);
        myMotion.setMode(Pin.Mode.INPUT);
        // set up the Fan
        var myFan = device.getPin(2);
        myFan.setMode(Pin.Mode.OUTPUT);
        //	set	up	the OLED	 display	(type,	size	...)
        I2CDevice i2cObject = device.getI2CDevice((byte) 0x3C); // Use 0x3C for the Grove OLED
        SSD1306 OledObject = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64); // 128x64 OLED SSD1515

        while(myButton.getValue() == 0){  // program stops when button is pressed
            while(true){                  // infinite loop that exits when all three conditions are met
                if (myMoist.getValue() < 650) {
                    while(true){                    // infinite loop that exits when the last conditions are met
                        if (mylight.getValue() > 15) {

                            if (myMotion.getValue() > 0) {
                                OledObject.getCanvas().clear();
                                OledObject.getCanvas().setTextsize(3);
                                OledObject.getCanvas().drawString(0, 0, "Welcome Back");
                                OledObject.display();

                                myFan.setValue(1);         // display and the fan on
                                break;                      // leave the nested while loop
                            }
                        }
                    }
                    break;          // leave the second main loop and leave the fan on
                }else{
                    continue;
                }
            }
            while (true){                 // while loop to check if user leaves
                if(myMotion.getValue() > 0){
                    while (true) {
                        if (mylight.getValue() < 15){
                            if(myMoist.getValue() < 650){
                                OledObject.getCanvas().clear();
                                OledObject.getCanvas().setTextsize(3);
                                OledObject.getCanvas().drawString(0, 0, "BYE");
                                OledObject.display();

                                myFan.setValue(0);   // display and fan off if all conditions are met in opposite order
                                break;                // leave the nested loop
                            }
                        }
                    }
                    break;                        /* leave the main loop that turns off fan and return
                                                  to Parent while loop to start everything again*/
                }
            }
        }
    }

}
