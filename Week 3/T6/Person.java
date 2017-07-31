import java.io.*;

public class Person implements Serializable{
	
	String name, address;
	int age;
	
	public Person(String name, String address,int age){
		this.name=name;
		this.address=address;
		this.age=age;
	}
	
	public String toString(){
		return name+" "+ address +" "+ age+" ";
	}
	
}