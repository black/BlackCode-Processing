package com.math;

import com.algorithm.segmentintersect.*;
import com.datastruct.DCHalfEdge;
import com.datastruct.DoublyConnectedEdgeList;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Vector;


//boolean operations for all polygons

public class PolyBoolean {

	public static DoublyConnectedEdgeList booleanSet(DoublyConnectedEdgeList clip, DoublyConnectedEdgeList subj,String type){

		//step 1: build data structures
		DoublyConnectedEdgeList newSubj = new DoublyConnectedEdgeList();

		LinkedList<BooleanPoint> subjList = new LinkedList<BooleanPoint>();
		LinkedList<BooleanPoint> clipList = new LinkedList<BooleanPoint>();
		

		LinkedList<BooleanPoint> enterPoints = new LinkedList<BooleanPoint>();
		LinkedList<BooleanPoint> exitPoints = new LinkedList<BooleanPoint>();
		
		
		FindIntersections fi = new FindIntersections();
		Vector<DoublyConnectedEdgeList> edgeSets = new Vector<DoublyConnectedEdgeList>();
		edgeSets.add(clip);
		edgeSets.add(subj);
		
		//ArrayList<com.algorithm.segmentintersect.Segment> intersectedSegments = fi.FindIntersectionSets(edgeSets);
		
		
		

		for(int i=0;i<clip.edges.size();i++){
			BooleanPoint bp=new BooleanPoint(clip.edges.get(i).start.getX(),clip.edges.get(i).start.getY());
			
			clipList.add(bp);
		}

		for(int i=0;i<subj.edges.size();i++){
			subjList.add(new BooleanPoint(subj.edges.get(i).start.getX(),subj.edges.get(i).start.getY()));

		}

		int totalIntersections=0;
		//step 2: find intersections
		boolean allIn = true;

		/*DCHalfEdge startClipEdge=null;
		DCHalfEdge startSubjEdge=null;
		DCHalfEdge endClipEdge=null;
		DCHalfEdge endSubjEdge=null;*/

		boolean inPolygon = Geom.rayPointInPolygon(subj.edges.get(0).start,clip);
		for(int i=0;i<subj.edges.size();i++){
			Vector<DCHalfEdge> intersectedEdges = Geom.edgeIntersectsPolygon(subj.edges.get(i),clip);
			boolean startInPolygon = Geom.rayPointInPolygon(subj.edges.get(i).start,clip);
			boolean endInPolygon = Geom.rayPointInPolygon(subj.edges.get(i).end,clip);
			
			Collections.sort(intersectedEdges,new ProximitySort(subj.edges.get(i).start));
			Vector<CompPoint> intersectionPoints = new Vector<CompPoint>();
			LinkedList<BooleanPoint> newInterList= new LinkedList<BooleanPoint>();
			for(int j=0;j<intersectedEdges.size();j++){

				CompPoint intersection = Geom.findIntersectionPoint(subj.edges.get(i), intersectedEdges.get(j));
				intersectionPoints.add(intersection);
			}
			
			
				int subjListIndex =0;
				int clipListIndex =0;
			
				for(int k=0;k<subjList.size();k++){
					if(subj.edges.get(i).start.compareTo(subjList.get(k))==0){
						subjListIndex=k+1;
						if(subjListIndex>=subjList.size()){
							subjListIndex=0;
						}
						//System.out.println("found subj index for"+i+"at"+ k);
						break;
					}

				
			}
			for(int j=0;j<intersectedEdges.size();j++){	
				for(int k=0;k<clipList.size();k++){
					if(intersectedEdges.get(j).start.compareTo(clipList.get(k))==0){
						clipListIndex=k+1;
						if(clipListIndex>=clipList.size()){
							clipListIndex=0;
						}
						//System.out.println("found clip index for"+i +"at"+ k);
						break;
					}

				}
				BooleanPoint newInter = new BooleanPoint(intersectionPoints.get(j).getX(),intersectionPoints.get(j).getY());
				newInter.inter=true;
				inPolygon = !inPolygon;

				if(!inPolygon){
					newInter.direction="exit";
					exitPoints.add(newInter);
				}

				if(inPolygon){
					newInter.direction="enter";
					enterPoints.add(newInter);
				}


				
				newInterList.add(newInter);

				//System.out.println(newInter.direction);
				
				//check ordering of clip list intersections
				if(!clipList.get(clipListIndex).inter){
					clipList.add(clipListIndex,newInter);
				}
				else{
					
					Vector<BooleanPoint>clipListReversal = new Vector(0);
					int clipListCounter = clipListIndex;
					
					while(clipList.get(clipListCounter).inter){
						clipListReversal.add(clipList.get(clipListCounter));
						clipList.remove(clipListCounter);
						clipListCounter--;
						if(clipListCounter<0){
							clipListCounter=clipList.size()-1;
						}
					}
					
					clipListReversal.add(newInter);
					Collections.sort(clipListReversal,new ProximityPointSort(intersectedEdges.get(j).start));
					
					clipList.addAll(clipListIndex,clipListReversal);
				}
			}
			
			
			subjList.addAll(subjListIndex,newInterList);	

			if(!startInPolygon && !endInPolygon){
				allIn = false;
			}

			totalIntersections+=intersectedEdges.size();

		}
		if(allIn && totalIntersections==0){
			//System.out.println("wholly contained in clipping shape");
			if(type=="difference"){
				for(int i = 0;i<subj.edges.size();i++){
					
					int next = i+1;
					if(next==subj.edges.size()){
						next = 0;
					}
					CompPoint start = new CompPoint(subj.edges.get(i).start.getX(),subj.edges.get(i).start.getY());
					CompPoint end = new CompPoint(subj.edges.get(next).start.getX(),subj.edges.get(next).start.getY());
					newSubj.addHalfEdge(new DCHalfEdge(start,end));
				}
				return newSubj;
			}
			
			if(type=="merge"){
				for(int i = 0;i<subj.edges.size();i++){
					
					int next = i+1;
					if(next==subj.edges.size()){
						next = 0;
					}
					CompPoint start = new CompPoint(subj.edges.get(i).start.getX(),subj.edges.get(i).start.getY());
					CompPoint end = new CompPoint(subj.edges.get(next).start.getX(),subj.edges.get(next).start.getY());
					newSubj.addHalfEdge(new DCHalfEdge(start,end));
				}
				return clip;
			}
		}

		else if(totalIntersections==0){
			//System.out.println("wholly outside clipping shape");
			if(type=="difference"){
				return null;
			}
			
			if(type=="merge"){
				return subj;
			}
		}

		else{
			//System.out.println("partialIntersection");
			//switch call based on type
			/*for(int i=0;i<subjList.size();i++){
				System.out.println("subList "+i+"="+subjList.get(i)+","+subjList.get(i).getX()+","+subjList.get(i).getY()+" inter="+subjList.get(i).inter+" direction="+subjList.get(i).direction);
			}
			System.out.println();
			for(int i=0;i<clipList.size();i++){
				System.out.println("clipList "+i+"="+clipList.get(i)+","+clipList.get(i).getX()+","+clipList.get(i).getY()+" inter="+clipList.get(i).inter+" direction="+clipList.get(i).direction);
			}*/
			//System.out.println();
			/*for(int i=0;i<enterPoints.size();i++){
				System.out.println("enterPoints "+i+"="+enterPoints.get(i)+","+enterPoints.get(i).getX()+","+enterPoints.get(i).getY()+" inter="+enterPoints.get(i).inter+" direction="+enterPoints.get(i).direction);
			}
			System.out.println();
			for(int i=0;i<exitPoints.size();i++){
				System.out.println("exitPoints "+i+"="+exitPoints.get(i)+","+exitPoints.get(i).getX()+","+exitPoints.get(i).getY()+" inter="+exitPoints.get(i).inter+" direction="+exitPoints.get(i).direction);
			}*/
			//System.out.println();
			if(type=="difference"){
				difference(subj,newSubj,clipList,subjList,enterPoints,exitPoints);
			}
			
			if(type=="merge"){
				//merge(subj,newSubj,clipList,subjList,enterPoints,exitPoints);
			}
			for(int i = 0;i<newSubj.verticies.size();i++){
				int next = i+1;
				if(next==newSubj.verticies.size()){
					next = 0;
				}
				CompPoint start = newSubj.verticies.get(i);
				CompPoint end = newSubj.verticies.get(next);
				newSubj.addHalfEdge(new DCHalfEdge(start,end));
			}
			return newSubj;


			//addInIntersections(startClipEdge,endClipEdge,startSubjEdge,endSubjEdge,clip,subj);
		}
		//System.out.println("totalIntersections="+totalIntersections);
		//System.out.println("==================");
		
		return null;
	
	}



	private static void difference(DoublyConnectedEdgeList subj,DoublyConnectedEdgeList newSubj, LinkedList<BooleanPoint> clipList, LinkedList<BooleanPoint> subjList, LinkedList<BooleanPoint> enterPoints, LinkedList<BooleanPoint> exitPoints){

		
		BooleanPoint endTarget = enterPoints.get(0);
		diffRecurseEntry(endTarget,newSubj,clipList,subjList,endTarget);
		//System.out.println("total verticies="+ newSubj.verticies.size());

	}

	private static void diffRecurseEntry(BooleanPoint transition,DoublyConnectedEdgeList newSubj, LinkedList<BooleanPoint>clipList, LinkedList<BooleanPoint> subjList, BooleanPoint endTarget ){
		
		newSubj.addVertex(new CompPoint(transition.getX(),transition.getY()));
		int startIndex = subjList.indexOf(transition)+1;
		if(startIndex>=subjList.size()){
			startIndex=0;
		}
		while(subjList.get(startIndex).inter==false){
			newSubj.addVertex(new CompPoint(subjList.get(startIndex).getX(),subjList.get(startIndex).getY()));
			startIndex++;
			if(startIndex>=subjList.size()){
				startIndex=0;
			}
		}
		transition = subjList.get(startIndex);
		//System.out.println("subj add is transitioning on:"+transition.direction);
		diffRecurseExit(transition,newSubj,clipList,subjList,endTarget);
				
	
	}
	
private static void diffRecurseExit(BooleanPoint transition,DoublyConnectedEdgeList newSubj, LinkedList<BooleanPoint>clipList, LinkedList<BooleanPoint> subjList, BooleanPoint endTarget ){
		
		newSubj.addVertex(new CompPoint(transition.getX(),transition.getY()));
		int startIndex = clipList.indexOf(transition)+1;
		if(startIndex>=clipList.size()){
			startIndex=0;
		}
		while(clipList.get(startIndex).inter==false){
			newSubj.addVertex(new CompPoint(clipList.get(startIndex).getX(),clipList.get(startIndex).getY()));
			startIndex++;
			if(startIndex>=clipList.size()){
				startIndex=0;
			}
		}
		transition = clipList.get(startIndex);
		//System.out.println("clip add is transitioning on:"+transition.direction);
		if(transition==endTarget){
			//System.out.println("completed!");
		}
		else{
			diffRecurseEntry(transition,newSubj,clipList,subjList,endTarget);
		}
				
	
	}


private static void merge(DoublyConnectedEdgeList subj,DoublyConnectedEdgeList newSubj, LinkedList<BooleanPoint> clipList, LinkedList<BooleanPoint> subjList, LinkedList<BooleanPoint> enterPoints, LinkedList<BooleanPoint> exitPoints){

	
	BooleanPoint endTarget = enterPoints.get(0);
	mergeRecurseEntry(endTarget,newSubj,clipList,subjList,endTarget);
	//System.out.println("total verticies="+ newSubj.verticies.size());

}

private static void mergeRecurseEntry(BooleanPoint transition,DoublyConnectedEdgeList newSubj, LinkedList<BooleanPoint>clipList, LinkedList<BooleanPoint> subjList, BooleanPoint endTarget ){
	
	newSubj.addVertex(new CompPoint(transition.getX(),transition.getY()));
	int startIndex = clipList.indexOf(transition)+1;
	if(startIndex>=clipList.size()){
		startIndex=0;
	}
	while(clipList.get(startIndex).inter==false){
		newSubj.addVertex(new CompPoint(clipList.get(startIndex).getX(),clipList.get(startIndex).getY()));
		startIndex++;
		if(startIndex>=clipList.size()){
			startIndex=0;
		}
	}
	transition = clipList.get(startIndex);
	System.out.println("subj add is transitioning on:"+transition.direction);
	mergeRecurseExit(transition,newSubj,clipList,subjList,endTarget);
			

}

private static void mergeRecurseExit(BooleanPoint transition,DoublyConnectedEdgeList newSubj, LinkedList<BooleanPoint>clipList, LinkedList<BooleanPoint> subjList, BooleanPoint endTarget ){
	
	newSubj.addVertex(new CompPoint(transition.getX(),transition.getY()));
	int startIndex = subjList.indexOf(transition)+1;
	if(startIndex>=subjList.size()){
		startIndex=0;
	}
	while(subjList.get(startIndex).inter==false){
		newSubj.addVertex(new CompPoint(subjList.get(startIndex).getX(),subjList.get(startIndex).getY()));
		startIndex++;
		if(startIndex>=subjList.size()){
			startIndex=0;
		}
	}
	transition = subjList.get(startIndex);
	System.out.println("clip add is transitioning on:"+transition.direction);
	if(transition==endTarget){
		//System.out.println("completed!");
	}
	else{
		mergeRecurseEntry(transition,newSubj,clipList,subjList,endTarget);
	}
			

}



	public static DCHalfEdge clipInBorder(DCHalfEdge edge, DoublyConnectedEdgeList border) { //clips the end of a path so it is within the border

		boolean startInPolygon = Geom.rayPointInPolygon(edge.start, border);
		boolean endInPolygon = Geom.rayPointInPolygon(edge.end, border);
		Vector<DCHalfEdge> intersectedEdges = Geom.edgeIntersectsPolygon(edge, border);

		if (!startInPolygon && !endInPolygon) {

			//myParent.println("deleted edge");

			if (intersectedEdges.size() != 0) {

				CompPoint intersection1 = Geom.findIntersectionPoint(edge, intersectedEdges.get(0));
				CompPoint intersection2 = Geom.findIntersectionPoint(edge, intersectedEdges.get(1));
				DCHalfEdge newEdge = new DCHalfEdge(intersection1, intersection2);
				newEdge.setPartnerEdge("start", intersectedEdges.get(0), intersectedEdges.get(0).start, intersectedEdges.get(0).getPartnerColor("start"));
				newEdge.setPartnerEdge("end", intersectedEdges.get(1), intersectedEdges.get(1).start, intersectedEdges.get(1).getPartnerColor("start"));

				return newEdge;


			} else {
				return null;
			}
		} else if (startInPolygon && endInPolygon) {
			return edge;

		} else {
			DCHalfEdge borderEdge = intersectedEdges.get(0);
			if (startInPolygon && !endInPolygon) {

				edge.end = Geom.findIntersectionPoint(edge, borderEdge);
				edge.setPartnerEdge("end", borderEdge, borderEdge.start, borderEdge.getPartnerColor("start"));

			} else if (!startInPolygon && endInPolygon) {

				edge.start = Geom.findIntersectionPoint(edge, borderEdge);
				edge.setPartnerEdge("start", borderEdge, borderEdge.start, borderEdge.getPartnerColor("start"));



			}
			return edge;

		}
	}
	
	
	public static double scaleVal(double x,double minSource,double maxSource,double minTarget,double maxTarget){//scales a number within a range
			
			      
			        return (((x-minSource)*(maxTarget-minTarget))/(maxSource-minSource))+minTarget;
			    }
	
	
	
	public static void contractPoly (DoublyConnectedEdgeList poly, double dist, double minSource, double maxSource, double minTarget, double maxTarget){
		poly.centroid=Geom.findCentroid(poly);
		double area=Geom.SignedPolygonArea(poly);
		double distMod = scaleVal(area,minSource,maxSource,0,maxTarget)+dist; 
		
		
		Vector<CompPoint> verticies = Geom.removeDuplicateVerts(poly);
		if(distMod<=1){
            for(int i=0;i<verticies.size();i++){
		verticies.get(i).moveToPolar(distMod, poly.centroid);
	     }
            }
		DoublyConnectedEdgeList newSubj = new DoublyConnectedEdgeList();
		
		for(int i = 0;i<verticies.size();i++){
			int next = i+1;
			if(next==verticies.size()){
				next = 0;
			}
			CompPoint start = verticies.get(i);
			CompPoint end = verticies.get(next);
			newSubj.addHalfEdge(new DCHalfEdge(start,end));
		}
		poly.edges = newSubj.edges;
		
		
		
	}
	
	

}


class BooleanPoint extends CompPoint{
	public boolean inter = false;
	public BooleanPoint  linkedInter;
	public String direction;
	public BooleanPoint(double _x, double _y) {
		super(_x, _y);
		// TODO Auto-generated constructor stub
	}
	


}

class ProximitySort implements Comparator<DCHalfEdge>{
	private CompPoint focus;

	ProximitySort(CompPoint focus){
		this.focus = focus;
	}
	
	 public int compare(DCHalfEdge a, DCHalfEdge b) {
	        CompPoint startA = a.start;
	        CompPoint startB = b.start;
	        
	        double distA = Math.sqrt(Math.pow((focus.getX()-startA.getX()),2)+Math.pow((focus.getY()-startA.getY()),2));
	        double distB = Math.sqrt(Math.pow((focus.getX()-startB.getX()),2)+Math.pow((focus.getY()-startB.getY()),2));
	        if(distA<=distB){
	        	return -1;
	        }
	        else{
	        	return 1;
	        }
   
	 }
}

class ProximityPointSort implements Comparator<BooleanPoint>{
	private CompPoint focus;

	ProximityPointSort(CompPoint focus){
		this.focus = focus;
	}
	
	 public int compare(BooleanPoint startA, BooleanPoint startB) {
	    
	        
	        double distA = Math.sqrt(Math.pow((focus.getX()-startA.getX()),2)+Math.pow((focus.getY()-startA.getY()),2));
	        double distB = Math.sqrt(Math.pow((focus.getX()-startB.getX()),2)+Math.pow((focus.getY()-startB.getY()),2));
	        if(distA<=distB){
	        	return -1;
	        }
	        else{
	        	return 1;
	        }
   
	 }	 
}
