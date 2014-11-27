precision highp float;

uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.      		       
uniform mat4 u_MVMatrix;		// A constant representing the combined model/view matrix.       		
		  			
attribute vec4 a_Position;		// Per-vertex position information we will pass in.   				
attribute vec2 a_TexCoordinate;			// Per-vertex color information we will pass in. 				
attribute vec3 a_Normal;		// Per-vertex normal information we will pass in.      
		  
varying vec3 v_Position;		// This will be passed into the fragment shader.       		
varying vec2 v_TexCoordinate;			// This will be passed into the fragment shader.          		
varying vec3 v_Normal;			// This will be passed into the fragment shader.  		
		  
// The entry point for our vertex shader.  
void main()                                                 	
{                                           
	// Special process needed to process 3D Models created with ArchiCAD 17, which uses the coordinate 
	// system in which y represents the "forward" direction and z represents the "up" direction
	//a_Position=a_Position.xzyw;
	// Transform the vertex into eye space. 
	
	vec4 pos=a_Position.xzyw;	
	pos.x=-pos.x;
	
	v_Position = vec3(u_MVMatrix * pos);            
		
	v_TexCoordinate = a_TexCoordinate;                              
	
	// Transform the normal's orientation into eye space.
    v_Normal = normalize(vec3(u_MVMatrix * vec4(a_Normal.xzy, 0.0)));
          
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = u_MVPMatrix * pos;           
	       		  
}                                                          