package neu.lab.evoshell.testgen;

import java.util.ArrayList;
import java.util.List;

import soot.SootMethod;
import soot.Type;

public class MethodVO {
	private String sig;
	private String returnType;
	private String name;
	private boolean isPublic;
	private boolean isPrivate;
	private boolean isStatic;
	private boolean isConstructor;
	private List<String> paramTypes;
	private ClassVO cls;

	public MethodVO(ClassVO cls,SootMethod stMthd) {
		this.cls = cls;
		this.sig = stMthd.getSignature();
		this.returnType = stMthd.getReturnType().toString();
		this.isPublic = stMthd.isPublic();
		this.isPrivate = stMthd.isPrivate();
		this.isStatic = stMthd.isStatic();
		this.name = stMthd.getName();
		this.paramTypes = new ArrayList<String>();
		this.isConstructor = stMthd.isConstructor();
		for (Type param : stMthd.getParameterTypes()) {
			this.paramTypes.add(param.toString());
		}
	}
	
	
	public boolean isPrivate() {
		return isPrivate;
	}


	public boolean isConstructor() {
		return isConstructor;
	}

	public int getParamNum() {
		return paramTypes.size();
	}
	

	public ClassVO getCls() {
		return cls;
	}


	public String getName() {
		return name;
	}


	public List<String> getParamTypes() {
		return paramTypes;
	}


	public boolean isPublic() {
		return isPublic;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public String getSig() {
		return sig;
	}

	public String getReturnType() {
		return returnType;
	}
}
