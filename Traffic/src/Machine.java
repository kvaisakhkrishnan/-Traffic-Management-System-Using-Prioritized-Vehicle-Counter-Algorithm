import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;
public class Machine{
	private String systemLocation;
	private String stationName;
	private Lane currentLane;
	private boolean onOrOff;
	private double vehicleFraction;
	private int noOfLanes;
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	LocalTime localTime = LocalTime.now();
	
	
	Machine(String systemLocation,String stationName, int noOfLanes)
	{
		this.systemLocation = systemLocation;
		this.stationName = stationName;
		this.noOfLanes = noOfLanes;
		this.currentLane = null;
		this.onOrOff = false;
		this.vehicleFraction = 0;
		
		
	}
	
	public double getVehicleFraction() {
		return vehicleFraction;
	}

	public void setVehicleFraction(double vehicleFraction) {
		this.vehicleFraction = vehicleFraction;
	}

	public int getNoOfLanes() {
		return noOfLanes;
	}

	public void setNoOfLanes(int noOfLanes) {
		this.noOfLanes = noOfLanes;
	}

	public void run()
	{
		emergencyChange();
	}
	public void startProcess()
	{
		boolean check = this.checkTime();
		if(check == false)
		{
			
			this.activateSensor();
			this.onOrOff = true;

		}
		
		else
		{
			
			this.onOrOff = false;
			Lane tLane = this.currentLane;
			int currLaneCount = 0;
			int totCount = 0;
			for(int i=0;i<this.noOfLanes;i++)
			{
				
				if(tLane == this.currentLane)
				{	
					currLaneCount = this.currentLane.getSensor().countVehicles(this.currentLane.getVehicle());
					totCount+=currLaneCount;
					
				}
				else
				{
					totCount += this.currentLane.getSensor().countVehicles(this.currentLane.getVehicle());
					
				}
				this.currentLane = this.currentLane.getNextLane();
			}
			
			if(totCount==0)
			{
				this.onOrOff = false;
				for(int i = 0; i<this.noOfLanes;i++)
				{
					System.out.println("Lane No: "+this.currentLane.getLaneNumber());
					this.currentLane.getSignal().changeSignal(false, true, false);
					try
					{
						Thread.sleep(1000);
					}
					catch(InterruptedException e)
					{
						System.out.println("Unable to pause thread "+e);
					}
					this.currentLane.getSignal().changeSignal(false, false, false);
					this.currentLane = this.currentLane.getNextLane();
				}
			}
			else
			{
				
				this.onOrOff = true;
				activateSensor();
				
			}
			
		}
	
	
	}
	
	public boolean checkTime()
	{
		String sTime = dtf.format(localTime).toString();
		
		if(sTime.compareTo("20:00:00")>=0 || sTime.compareTo("08:00:00")<=0)
		{
			return true;
		}
		return false;
	}
	
	public void emergencyChange()
	{
		Lane pLane = this.currentLane;
		this.currentLane = this.currentLane.getNextLane();
		while(this.currentLane!= pLane)
		{
			
			System.out.println("\nStopping Lane Number : "+this.currentLane.getLaneNumber());
			System.out.println("\nSignal");
			this.currentLane.getSignal().changeSignal(false, false, true);
			this.currentLane = this.currentLane.getNextLane();
		}
	}
	
	public void activateSensor()
	{
		Lane tlane = this.currentLane;
		int i = 0;
		while(i<this.noOfLanes)
		{
			tlane.setSensor(new Sensor(i,"IR_SENSOR",500));
			i++;
		}
	
		boolean ambCheck = false;
		for(int j = 0; j<this.noOfLanes;j++)
		{
			
			if(this.currentLane.checkAmbulance()==true && this.currentLane.getAmbulance().getChip().getMessage().equals("URGENT"))
			{
				ambCheck=true;
				break;
			}
			this.currentLane = this.currentLane.getNextLane();
		}
	
	
		if(ambCheck == true)
		{
			double apprTime = 0;
			apprTime = this.currentLane.calculateApproachTime();
			System.out.println("Ambulance approaching in Lane "+this.currentLane.getLaneNumber());
			emergencyChange();
			System.out.println("\nHighest Priority Lane No: "+this.currentLane.getLaneNumber());
			System.out.println("Signal");
			this.currentLane.getSignal().changeSignal(false, true, false);
			try
			{
				Thread.sleep(3000);
			}
			catch(InterruptedException e)
			{
				System.out.println("Unable to sleep thread "+e);
			}
			this.currentLane.getSignal().changeSignal(true, false, false);
			this.currentLane.getTimer().setCountdownTime(apprTime*1.5);
			this.currentLane.getSignal().changeSignal(false, true, false);
			try
			{
				Thread.sleep(3000);
			}
			catch(InterruptedException e)
			{
				System.out.println("Unable to sleep thread "+e);
			}
			this.currentLane.getSignal().changeSignal(false, false, true);
		}
		
		else
		{
			
			while(this.getCurrentLane().getLaneNumber()!=0)
				this.setCurrentLane(this.getCurrentLane().getNextLane());
			boolean checkPed = false;
			for(int j = 0; j<this.noOfLanes;j++)
			{
				if(this.currentLane.checkPedestrian())
				{
					checkPed = true;
					break;
				}
				this.currentLane = this.currentLane.getNextLane();
			}
		
			if(checkPed == true)
			{	System.out.println("\nPedestrian found in lane : "+this.getCurrentLane().getLaneNumber());
				System.out.println("Changing Signals\n");
				Lane tempLane = this.currentLane;
				this.currentLane = this.currentLane.getNextLane();
				while(tempLane!=this.currentLane)
				{
					System.out.println("Lane Number : "+this.currentLane.getLaneNumber()+"\nSignal\n");
					this.currentLane.getSignal().changeSignal(false, true, false);
					try
					{
						Thread.sleep(3000);
					}
					catch(InterruptedException e)
					{
						System.out.println("Cannot sleep thread "+e);
					}
					this.currentLane.getSignal().changeSignal(false, false, true);
					this.currentLane = this.currentLane.getNextLane();
				}
				System.out.println("\nPedestrian Lane : "+this.currentLane.getLaneNumber());
				this.currentLane.getLight().changeSignal(true, false);
				this.currentLane.getTimer().setCountdownTime(10.0);
				this.currentLane.getLight().changeSignal(false, true);
				this.currentLane.setPedestrian(null);
			}
			
			while(this.currentLane.getLaneNumber()!=0)
			{
				this.currentLane = this.currentLane.getNextLane();
			}
			
			
			Lane tLane = this.currentLane;
			int currLaneCount = 0;
			int totCount = 0;
			
			for(i=0;i<this.noOfLanes;i++)
			{
				
				if(tLane == this.currentLane)
				{
					
					currLaneCount = this.currentLane.getSensor().countVehicles(this.currentLane.getVehicle());
					
					totCount+=currLaneCount;
					
				}
				else
				{
					totCount += this.currentLane.getSensor().countVehicles(this.currentLane.getVehicle());
					
				}
				
				this.currentLane = this.currentLane.getNextLane();
			}
			
			if(totCount==0)
				totCount=1;
			this.vehicleFraction = (currLaneCount*1.0)/totCount;
			Double countDown = 30 * this.vehicleFraction;
			
			if(this.vehicleFraction == 0)
			{
				
				int count = 0;
				while(count<this.noOfLanes)
				{
					count++;
					this.setCurrentLane(this.getCurrentLane().getNextLane());
				
					
					
					for(int l = 0;l<this.noOfLanes;l++)
					{
						tLane = this.currentLane;
						currLaneCount = 0;
						totCount = 0;
						
						for(i=0;i<this.noOfLanes;i++)
						{
							
							if(tLane == this.currentLane)
							{
								
								currLaneCount = this.currentLane.getSensor().countVehicles(this.currentLane.getVehicle());
								
								totCount+=currLaneCount;
								
							}
							else
							{
								totCount += this.currentLane.getSensor().countVehicles(this.currentLane.getVehicle());
								
							}
							
							this.currentLane = this.currentLane.getNextLane();
						}
						
						
						
					}
					if(totCount==0)
						totCount++;
					
					double tfrac=(currLaneCount*1.0)/totCount;
				
					if(tfrac!=0)
					{
						countDown = 30.0 * tfrac;
						
						break;
					}
						
					
					
					
					
					
				}
				
				
				
				
				System.out.println("\nLane Number : "+this.currentLane.getLaneNumber()+"\n");
				this.currentLane.getSignal().changeSignal(false, false, true);
				if(countDown==0);
				else
				{
					
				
				try
				{
					Thread.sleep(3000);
				}
				catch(InterruptedException e)
				{
					System.out.println("Unable to sleep thread, "+e);
				}
				this.currentLane.getSignal().changeSignal(false, true, false);
				try
				{
					Thread.sleep(3000);
				}
				catch(InterruptedException e)
				{
					System.out.println("Unable to sleep thread, "+e);
				}
				this.currentLane.getSignal().changeSignal(true, false, false);
				this.currentLane.getTimer().setCountdownTime(countDown);
				this.currentLane.getSignal().changeSignal(false, true, false);
				
				try
				{
					Thread.sleep(3000);
				}
				catch(InterruptedException e)
				{
					System.out.println("Unable to sleep thread, "+e);
				}
				this.currentLane.getSignal().changeSignal(false, false, true);
				}
				this.currentLane = this.currentLane.getNextLane();
			
				
				
				
			}
			
			else if(this.vehicleFraction == 1 )
				countDown = totCount * 5.0;
			else if(countDown<15.0)
				countDown = 15.0;
			
			System.out.println("Lane Number : "+this.currentLane.getLaneNumber());
			this.currentLane.getSignal().changeSignal(false, false, true);
			if(this.vehicleFraction==0);
			else
			{
				
			
			try
			{
				Thread.sleep(3000);
			}
			catch(InterruptedException e)
			{
				System.out.println("Unable to sleep thread, "+e);
			}
			this.currentLane.getSignal().changeSignal(false, true, false);
			try
			{
				Thread.sleep(3000);
			}
			catch(InterruptedException e)
			{
				System.out.println("Unable to sleep thread, "+e);
			}
			this.currentLane.getSignal().changeSignal(true, false, false);
			this.currentLane.getTimer().setCountdownTime(countDown);
			this.currentLane.getSignal().changeSignal(false, true, false);
			
			try
			{
				Thread.sleep(3000);
			}
			catch(InterruptedException e)
			{
				System.out.println("Unable to sleep thread, "+e);
			}
			this.currentLane.getSignal().changeSignal(false, false, true);
			}
			this.currentLane = this.currentLane.getNextLane();
		}
	
	}
	
	

	public boolean checkAmbulance()
	{
		Lane tlane = this.currentLane;
		int i = 0;
		boolean find = false;
		for(;i<this.noOfLanes;i++)
		{
			if(this.currentLane.checkAmbulance()==true)
			{
				find = true;
			}
		}
		return find;
		
	}
	
	
	
	
	public Lane getCurrentLane() {
		return currentLane;
	}




	public void setCurrentLane(Lane currentLane) {
		this.currentLane = currentLane;
	}




	public String getSystemLocation()
	{
		return systemLocation;
	}
	public void setSystemLocation(String systemLocation) {
	}
	public String getStationName() {
		return stationName;
	}
	public void getStationName(String stationName) {
		
	}

	public boolean powerOnOff() {
		return onOrOff;
	}
	public void pointNextLane() {
		
	}
	

}
