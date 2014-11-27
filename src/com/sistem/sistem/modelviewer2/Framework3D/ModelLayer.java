package com.sistem.sistem.modelviewer2.Framework3D;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.security.KeyException;
import java.util.zip.GZIPInputStream;

import android.opengl.GLES20;
import android.util.Log;

import com.sistem.sistem.modelviewer2.common.TextureHelper;

public class ModelLayer extends Layer {


	static final int FLOATS_PER_VERTEX=8;
	static final int BYTES_PER_VERTEX=FLOATS_PER_VERTEX*Framework3D.BYTES_PER_FLOAT;
	static final int FLOATS_PER_FACE=FLOATS_PER_VERTEX*Framework3D.VERTICES_PER_FACE;
	static final int BYTES_PER_FACE=FLOATS_PER_FACE*Framework3D.BYTES_PER_FLOAT;

	/*
	 Test code(C#): 
	                        using (var fs = File.OpenRead(_outputfile))
	                        {

	                            t_fail(fs.ReadBEInt32() != INT32_3DB3, "invalid header");
	                            //int datasize = fs.ReadBEInt32();
	                            using (var gzips = new System.IO.Compression.GZipStream(fs, System.IO.Compression.CompressionMode.Decompress))
	                            {
	                                int filecount = gzips.ReadBEInt32();
	                                t_assert(filecount == files.Count, "filecount");
	                                for (int i = 0; i < filecount; i++)
	                                {
	                                    t_assert(gzips.ReadShortstring() == System.IO.Path.GetFileName(files[i].filename) && 
	                                    gzips.ReadBEInt32() == files[i].nextfile_g_index, "file " + i.ToString());
	                                }

	                                int groupcount = gzips.ReadBEInt32();
	                                t_assert(groupcount == groups.Count, "groupcount");
	                                var materialsarray = materials.Values.ToArray();
	                                for (int i = 0; i < groupcount; i++)
	                                {
	                                    t_assert(materialsarray[gzips.ReadBEInt32()].name == groups[i].materialname &&
	                                        Convert.ToBoolean(gzips.ReadByte()) == groups[i].smoothed &&
	                                        gzips.ReadBEInt32() == groups[i].nextgroupindex, "file " + i.ToString());
	                                }

	                                int facecount = gzips.ReadBEInt32();
	                                t_assert(facecount == faces.Count, "facecount");
	                                indices[] indices = new indices[3];
	                                for (int i = 0; i < facecount; i++)
	                                {
	                                    indices[0] = faces[i].i1; indices[1] = faces[i].i2; indices[2] = faces[i].i3;
	                                    for (int j = 0; j < 3; j++)
	                                    {
	                                        vec3 _vertice; vec2 _texcoord; vec3 _normal;
	                                        readvertex(gzips, out _vertice, out _texcoord, out _normal);
	                                        t_assert(_vertice == vertices[indices[j].v] &&
	                                            (indices[j].vt == -1 || _texcoord == texcoords[indices[j].vt]) &&
	                                            (indices[j].vn == -1 || _normal == normals[indices[j].vn]), "face " + i.ToString());

	                                    }
	                                }

	                                int materialcount = gzips.ReadBEInt32();
	                                t_assert(materialcount == materialsarray.Length, "materialcount");

	                                for (int i = 0; i < materialcount; i++)
	                                {
	                                    t_assert(
	                                        gzips.ReadBEInt32() == materialsarray[i].index &&
	                                        gzips.ReadShortstring() == materialsarray[i].name &&
	                                        readvec3(gzips) == materialsarray[i].ka &&
	                                        readvec3(gzips) == materialsarray[i].kd &&
	                                        readvec3(gzips) == materialsarray[i].ks &&
	                                        gzips.ReadShortstring() == materialsarray[i].map &&
	                                        gzips.ReadBEFloat() == materialsarray[i].ns &&
	                                        gzips.ReadBEFloat() == materialsarray[i].tr &&
	                                        gzips.ReadBEInt16() == materialsarray[i].illum, "material " + i.ToString());

	                                }

	                            }
	                            appendlog(new logentry(logtype.RESULT, "DEBUG: TEST SUCCESSFUL!!!"));


	                        }
	 */
	file[] files;
	group[] groups;
	//FloatBuffer vertexdata;
	int GPUvertexbuffer;
	Material[] materials;
	final Framework3D m_framework;
	int m_fileid;
	StringMap m_textureids;
	
	public ModelLayer(Framework3D framework, int fileid,StringMap textureids,float[] ModelMatrices,boolean isvisible) throws Exception {
		super(framework, ModelMatrices,isvisible);
		this.m_framework=framework;
		// TODO Auto-generated constructor stub
		m_fileid=fileid;
		m_textureids=textureids;
		/*
		framework.mSingleThreadedExecutor.submit(new Runnable(){
			@Override
			public void run() {			
					// Run on the GL thread -- the same thread the other members of the renderer run in.
					// register to the thread of the surfaceview
					m_framework.surfaceview.queueEvent(new Runnable() {
						@Override
						public void run() {												
							
							// Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
							System.gc();
*/
							try {
								load(m_fileid,m_textureids);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							/*													
						}				
					});
			}
			
			
		});	
		*/
	}
	@Override
	void onsurfacecreated(){
		ShaderProgram.prepare();
	}
	private void load(int fileid, StringMap textureids) throws IOException, KeyException {
			
		
		InputStream is=App.getContext().getResources().openRawResource(fileid);
		 try {
			byte[] tempbuf=new byte[4];

            final int INT32_3DB3 = 0x33444203;// 3, D, B, 0x03 = magic number;
            int readbytes=0;
            do{
            	int newbytes=is.read(tempbuf, 0, tempbuf.length);
            	if(newbytes<0)throw new IOException();
            	readbytes+=newbytes;
            	
            } while(readbytes<tempbuf.length);
			int magic=getbeint32(tempbuf,0);
			if(readbytes<tempbuf.length||magic!=INT32_3DB3)throw new IOException();
			//int datasize=getbeint32(tempbuf,4);
			GZIPInputStream zis = new GZIPInputStream(new BufferedInputStream(is));
			try	{
				/*byte[] data=new byte[datasize];
				zis.read(data);*/
				DataInputStream dis=new DataInputStream(zis);
				
                int filecount = dis.readInt();
				files=new file[filecount];
				int previndex_group=0;
                for (int i = 0; i < filecount; i++)
                {
                	files[i]=new file();
                	files[i].name=readshortstring(dis);
                	int nextindex_group=dis.readInt();
                	files[i].groupistart=previndex_group;
                	files[i].groupcount=nextindex_group-previndex_group;
                	previndex_group=nextindex_group;
                }
                
                int groupcount = dis.readInt();
                groups=new group[groupcount];
                int previndex_face=0;
                for (int i = 0; i < groupcount; i++)
                {
                	groups[i]=new group();
                    groups[i].materialindex=dis.readInt();
                    groups[i].smoothed=dis.readBoolean();
                    int nextindex_face=dis.readInt();
                    groups[i].faceistart=previndex_face;
                    groups[i].facecount=nextindex_face-previndex_face;
                	previndex_face=nextindex_face;
                }
                
                int facecount = dis.readInt();
                byte[] buffer=new byte[6000*BYTES_PER_FACE];
                float[] testbuf=new float[24];
                int cursor=0;
                ByteBuffer filebuffer=ByteBuffer.allocateDirect(facecount*BYTES_PER_FACE);
                FloatBuffer vertexbuffer=filebuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();
                while(cursor<facecount*BYTES_PER_FACE) {
                	
                	int read=Math.min(facecount*BYTES_PER_FACE-cursor,buffer.length);
                	dis.readFully(buffer,0, read);
                	vertexbuffer.put(ByteBuffer.wrap(buffer,0,read).order(ByteOrder.BIG_ENDIAN).asFloatBuffer());
                	
                	cursor+=read;
                }
                //byte[] filedata=new byte[BYTES_PER_FACE*facecount];
                //dis.readFully(filedata);
                
                buffer=null;
                //vertexbuffer.put(ByteBuffer.wrap(filedata).order(ByteOrder.BIG_ENDIAN).asFloatBuffer());
                //filedata=null;
                //System.gc();
                vertexbuffer.position(0);
                
                final int GPUbuffers[] = new int[1];
        		GLES20.glGenBuffers(1, GPUbuffers, 0);						
        		GPUvertexbuffer=GPUbuffers[0];
        		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,GPUvertexbuffer);
        		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexbuffer.capacity() * Framework3D.BYTES_PER_FLOAT, vertexbuffer, GLES20.GL_STATIC_DRAW);			
        		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
                
        		//filedata=null;
                vertexbuffer.limit(0);
                vertexbuffer=null;

                int materialcount = dis.readInt();
                materials=new Material[materialcount];
                for (int i = 0; i < materialcount; i++)
                {
                	int index=dis.readInt();
                	materials[index]=new Material();
                	materials[index].index=index;
                	materials[index].name=readshortstring(dis);
                	for(int j=0; j<9; j++)
                		materials[index].color_coeffs[j]=dis.readFloat();
                	String texturename=readshortstring(dis);
                	if(texturename.length()!=0){
                		int textureid=textureids.get(texturename);
                		int texturehandle=TextureHelper.loadTexture(App.getContext(), textureid);
                    	materials[index].texture= texturehandle;
                    	GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);			
                		
                		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturehandle);		
                		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		
                		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturehandle);		
                		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);		
                		GLES20.glBindBuffer(GLES20.GL_TEXTURE_2D, 0);
                	}
                	else {
                		materials[index].texture=-1;
                	}
            		
            		materials[index].color_coeffs[9]=dis.readFloat();  //Ns
            		materials[index].color_coeffs[10]=dis.readFloat();  //Dissolvance
            		materials[index].illum=dis.readShort();  
                }
                
				
			}
			finally{
				zis.close();
			}
		 } finally {
			is.close();
		 }
	}
	/*
	{
           int filecount = 1;
			files=new file[filecount];
			int previndex_group=0;
           for (int i = 0; i < filecount; i++)
           {
           	files[i]=new file();
           	files[i].name="TEST_CUBE.obj";
           	int nextindex_group=6;
           	files[i].groupistart=previndex_group;
           	files[i].groupcount=nextindex_group-previndex_group;
           	previndex_group=nextindex_group;
           }
           
           int groupcount = 6;
           groups=new group[groupcount];
           int previndex_face=0;
           for (int i = 0; i < groupcount; i++)
           {
           	groups[i]=new group();
               groups[i].materialindex=0;
               groups[i].smoothed=false;
               int nextindex_face=2*i+2;
               groups[i].faceistart=previndex_face;
               groups[i].facecount=nextindex_face-previndex_face;
           	previndex_face=nextindex_face;
           }
           
           int facecount = 12;
           float[] filedataf=new float[facecount*FLOATS_PER_FACE];
           testcreatecubedata(filedataf);
           FloatBuffer vertexbuffer=ByteBuffer.allocateDirect(facecount*BYTES_PER_FACE).order(ByteOrder.nativeOrder()).asFloatBuffer();
           vertexbuffer.put(filedataf);
           vertexbuffer.position(0);
           
           final int GPUbuffers[] = new int[1];
   		GLES20.glGenBuffers(1, GPUbuffers, 0);						
   		GPUvertexbuffer=GPUbuffers[0];
   		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,GPUvertexbuffer);
   		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexbuffer.capacity() * Framework3D.BYTES_PER_FLOAT, vertexbuffer, GLES20.GL_STATIC_DRAW);			
   		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
           
   		filedataf=null;
           vertexbuffer.limit(0);
           vertexbuffer=null;

           int materialcount = 1;
           materials=new Material[materialcount];
           for (int i = 0; i < materialcount; i++)
           {
           	int index=i;
           	materials[index]=new Material();
           	materials[index].index=index;
           	materials[index].name="TEST_CUBE_MATERIAL";
           	for(int j=0; j<9; j++)
           		materials[index].color_coeffs[j]=0f;
           	String texturename="�ݶޒ���ς� alpha.jpg";
           	if(texturename.length()!=0){
           		int textureid=TextureHelper.loadTexture(App.getContext(), textureids.get(texturename));
               	materials[index].texture= textureid;
               	GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);			
           		
           		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid);		
           		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		
           		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid);		
           		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);		
           		GLES20.glBindBuffer(GLES20.GL_TEXTURE_2D, 0);
           	}
           	else {
           		materials[index].texture=-1;
           	}
       		
       		materials[index].color_coeffs[9]=1;  
       		materials[index].color_coeffs[10]=0;  
       		materials[index].illum=2;  
           }
           
}
	
	if(true)return;
	*/
	static void testcreatecubedata(float[] array){
		int index=0;
		index=testcreatecubeface(array,index,2,3,0,1);
		index=testcreatecubeface(array,index,6,7,2,3);
		index=testcreatecubeface(array,index,7,5,3,1);
		index=testcreatecubeface(array,index,4,6,0,2);
		index=testcreatecubeface(array,index,5,4,1,0);
		index=testcreatecubeface(array,index,7,6,5,4);
	}
	static int testcreatecubeface(float[] array,int index, int v1,int v2,int v3, int v4){
		index=testcreatecubevertex(array, index, v1,0,v1,v2,v3,v4);
		index=testcreatecubevertex(array, index, v2,1,v1,v2,v3,v4);
		index=testcreatecubevertex(array, index, v3,2,v1,v2,v3,v4);
		index=testcreatecubevertex(array, index, v3,2,v1,v2,v3,v4);
		index=testcreatecubevertex(array, index, v2,1,v1,v2,v3,v4);
		index=testcreatecubevertex(array, index, v4,3,v1,v2,v3,v4);
		return index;
	}
	static final float size=20f;
	static int testcreatecubevertex(float[] array,int index,int v, int vindinface,  int v1,int v2,int v3, int v4){
		array[index++]=(v&1)*size;
		array[index++]=((v>>1)&1)*size;
		array[index++]=((v>>2)&1)*size;
		array[index++]=(vindinface&1);
		array[index++]=((vindinface&2)>>1);
		
		int vand=v1&v2&v3&v4;
		int vorxor=(v1|v2|v3|v4)^7;
		
		if((vand&1)==1)
			array[index++]=1f;
		else if((vorxor&1)==1)
			array[index++]=-1f;
		else array[index++]=0;
		
		if(((vand>>=1)&1)==1)
			array[index++]=1f;
		else if(((vorxor>>=1)&1)==1)
			array[index++]=-1f;
		else array[index++]=0;
		
		if(((vand>>=1)&1)==1)
			array[index++]=1f;
		else if(((vorxor>>=1)&1)==1)
			array[index++]=-1f;
		else array[index++]=0;
		return index;
	}
	
	static int getbeint32(byte[] array,int index){
		return array[index+3]|array[index+2]<<8|array[index+1]<<16|array[index+0]<<24;
	}
	static byte[] stringbuf=new byte[255];
	static String readshortstring(InputStream stream) throws IOException{
		int length=stream.read();
		if(length==0)return "";
		int readbytes=0;
        do{
        	int newbytes=stream.read(stringbuf,readbytes,length-readbytes);
        	if(newbytes<0)throw new IOException();
        	readbytes+=newbytes;
        	
        } while(readbytes<length);
		return Charset.forName("UTF-8").decode(ByteBuffer.wrap(stringbuf,0,length)).toString().trim();
	}
	
	@Override
	protected void dispose(){

		//Releasing GPU buffers;
		final int[] buffersToDelete = new int[] { GPUvertexbuffer };
		GLES20.glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
		
	}
	@Override
	public void Render(View view, Light light) {
		// TODO Auto-generated method stub
		//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		ShaderProgram.use();

		synchronized(view.framework.lockobj){
			ShaderProgram.setMatrices(view,Modelmatrix);
			ShaderProgram.setLight(light,view);
		}
		ShaderProgram.setVertices(GPUvertexbuffer);
		//int dummy=0;
		for(int i=0;i<files.length;i++){
			file file=files[i];
			if(file.Dissolvance>=1.0f)continue;
			for(int j=file.groupistart;j<file.groupistart+file.groupcount;j++){
				group group=groups[j];
				if(group.Dissolvance>=1.0f)continue;
				Material material=materials[group.materialindex];
				/*if(material.color_coeffs[0]<=0.1&&material.color_coeffs[1]<=0.1&&material.color_coeffs[2]<=0.1)
				{
					dummy++;
					continue;
				}*/
				//if(material.color_coeffs[10]/*dissolvance*/>0)continue;
				ShaderProgram.setMaterial(material,(1-file.Dissolvance)*(1-group.Dissolvance));
				ShaderProgram.setTexture(material.texture);
				//Draw
				final int blocksize=21845;
				for (int k = 0; k < group.facecount; k+=blocksize) { 	
					GLES20.glDrawArrays(GLES20.GL_TRIANGLES,(group.faceistart+k)*Framework3D.VERTICES_PER_FACE,	(Math.min(blocksize, group.facecount-k))*Framework3D.VERTICES_PER_FACE);	
				}
			}
		}
		//System.out.println(dummy);
		
		
	}

}
class file{
public file(){}
public String name;
public int groupistart;
public int groupcount;
public float Dissolvance=0;
}
class group{

	public group(){}
	public int materialindex;
	public boolean smoothed;
	public int faceistart;
	public int facecount;
	public float Dissolvance=0;
}
class Material{
	public Material(){}
	public int index;
	public String name;
	public float[] color_coeffs=new float[11];//(ka[3],kd[3],ks[3],ns(specular coefficient alpha),tr(dissolvance=1-opacity))
	public int texture;
	public short illum;
	
	
}

