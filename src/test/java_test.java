package test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



abstract class person_te
{
	String name;
	int age;
	String occupation;
	public abstract String talk();
	public person_te(String name,int age,String occup)
	{
		this.name=name;
		this.age=age;
		this.occupation=occup;
		
	}
}

interface person_in
{
	String name="zhang san";
	int age=45;
	String occupation="teacher";
	public abstract String talk();
}

class stud_in implements person_in
{
	public String talk()
	{
		return "student----name:"+this.name+"->age:"+this.age+"->occuption:"+this.occupation;
		
	}
}

class student_te extends person_te
{
	public student_te(String name,int age,String occup)
	{
		/*
		this.name=name;
		this.age=age;
		this.occupation=occup;
		*/
		super(name,age,occup);
		
	}
	public String talk()
	{
		return "student----name:"+this.name+"->age:"+this.age+"->occuption:"+this.occupation;
		
	}
	
}
class worker_te extends person_te
{
	public worker_te(String name,int age,String occup)
	{
		/*
		this.name=name;
		this.age=age;
		this.occupation=occup;*/
		super(name,age,occup);
		
	}
	public String talk()
	{
		return "worker----name:"+this.name+"->age:"+this.age+"->occuption:"+this.occupation;
	}
	
}


public class java_test {
	//private
	 private static int count=0;
	public String name;
	public int age;
	public java_test()
	{
		System.out.println("java_test");
	}
	public java_test(String name,int age)
	{
		this();
		this.name=name;
		this.age=age;
		System.out.println("java_test111");
		count++;
		System.out.println("count:"+count);
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		
		/*
		student_te s=new student_te("zhouqiao",27,"student");
		worker_te w=new worker_te("test",26,"worker");
		System.out.println(s.talk());
		System.out.println(w.talk());
		stud_in si=new stud_in();
		System.out.println(si.talk());
	*/
		java_test jt = new java_test("111",12);
		java_test jt1 = new java_test("222",13);
		jt1.age=3;
		java_test jt2 = new java_test("222",13);
		System.out.println(jt2);
		
		Object boolobj=true;
		if(boolobj instanceof Boolean)
		{
			boolean b = (Boolean)boolobj;
			System.out.println(b);
		}
		 
	}

}
