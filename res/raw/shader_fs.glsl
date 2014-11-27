precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.
uniform vec3 u_LightPos;       	// The position of the light in eye space.
uniform sampler2D u_Texture;    // The input texture.

varying vec3 v_Position;		// This will be passed into the fragment shader.       		
varying vec2 v_TexCoordinate;			// This will be passed into the fragment shader.          		
varying vec3 v_Normal;			// This will be passed into the fragment shader.

uniform bool u_UseTexture;
//uniform vec4 u_Color;
uniform float u_Ns;
uniform float u_opacity;
uniform vec4 u_KaColor;
uniform vec4 u_KdColor;
uniform vec4 u_KsColor;
		  
// The entry point for our fragment shader.
void main()                    		
{                              
	vec3 normal=normalize(v_Normal);
	// Will be used for attenuation.
    float distance = length(u_LightPos - v_Position);                  
	
	// Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(u_LightPos - v_Position);              	

	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
    float diffuse = max((dot(normal, lightVector)), 0.0);               	  		  													  

	// Add attenuation. 
    //diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));
    
    // Add ambient lighting
    diffuse = diffuse*0.6;
    float ambient=0.4;
    float specular=0.5*pow(max(0.0, dot(reflect(lightVector, normal), vec3(0.0,0.0,-1.0))),u_Ns);
      

	// Multiply the color by the diffuse illumination level and texture value to get final output color.
	//vec4 color=vec4(1.0,0.0,1.0,1.0);
	/*vec4 ka=vec4(0.0,0.0,0.0,0.0);
	vec4 kd=vec4(0.0,0.0,0.0,0.0);
	vec4 ks=vec4(0.0,0.0,0.0,0.0);
	*/
	vec4 ka,kd,ks;
	if(u_UseTexture){
		ka=kd=ks=texture2D(u_Texture, v_TexCoordinate);
   	}
    else{
    	ka=u_KaColor;
    	kd=u_KdColor;
    	ks=u_KsColor;
    }
    	gl_FragColor = vec4(
    		(ambient*ka+
    		diffuse*kd+
    		specular*ks).rgb,/*color.a*u_opacity*/1.0 );
    	//gl_FragColor=vec4(0.5,1.0,0.0,1.0);
    
}                                                                     	

