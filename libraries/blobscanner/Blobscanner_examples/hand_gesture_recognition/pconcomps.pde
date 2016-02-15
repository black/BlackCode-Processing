/*
 * This software is part of Blobscanner Processing library examples.
 * Hand detection and gesture recognition 
 * (c) Antonio Molinaro 2011 http://code.google.com/p/blobscanner/.
 *
 * Simple Binary Connected components 
 * (c) 2006 Andrew Senior http://www.andrewsenior.com
 * (Removed two bugs from the original version by Antonio Molinaro)
 * Preserve this header when redistributing. 
 */
 
class pconcomps
{
  int []  piLabels;
  int  lIndex;
  int []  plEquivalent;
  int []  plAreas;
  int  lIndicesAllocated;
  int  iHeight,  iWidth; 
  
  // Take an image and label the components in it internally 
  void Compute(PImage Im)
    {
       iWidth=Im.width; 
       iHeight=Im.height;
       piLabels=new int[Im.width * Im.height];
       lIndex=1;
       lIndicesAllocated=10;
       plEquivalent= new int [ lIndicesAllocated];
       plEquivalent[0]=0;
      int iIndex=0;
      for(int j=0; j<Im.height; j++)
      {
	for(int i=0; i<Im.width; i++, iIndex++)
	{
 
 	  if (i>0 && red(Im.pixels[iIndex])==0)  // Same colour as the left pixel
 
	       piLabels[iIndex]=0;
	    else
	    {
	      if (i>0 && (red(Im.pixels[iIndex-1])!=0))  // Same colour as the left pixel
	      {
		 piLabels[iIndex]= piLabels[iIndex-1];
		if (j>0 
		    && (red(Im.pixels[iIndex-Im.width])!=0) 
		    &&  piLabels[iIndex]!= piLabels[iIndex-Im.width])  // Two regions  different label adjoin
		  MarkEquivalent( piLabels[iIndex],  piLabels[iIndex-Im.width]);
	      }
	      else   // Same colour as the  pixel below
		if (j>0 && (red(Im.pixels[iIndex-Im.width])!=0))
		   piLabels[iIndex]= piLabels[iIndex-Im.width];
		else  // No neighbours - start a new component
		{
		   piLabels[iIndex]= lIndex;  // The new component will have this label
		  NewComponent(); // All non BG components are considered same colour ie 1
		}
	    }
	}
      }
      RelabelEquivalents();       
    }

  void MarkEquivalent(int i, int j)
    {
      // Find the ultimate parent (equivalency) of each component
      int iT= plEquivalent[i];
      int iS= plEquivalent[j];
      // sort them 
      if (iT>iS) 
      {
	int l=iT;
	iT=iS;
	iS=l;
      }
      for( int k=iS; k< lIndex; k++)  // Overwrite the highest (iS) with the lowest (iT)
	if ( plEquivalent[k]==iS)
	   plEquivalent[k]=iT;
    }
  void ReallocIndices()
    {
      int [] pTemp=new int[ lIndicesAllocated+10];
      for(int i=0; i< lIndicesAllocated; i++)
	pTemp[i]= plEquivalent[i];
       plEquivalent=pTemp;
       lIndicesAllocated+=10;
    }

  void NewComponent()
    {
      if ( lIndex>= lIndicesAllocated)
	ReallocIndices();
       plEquivalent[ lIndex]= lIndex;  // itself later it might get relabelled to a lower numbered component when they're found to touch
       lIndex++; // total number === Next component to allocate 
    }
  void RelabelEquivalents()
    {
      // First go through and find the active components
      // iUsed keeps track of which ones are a "root" of equivalence
      // Everything, including the root is now mapped into an index so that 
      // only the first iUsed indices are used. 
      int lOldMax= lIndex;
      int k=0;
      int iUsed=0;
      for(k=0; k< lIndex; k++)
	if ( plEquivalent[k]==k) // A "root" 
	{
	  if (iUsed!=k)  // We need to relabel all equivalents to point to this now too
	  {
	    for( int l=k; l< lIndex; l++)
	      if ( plEquivalent[l]==k)                // this is safe because iUsed<k
		 plEquivalent[l]=iUsed;  
	  }
	  iUsed++;
	}
       lIndex=iUsed;

       plAreas = new int [ lIndex];
// println(" in Relable components=" +  lIndex);
      if ( lIndex==0)
	return;
      int iIndex=0;
      for(int  y=0; y< iHeight; y++)
	for(int  x=0; x< iWidth; x++, iIndex++)
	{
	  int lNewClass= plEquivalent[ piLabels[iIndex]];
	   plAreas[lNewClass]++;
	   piLabels[iIndex]=lNewClass;
	}
    }

  // Remove components with area less than lSize
  void RemoveSmall(int lSize)
    {
      int k;
      int iDeleted=0;
      if ( lIndex==0)
	return;

      // Go through components in reverse order
      for(k= lIndex-1; k>=1; k--)
	if ( plAreas[k]<lSize ) // a non-background region not touching tbe main background component
	{
	  DeleteComponent(k);
	  iDeleted++;
	}
      //   println("Deleted "+iDeleted+" componnents leaving "+ lIndex);
    }

  // Delete a given component
  void DeleteComponent(int k)
    {
      int iIndex=0;
      for(int  y=0; y< iHeight; y++)
	for(int  x=0; x< iWidth; x++, iIndex++)
	{
	  if ( piLabels[iIndex]==k)
	     piLabels[iIndex]=0;
	  else if ( piLabels[iIndex]>k)
	     piLabels[iIndex]= piLabels[iIndex]-1;
	} 

  for(int k2=k; k2< lIndex-1; k2++)
	 plAreas[k2]= plAreas[k2+1];
       lIndex--;
    }

  // Copy the current appearance (remaining components) to an image
  void SetImage(PImage Im)
    {
      int iIndex=0;
      for(int  y=0; y< iHeight; y++)
	for(int  x=0; x< iWidth; x++, iIndex++)
	{
	  if ( piLabels[iIndex]==0)
	    Im.pixels[iIndex]=color(0,0,0);
	  else 
	    Im.pixels[iIndex]=color(255,255,255);
	}
    }
}  
