import java.util.Scanner;
import java.util.*;
import java.io.*;
public class Simulator {
	private int noOfLanes;
	private Vehicle vehicle[][];
	private Ambulance ambulance[];
	private Machine system;
	
	
	public Simulator()
	{	
		try {
		File fr = new File("/Users/vaisakh/eclipse-workspace/Traffic/src/data.txt");
		Scanner stdin = new Scanner(fr);
		String location;
		String station;
		System.out.print("Junction Name: ");
		location = stdin.next();
		System.out.println(location);
		
		
		
		System.out.print("Station Name: ");
		station = stdin.next();
		System.out.println(station);
		
		System.out.print("No of lanes: ");
		this.noOfLanes = stdin.nextInt();
		System.out.println(this.noOfLanes);
		
		Machine system = new Machine(location, station, noOfLanes);
		
		
		Lane tLane = null;
		Lane fLane = null;
		
		for(int i = 0; i<this.noOfLanes;i++)
		{
			System.out.println("\nLane No : "+i);
			String laneTo, laneFrom;
			
			System.out.print("Lane From: ");
			laneFrom = stdin.next();
			System.out.println(laneFrom);
			
			
			System.out.print("Lane To: ");
			laneTo = stdin.next();
			System.out.println(laneTo);
			Lane lane;
			
			lane = new Lane(i, laneTo, laneFrom);
			
			
			
			if(i==0)
			{
				tLane = lane;
				fLane = lane;
				system.setCurrentLane(lane);
				
			}
			else
			{
				tLane.setNextLane(lane);
				tLane = lane;
				
			}
		}
		tLane.setNextLane(fLane);
		
		
		
		Lane llane = system.getCurrentLane();
		llane = llane.getNextLane();
		
		
		
			
		while(fr.length()!=0)
			
		{
			
		vehicle = new Vehicle[this.noOfLanes][];
		for(int i = 0; i<this.noOfLanes;i++)
		{
			System.out.print("\nNo of two wheelers in lane "+i+": ");
			int count_1 = stdin.nextInt();
			System.out.println(count_1);
			
			System.out.print("No of four wheelers in lane "+i+": ");
			int count_2 = stdin.nextInt();
			System.out.println(count_2);
			
			int count = count_1 + count_2;
			vehicle[i] = new Vehicle[count];
			for(int j = 0; j<count_1;j++)
			{
				String vehicle_num;
				String vehicle_type;
				String vehicle_owner;
				System.out.print("Two Wheeler Register Number "+j+": ");
				vehicle_num = stdin.next();
				System.out.println(vehicle_num);
				
				
				System.out.print("Two Wheeler Type "+j+": ");
				vehicle_type = stdin.next();
				System.out.println(vehicle_type);
				
				System.out.print("Two Wheeler Owner "+j+": ");
				vehicle_owner = stdin.next();
				System.out.println(vehicle_owner);
				
				
				vehicle[i][j] = new TwoWheeler(vehicle_num, vehicle_type, vehicle_owner);
			}
			for(int j = count_1;j<count;j++)
			{
				String vehicle_num;
				String vehicle_type;
				String vehicle_owner;
				System.out.print("Four Wheeler Register Number "+j+": ");
				vehicle_num = stdin.next();
				System.out.println(vehicle_num);
				
				System.out.print("Four Wheeler Type "+j+": ");
				vehicle_type = stdin.next();
				System.out.println(vehicle_type);
				
				System.out.print("Four Wheeler Owner "+j+": ");
				vehicle_owner = stdin.next();
				System.out.println(vehicle_owner);
				
				vehicle[i][j] = new FourWheeler(vehicle_num, vehicle_type, vehicle_owner);
			}
			system.getCurrentLane().setVehicle(vehicle[i]);
		//	System.out.println(system.getCurrentLane().getVehicle());
			system.setCurrentLane(system.getCurrentLane().getNextLane());
		}
		
	
		
	
		
		
		
		Lane tLanes = system.getCurrentLane();
		for(int i = 0; i<this.noOfLanes;i++)
		{
			System.out.print("\nAmbulance in lane "+i+": ");
			boolean ambCheck = false;
			ambCheck = stdin.nextBoolean();
			System.out.println(ambCheck);
			if(ambCheck == true)
			{
				System.out.print("Ambulance Registration No : ");
				String ambReg = stdin.next();
				System.out.println(ambReg);
				System.out.println("Hospital Name : ");
				String ambName = stdin.next();
				System.out.println(ambName);
				System.out.print("Chip Message : ");
				String msg = stdin.next();
				System.out.println(msg);
				Double time1,time2;
				System.out.print("Response time 1 : ");
				time1 = stdin.nextDouble();
				System.out.println(time1);
				System.out.print("Response time 2 : ");
				time2 = stdin.nextDouble();
				System.out.println(time2);
				time1/=2;
				time2/=2;
				double distance1 = 300000000 * time1;
				double distance2 = 300000000 * time2;
				
				Ambulance amb = new Ambulance(ambReg, ambName);
				amb.setDistanceOne(distance1);
				amb.setDistanceTwo(distance2);
				tLanes.setAmbulance(amb);
				amb.getChip().setMessage(msg);
				
			}
			else
			{
				tLanes.setAmbulance(null);
			}
			
			tLanes = tLanes.getNextLane();
			
		}
		
		
		
		boolean checkPed = false;
		System.out.print("\nPedestrian: ");
		checkPed = stdin.nextBoolean();
		System.out.println(checkPed);
		if(checkPed == true)
		{
			System.out.print("Lane number : ");
			int lno;
			lno = stdin.nextInt();
			System.out.println(lno);
			System.out.print("Pedestrian name : ");
			String pname = stdin.next();
			System.out.println(pname);
			System.out.print("Pedestrian age : ");
			int page = stdin.nextInt();
			System.out.println(page);
			Pedestrian p = new Pedestrian(pname, page);
			for(int i = 0; i<this.noOfLanes;i++)
			{
				if(system.getCurrentLane().getLaneNumber()==lno)
				{
					system.getCurrentLane().setPedestrian(p);
				}
				else
				{
					system.getCurrentLane().setPedestrian(null);
				}
				system.setCurrentLane(system.getCurrentLane().getNextLane());
			}
			
		}
		else
		{
			for(int i = 0 ; i<this.noOfLanes;i++)
			{
				system.getCurrentLane().setPedestrian(null);
				system.setCurrentLane(system.getCurrentLane().getNextLane());
			}
			
		}
		
		

		System.out.println("\nStarting Simulation\n");
		system.startProcess();
		}
		
		}
		
		
		catch(Exception e)
		{
			System.out.println("\n\nEnd of Simulation");
		}
		
		
	}

}
