package com.sistem.sistem.modelviewer2.Framework3D;

import java.nio.*;

import android.os.Debug;

public class PolygonUtil {

	static final float EPSILON=0.0000000001f;

	public static  float area(float[] polygon)
	{
		int n = polygon.length/2;
		float A=0.0f;
		for(int p=n-1,q=0; q<n; p=q++)
			A+= polygon[p*2]*polygon[q*2+1] - polygon[p*2+1]*polygon[q*2];
		return A*0.5f;
	}

	/*
     InsideTriangle decides if a point P is Inside of the triangle
     defined by A, B, C.
	 */
	public static boolean isInsideTriangle(float Ax, float Ay,
			float Bx, float By,
			float Cx, float Cy,
			float Px, float Py)

	{
		float ax, ay, bx, by, cx, cy, apx, apy, bpx, bpy, cpx, cpy;
		float cCROSSap, bCROSScp, aCROSSbp;

		ax = Cx - Bx;  ay = Cy - By;
		bx = Ax - Cx;  by = Ay - Cy;
		cx = Bx - Ax;  cy = By - Ay;
		apx= Px - Ax;  apy= Py - Ay;
		bpx= Px - Bx;  bpy= Py - By;
		cpx= Px - Cx;  cpy= Py - Cy;

		aCROSSbp = ax*bpy - ay*bpx;
		cCROSSap = cx*apy - cy*apx;
		bCROSScp = bx*cpy - by*cpx;

		return ((aCROSSbp > 0.0f) && (bCROSScp > 0.0f) && (cCROSSap > 0.0f));
		
		
	};
	public static boolean isInsideTriangle(float[] pol,int a,int b,int c,int p) {
		return isInsideTriangle(pol[a*2], pol[a*2+1], pol[b*2], pol[b*2+1], pol[c*2], pol[c*2+1], pol[p*2], pol[p*2+1]);
	}
	private static  int intersects(float x1,float y1,float x2,float y2,float x3,float y3,float x4,float y4) {

		// First find Ax+By=C values for the two lines
		double A1 = y2 - y1;
		double B1 = x1 - x2;
		double C1 = A1 * x1 + B1 * y1;

		double A2 = y4 - y3;
		double B2 = x3 - x4;
		double C2 = A2 * x3 + B2 * y3;

		double det = (A1 * B2) - (A2 * B1);

		if (Math.abs(det) <= 1) {
			// Lines are either parallel, are collinear (the exact same
			// segment), or are overlapping partially, but not fully
			// To see what the case is, check if the endpoints of one line
			// correctly satisfy the equation of the other (meaning the two
			// lines have the same y-intercept).
			// If no endpoints on 2nd line can be found on 1st, they are
			// parallel.
			// If any can be found, they are either the same segment,
			// overlapping, or two segments of the same line, separated by some
			// distance.
			// Remember that we know they share a slope, so there are no other
			// possibilities

			// Check if the segments lie on the same line
			// (No need to check both points)
			if ((A1 * x3) + (B1 * y3) == C1) {
				// They are on the same line, check if they are in the same
				// space
				// We only need to check one axis - the other will follow
				if ((Math.min(x1, x2) < x3)
						&& (Math.max(x1, x2) > x3))
					return 0;//true;

				// One end point is ok, now check the other
				if ((Math.min(x1, x2) < x4)
						&& (Math.max(x1, x2) > x4))
					return 0;//true;

				// They are on the same line, but there is distance between them
				return 0;
			}

			// They are simply parallel
			return 0;
		} else {
			double Dx=x3-x1,Dy=y3-y1;
			
			double u= (Dx*A2+Dy*B2)/det;
			double v= (Dx*A1+Dy*B1)/det;
			if(0<u&&u<1) {
				if(0<v&&v<1)return 1;
				else if(v==0)return -1;
				else if(v==1)return -2;
				else return 0;
			}
			else return 0;
			
			
			/*
			// Lines DO intersect somewhere, but do the line segments intersect?
			double x = (B2 * C1 - B1 * C2) / det;
			double y = (A1 * C2 - A2 * C1) / det;

			// Make sure that the intersection is within the bounding box of
			// both segments
			boolean flag1=Math.abs(x2-x1)>Math.abs(y2-y1),flag2=Math.abs(x4-x3)>Math.abs(y4-y3);
			if ((flag1?(x > Math.min(x1, x2) && x < Math.max(x1,
					x2)):(x >= Math.min(x1, x2) && x <= Math.max(x1,
							x2)))
					&& (flag1?(y >= Math.min(y1, y2) && y <= Math.max(
							y1, y2)):(y > Math.min(y1, y2) && y < Math.max(
							y1, y2)))) {
				// We are within the bounding box of the first line segment,
				// so now check second line segment
				if ((flag2?(x > Math.min(x3, x4) && x < Math.max(x3,
						x4)):(x >= Math.min(x3, x4) && x <= Math.max(x3,
								x4)))
						&& (flag2?(y >= Math.min(y3, y4) && y < Math.max(
								y3, y4)):(y > Math.min(y3, y4) && y < Math.max(
										y3, y4)))) {
					// The line segments do intersect
					return true;
				}
			}
*/
			// The lines do intersect, but the line segments do not
		}
	}
private static int intersects(float[] pol, int s1,int e1,int s2,int e2) {
	return intersects(pol[s1*2],pol[s1*2+1],pol[e1*2],pol[e1*2+1],pol[s2*2],pol[s2*2+1],pol[e2*2],pol[e2*2+1]);
}
	public static  boolean snip(float[] polygon,int u,int v,int w,int n,int[] V)
	{
		int p;
		float Ax, Ay, Bx, By, Cx, Cy, Px, Py;

		Ax = polygon[V[u]*2];
		Ay = polygon[V[u]*2+1];

		Bx = polygon[V[v]*2];
		By = polygon[V[v]*2+1];

		Cx = polygon[V[w]*2];
		Cy = polygon[V[w]*2+1];

		if ( EPSILON > (((Bx-Ax)*(Cy-Ay)) - ((By-Ay)*(Cx-Ax))) ) return false;

		for (p=0;p<n;p++)
		{
			if( (p == u) || (p == v) || (p == w) ) continue;
			Px = polygon[V[p]*2];
			Py = polygon[V[p]*2+1];
			if (isInsideTriangle(Ax,Ay,Bx,By,Cx,Cy,Px,Py)) return false;
		}

		return true;
	}

	public static  int triangulate(float[] polygon,ShortBuffer result,int indexoffset)
	{
		/* allocate and initialize list of Vertices in polygon */
		int ret=0;
		int n = polygon.length/2;
		if ( n < 3 ) return 0;

		int[] V = new int[n];

		/* we want a counter-clockwise polygon in V */

		if ( 0.0f < area(polygon) )
			for (int v=0; v<n; v++) V[v] = v;
		else
			for(int v=0; v<n; v++) V[v] = (n-1)-v;

		int nv = n;

		/*  remove nv-2 Vertices, creating 1 triangle every time */
		int count = 2*nv;   /* error detection */

		for(int m=0, v=nv-1; nv>2; )
		{
			/* if we loop, it is probably a non-simple polygon */
			if (0 >= (count--))
			{
				//** Triangulate: ERROR - probable bad polygon!
				return 0;
			}

			/* three consecutive vertices in current polygon, <u,v,w> */
			int u = v  ; if (nv <= u) u = 0;     /* previous */
			v = u+1; if (nv <= v) v = 0;     /* new v    */
			int w = v+1; if (nv <= w) w = 0;     /* next     */

			if ( snip(polygon,u,v,w,nv,V) )
			{
				int a,b,c,s,t;

				/* true names of the vertices */
				a = V[u]; b = V[v]; c = V[w];

				/* output Triangle */
				result.put( (short) (a+indexoffset) );
				result.put( (short) (b+indexoffset) );
				result.put( (short) (c+indexoffset) );
				ret+=3;
				m++;

				/* remove v from remaining polygon */
				for(s=v,t=v+1;t<nv;s++,t++) V[s] = V[t]; nv--;

				/* resest error detection counter */
				count = 2*nv;
			}
		}

		return ret;
	}

	public static float dot(float[] polygon, int base,int p1,int p2) {
		return (polygon[p2*2]-polygon[base*2])*(polygon[p1*2]-polygon[base*2])+(polygon[p2*2+1]-polygon[base*2+1])*(polygon[p1*2+1]-polygon[base*2+1]);
	}
	public static float cross(float[] polygon, int base,int p1,int p2) {
		return (polygon[p1*2]-polygon[base*2])*(polygon[p2*2+1]-polygon[base*2+1])-(polygon[p2*2]-polygon[base*2])*(polygon[p1*2+1]-polygon[base*2+1]);
	}
	public static  void triangulatenaive(float[] polygon,ShortBuffer result,int indexoffset)
	{
		/* allocate and initialize list of Vertices in polygon */
		int ret=0;
		int n = polygon.length/2;
		Kong(polygon,n,result,indexoffset);
		/*if ( n < 3 ) return 0;

		//int[] V = new int[n*2];
		int[] N=new int[n];
		float[] Vy=new float[n];
		

		/* we want a counter-clockwise polygon in V */

		/*if ( 0.0f < area(polygon) )
			for (int v=0; v<n; v++) V[v+n]=V[v] = v;
		else
			for(int v=0; v<n; v++) V[v+n]=V[v] = (n-1)-v;*/
		/*for(int v=0;v<n-1;v++)N[v]=v|(v+1)%n<<8|(v+(n-1))%n<<16;
		for(int v=0;v<n;v++)Vy[v]=polygon[v*2+1];
		
		
		MergeSort(Vy, N);

		rec_triangulate(polygon,n,0, N, Vy,result);
		

		return ret;*/
	}
	
	static boolean isEar(float[] pol,/*int[] concave,int concavecount,*/int[] next,int[] prev,int p0, int p1, int p2, float crossscale) {
		if(crossscale*cross(pol, p1, p0, p2)>=0) {
			/*for (int j = 0; j < concavecount; j++) {
				if(concave[j]!=p0&&concave[j]!=p1&&concave[j]!=p2&&isInsideTriangle(pol, p0, p1, p2, concave[j]))
					return false;
				}*/
			for(int i=next[p2];i!=prev[p0];i=next[i]) {
				int check=intersects(pol,p0,p2,i,next[i]);
				if(check==1)return false;
				else if(check==-1) {if((cross(pol,prev[i],p0,p2)>0)^(cross(pol,next[i],p0,p2)>0))return false;}
				else if(check==-2) {if((cross(pol,i,p0,p2)>0)^(cross(pol,next[next[i]],p0,p2)>0))return false;}
			}
			return true;
		}else return false;
	}
	static void Kong(float[] pol, int n,ShortBuffer result,int indexoffset){
		int p=0;
		int remainingcount=n;
		
		
		
		//boolean[] used=new boolean[n];
		int[] prev=new int[n];
		int[] next=new int[n];
		for(int i=0;i<n;i++) {prev[i]=(i+n-1)%n;next[i]=(i+1)%n;}
		

		float maxx=-Float.MAX_VALUE,maxy=-Float.MAX_VALUE;
		int max=-1;
		for (int i = 0; i < n; i++) {
			float x=pol[i*2],y=pol[i*2+1];
			if(x>maxx||(x==maxx&&y>maxy)) {
				maxx=x;
				maxy=y;
				max=i;
			}
		}
		float scale=cross(pol, max, prev[max], next[max]);
		/*
		int[] concave=new int[n];
		int concavecount=0;
		for (int i = 0; i < n; i++) 
			if(scale*cross(pol, i, prev[i], next[i])<0) concave[concavecount++]=i;*/
	    while(remainingcount>3) {
			    if( isEar(pol,/*concave,concavecount,*/next,prev,prev[p], p, next[p],scale)) {
			            result.put((short) (prev[p]+indexoffset));
			            result.put((short) (p+indexoffset));
			            result.put((short) (next[p]+indexoffset));
			            next[prev[p]]=next[p];
			            prev[next[p]]=prev[p];
			            remainingcount--;
			            p=prev[p];
			        }
			    else
			        p=next[p];
	        }
		result.put((short) (prev[p]+indexoffset));
	    result.put((short) (p+indexoffset));
	    result.put((short) (next[p]+indexoffset));
	    }


	static int BinarySearch(float[] array, float key,int count){
		return BinarySearch(array,key,0,count);
	}
	static int BinarySearch(float[] array, float key,int start, int end){
		int mid=0;
		int count=end-start;
		while(count>0){
			mid=(count>>1)+start;
			float compare=key-array[mid];
			if(compare>0)start=mid+1;
			else if(compare<0)end=mid;
			else return mid;
			count=end-start;
		}
		return ~start;
	}
	static int BinarySearchInsertion(float[] array, float key,int start, int end){
		int mid=0;
		int count=end-start;
		while(count>0){
			mid=(count>>1)+start;
			float compare=key-array[mid];
			if(compare>=0)start=mid+1;
			else end=mid;
			count=end-start;
		}
		return start;
	}
	
	static long BinarySearchStarttoEnd(float[] array, float key,int count){
		return BinarySearchStarttoEnd(array,key,0,count);
	}
	static long BinarySearchStarttoEnd(float[] array, float key,int start, int end){

		int count=end-start;
		int mid=0;
		int high=-1;
		int low=-1;
		
		while(count>0){
			
			mid=(count>>1)+start;
			float compare=key-array[mid];
			if(compare>0)start=mid+1;
			else if(compare<0)end=mid;
			else{
			//Correspondence
				high=low=mid;
				count=end-high;
				while(count>0){
					mid=(count>>1)+high;
					compare=key-array[mid];
					if(compare<0)end=mid;
					else high=mid+1;
					count=end-high;
				}
				count=low-start;
				while(count>0){
					mid=(count>>1)+start;
					compare=key-array[mid];
					if(compare>0)start=mid+1;
					else low=mid;
					count=end-high;
				}
				return low<<32&high;
			}
			count=end-start;
		}
		return ~start;
		
	}
	static void MergeSort(float[] V, int[] P){
		MergeSort(V,P,V.clone(),P.clone(),0,V.length);
	}
	static void Insert(float[] array,float item,int index,int end){
		for(int i=end;i>index;i--){
			array[i]=array[i-1];
		}
		array[index]=item;
	}
	static void Insert(int[] array,int item,int index,int end){
		for(int i=end;i>index;i--){
			array[i]=array[i-1];
		}
		array[index]=item;
	}
	static void MergeSort(float[] V, int[] P,float[] Vbuf, int[] Pbuf,int start,int count){

		int end=start+count;
		if(count<=8){
			for(int i=start+1;i<end;i++)
			{
				int insindex=BinarySearchInsertion(V,V[i],start,i);
				Insert(V,V[i],insindex,i);
				Insert(V,V[i],insindex,i);
			}
			return;
		}
		int half=count>>1;
		int mid=start+half;
		MergeSort(Vbuf,Pbuf,V,P,start,half);
		MergeSort(Vbuf,Pbuf,V,P,mid,count-half);
		int i1=start;
		int i2=mid;
		int i=start;
		if(Vbuf[mid]-Vbuf[mid-1]>=0){
			System.arraycopy(Vbuf, start, V, start, count);
			System.arraycopy(Vbuf, start, V, start, count);
		}else{
		while(i1<mid&&i2<end){
			float compare=Vbuf[i1]-Vbuf[i2];
			if(compare>0){
				V[i]=Vbuf[i2];
			V[i]=Vbuf[i2++];
			}
			else {
				V[i]=Vbuf[i1];
				V[i]=Vbuf[i1++];
				}
			i++;
			
		}
			System.arraycopy(Vbuf, i2, V, i, end-i2);
			System.arraycopy(Vbuf, i2, V, i, end-i2);
			i+=end-i2;
			System.arraycopy(Vbuf, i1, V, i, mid-i1);
			System.arraycopy(Vbuf, i1, V, i, mid-i1);
		}
		
		
		
	}

}
