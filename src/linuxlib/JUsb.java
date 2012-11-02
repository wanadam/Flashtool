package linuxlib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import libusb.UsbDevList;
import libusb.UsbDevice;
import libusb.UsbSystem;

import org.logger.MyLogger;

import flashsystem.BytesUtil;
import flashsystem.HexDump;

public class JUsb {
	
	private static byte[] data = new byte[512];
	private static UsbSystem us=null;
	private static UsbDevice dev=null;
	private static String VendorId = "";
	private static String DeviceId = "";
	private static String Serial = "";
	public static String version = "";
	
	public static void init() {
		us = new UsbSystem();
	}
	
	public static void fillDevice(boolean destroy) {
		dev = getDevice();
		if (dev!=null) {
			VendorId = dev.getVendor().toUpperCase();
			DeviceId = dev.getProduct().toUpperCase();
			Serial = "";
			if (destroy) {
				dev.open();
				Serial = dev.getSerial();
				dev.close();
				dev.destroy();
			}
		}
		else {
			VendorId = "";
			DeviceId = "";
			Serial = "";			
		}
	}
	
	public static String getVendorId() {
		return VendorId;
	}
	
	public static String getProductId() {
		return DeviceId;
	}
	
	public static String getSerial() {
		return Serial;
	}
	
	public static UsbDevice getDevice() {
		UsbDevice device=null;
		UsbDevList ulist = us.getDevices("0fce");
	    if (ulist.size()> 0 ) {
	    	device = ulist.get(0);
	    }
	    return device;
	}
	
	public static void open() {
  	  	dev.openAndClaim(0);
	}
	
	public static void writeBytes(byte[] towrite) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(towrite);		
  	  	boolean hasData = true;
  	  	int loop = 0;
  	  	while (hasData) {
				int read = in.read(data);
				if (read > 0) {
	  	  			dev.bulkWrite(BytesUtil.getReply(data, read));
				}
				else hasData=false;
  	  	}
  	  	in.close();
	}

	public static void close() {
		dev.releaseAndClose();
	}
	
	public static byte[] readBytes() {
		return dev.bulkRead();
	}

	public static byte[] readBytes(int timeout) {
		return dev.bulkRead();
	}

	public static void cleanup() throws Exception {
		us.endSystem();
	}

}