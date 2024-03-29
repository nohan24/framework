package etu2075;

public class FileUpload {
    String name;
	String path;
	byte[] bytes;

	public FileUpload(){}

	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}

	public void setPath( String path ){
		this.path = path;
	}

	public String getPath(){
		return this.path;
	}

	public void setBytes( byte[] bytes ){
		this.bytes = bytes;
	}
	public byte[] getBytes(){
		return this.bytes;
	}

	public String toString(){
		return this.getName() + " [ " + this.getBytes().length + " ]";
	}
}
