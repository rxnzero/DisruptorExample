package com.dhlee.clone;

public class CloneMain {
	
	private static void testClone() throws CloneNotSupportedException {
		CloneTest c1 = new CloneTest();
        c1.getList().add(1);
        c1.getList().add(2);
        System.out.println("c1 = " + c1.getList());
        CloneTest c2 =(CloneTest) c1.clone();
        System.out.println("c2 = " + c2 + ", c1 =" + c1);
        System.out.println(c2 == c1);
        System.out.println(c2.getList()== c1.getList());
        c1.getList().add(3);
        System.out.println(c1.getList());
        System.out.println(c2.getList());
	}
	
	private static void testCopy() throws Exception {
		CloneTest c1 = new CloneTest();
        c1.getList().add(1);
        c1.getList().add(2);
        System.out.println("c1 = " + c1.getList());
        CloneTest c2 = CloneTest.copy(c1);
        System.out.println("c2 = " + c2 + ", c1 =" + c1);
        System.out.println(c2 == c1);
        System.out.println(c2.getList()== c1.getList());
        c1.getList().add(3);
        System.out.println(c1.getList());
        System.out.println(c2.getList());
	}
	
	public static void main(String[] args) throws CloneNotSupportedException {
		try {
			System.out.println(">> clone test");
			testClone();
			System.out.println(">> copy test");
			testCopy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
