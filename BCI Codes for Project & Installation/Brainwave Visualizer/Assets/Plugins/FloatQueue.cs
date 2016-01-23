using UnityEngine;
using System.Collections;
using System;

public class FloatQueue {

  private float[] queue;
	
	public FloatQueue(int capacity){
	  queue = new float[capacity];
	}
	
	public float this[int index] {
	  get { return queue[index]; }
	  set { queue[index] = (float)value; }
	}
	
	public void Push(float value){
	  float tempValue = queue[0];
	  
	  for(int i = 1; i < queue.Length; i++){
	    float newTempValue = queue[i];
	    queue[i] = tempValue;
	    tempValue = newTempValue;
	  }
	  
	  queue[0] = value;
	}
}
