package cjw;

public class Grade {
	
	int studentNo=0;
	String studentName=null;
	int korean=0;
	int english=0;
	int math=0;
	
	Grade(int studentNo, String studentName, int korean, int english, int math){
		this.studentNo = studentNo;
		this.studentName = studentName;
		this.korean = korean;
		this.english = english;
		this.math = math;
	}
	
	@Override
	public String toString() {
		return studentNo +","+studentName +","+korean +","+english +","+math;
	}
	
}
