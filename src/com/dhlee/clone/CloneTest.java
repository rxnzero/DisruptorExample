package com.dhlee.clone;

import java.util.ArrayList;
import java.util.List;

public class CloneTest implements Cloneable {
    private List<Integer> list = new ArrayList<>();

    public CloneTest() {
    }

    public CloneTest(List<Integer> list) {
    	this.list = list;
    }
    
    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
    	CloneTest cloned = (CloneTest)super.clone();
    	cloned.setList( (ArrayList)((ArrayList)list).clone());
    	return cloned;
    }
    
    public static CloneTest copy(CloneTest org) throws CloneNotSupportedException
	{
		if(!(org instanceof Cloneable))
		{
			throw new CloneNotSupportedException("Invalid cloning");
		}

		//Can do multiple other things here
//		List<Integer> list = new ArrayList(org.getList());
		List<Integer> list = (ArrayList)((ArrayList)org.getList()).clone();
		return new CloneTest(list);
	}
}
