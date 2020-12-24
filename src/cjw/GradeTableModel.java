package cjw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayLispackage cjw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.stream.Stream;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

public class GradeTableModel extends AbstractTableModel{

	private static final long serialVersionUID = 127756560822363832L;

	static final String[] header = {"번호", "이름", "국어", "영어", "수학", "평균"};
	private Vector<Double> average = new Vector<Double>();
	private Vector<Grade> gradeList = new Vector<Grade>(); // 성적 클래스의 배열

	// 생성자: 인스턴스 1개 제한
	private static GradeTableModel instance = new GradeTableModel();
	private GradeTableModel() {} //생성자 사용 금지
	public static GradeTableModel getInstance() {return instance;} //getInstance 메소드로만 인스턴스 호출가능



	//고유 메소드


	//파일을 열어 이 클래스의 grade 리스트에 추가하는 메소드
	byte open(File file, String encoding){
		BufferedReader reader = null;
		try { //파일 읽기 시도
			//FileInputStream (UTF-8)로 읽은 뒤 InputStreamReader -> BufferedReader로 이동
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
		}catch(FileNotFoundException e) {
			//System.out.println("파일을 찾을 수 없습니다.");
			return 1;
		}catch(UnsupportedEncodingException e) {
			//System.out.println("지원되지 않는 인코딩");
			return 2;
		}catch(SecurityException e) {
			//System.out.println("권한이 부족합니다.");
			return 4;
		}finally {
			if(reader != null) {
				//파일 가공하기
				try {
					// gradeList 초기화
					this.gradeList.clear();
					this.average.clear();
					Stream<String> s = reader.lines(); // BufferedReader에 있는 모든 내용을 Stream으로 이동
					Object[] oStream = s.toArray(); //Stream의 내용을 Object 배열 (stringStream)로 이동
					for(int i = 1; i<oStream.length; i++) { 
						if (oStream[i]==null) { //만약 oStream에 null이 있으면 
							continue;
						}else {
							String original = (String)oStream[i]; //oStream의 한 줄을 뽑아
							String[] sArr = original.split(","); // ,를 기준으로 배열을 만들고
							// 만들어진 배열을 바탕으로 Grade 클래스에 들어갈 내용 생성
							int studentNo = -1, korean = -1, english = -1, math = -1;
							String studentName = "";
							if(sArr[0] !=null) studentNo = Integer.parseInt(sArr[0].trim());
							if(sArr[1] !=null) studentName = sArr[1];
							if(sArr[2] !=null) korean = Integer.parseInt(sArr[2].trim()); 
							if(sArr[3] !=null) english = Integer.parseInt(sArr[3].trim());
							if(sArr[4] !=null) math = Integer.parseInt(sArr[4].trim());
							Grade g = new Grade(studentNo, studentName, korean, english, math);
							// 만들어진 Grade 클래스를 gradeList에 삽입
							this.gradeList.add(g);
							this.average.add(getAverage(i-1));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return 3;
				} finally {//마지막으로 BufferedReader 닫기
					if (reader != null) try {
						reader.close();} catch(Exception ex) {ex.printStackTrace();}
				}
			}
		}
		return 0;
	}

	// 평균 구하기
	public double getAverage(int row) {
		Grade g = this.gradeList.get(row);
		String s = String.format("%.1f", (g.korean+g.english+g.math)/3.0);
		Double d = Double.parseDouble(s);
		return d;

	}

	// 파일 저장하기
	byte save(File file, String encoding){
		int gradeLen = gradeList.size();
		BufferedWriter writer=null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			writer.write("학생번호,학생이름,국어,영어,수학"+"\n");
			for(int i = 0; i<gradeLen; i++) {
				Grade g = gradeList.get(i);
				writer.write(g.toString()+"\n");
			}
			writer.flush();
		}catch(FileNotFoundException e) {
			return 1;
		}catch(UnsupportedEncodingException e) {
			return 2;
		}catch(SecurityException e) {
			return 4;
		}catch(IOException e) {
			//System.out.println("파일을 쓸 수 없습니다.");
			return 5;
		}finally {
			if(writer != null) try{ writer.close();} catch(Exception ex) {}
		}

		return 0;
	}


	//추상 클래스 구현부
	@Override
	public int getRowCount() {
		return gradeList.size();
	}

	@Override
	public int getColumnCount() {
		return header.length;
	}

	@Override
	public String getColumnName(int column) {
		return header[column];
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		Grade g = this.gradeList.get(row);
		switch(column) {
		case 0: g.studentNo = (int)aValue; fireTableCellUpdated(row, column); this.gradeList.set(row,g); break;
		case 1: g.studentName = (String)aValue; fireTableCellUpdated(row, column); this.gradeList.set(row,g); break;
		case 2: g.korean = (int)aValue; fireTableCellUpdated(row, column); this.gradeList.set(row,g); break;
		case 3: g.english = (int)aValue; fireTableCellUpdated(row, column); this.gradeList.set(row,g); break;
		case 4: g.math = (int)aValue; fireTableCellUpdated(row, column); this.gradeList.set(row,g); break;
		case 5: this.average.set(row, (Double)aValue); break;
		default: System.out.println("출력될 일이 없다");
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Grade g = this.gradeList.get(rowIndex);
		switch(columnIndex) {
		case 0: return g.studentNo;
		case 1: return g.studentName;
		case 2: return g.korean;
		case 3: return g.english;
		case 4: return g.math;
		case 5: return average.get(rowIndex);
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 5) return false;
		else return true;
	}

	@Override
	public Class<?> getColumnClass (int c) {
		return getValueAt(0, c).getClass();

	}

	public void addRow(int row, Grade rowData) {
			gradeList.add(row+1, rowData);
			int length = getRowCount();
			average.clear();
			for(int i = 0; i<length; i++) average.add(getAverage(i));
			fireTableRowsInserted(0, getRowCount()-1);

	}

	public void removeRow(int[] tmRows, int[] tbRows) {
		ArrayList<Grade> tmRowsAL = new ArrayList<Grade>();
		ArrayList<Double> tmRowsAL2 = new ArrayList<Double>();
		for(int i: tmRows) {
			tmRowsAL.add(gradeList.get(i)); 
			tmRowsAL2.add(average.get(i));
		}
		gradeList.removeAll(tmRowsAL);
		//average.removeAll(tmRowsAL2);
		int minTbRow = Integer.MAX_VALUE;
		int maxTbRow = Integer.MIN_VALUE;
		for(int i: tbRows) {
			if(i>maxTbRow) maxTbRow=i;
			if(i<minTbRow) minTbRow=i;
		}
		System.out.printf("(%d, %d)",minTbRow,maxTbRow);
		fireTableRowsDeleted(minTbRow, maxTbRow);
	}
}
t;
import java.util.stream.Stream;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

public class GradeTableModel extends AbstractTableModel{

	private static final long serialVersionUID = 127756560822363832L;

	static final String[] header = {"번호", "이름", "국어", "영어", "수학", "평균"};
	private ArrayList<Double> average = new ArrayList<Double>();
	private ArrayList<Grade> gradeList = new ArrayList<Grade>(); // 성적 클래스의 배열

	// 생성자: 인스턴스 1개 제한
	private static GradeTableModel instance = new GradeTableModel();
	private GradeTableModel() {} //생성자 사용 금지
	public static GradeTableModel getInstance() {return instance;} //getInstance 메소드로만 인스턴스 호출가능



	//고유 메소드


	//파일을 열어 이 클래스의 grade 리스트에 추가하는 메소드
	byte open(File file, String encoding){
		BufferedReader reader = null;
		try { //파일 읽기 시도
			//FileInputStream (UTF-8)로 읽은 뒤 InputStreamReader -> BufferedReader로 이동
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
		}catch(FileNotFoundException e) {
			//System.out.println("파일을 찾을 수 없습니다.");
			return 1;
		}catch(UnsupportedEncodingException e) {
			//System.out.println("지원되지 않는 인코딩");
			return 2;
		}catch(SecurityException e) {
			//System.out.println("권한이 부족합니다.");
			return 4;
		}finally {
			if(reader != null) {
				//파일 가공하기
				try {
					// gradeList 초기화
					this.gradeList.clear();
					this.average.clear();
					Stream<String> s = reader.lines(); // BufferedReader에 있는 모든 내용을 Stream으로 이동
					Object[] oStream = s.toArray(); //Stream의 내용을 Object 배열 (stringStream)로 이동
					for(int i = 1; i<oStream.length; i++) { 
						if (oStream[i]==null) { //만약 oStream에 null이 있으면 
							continue;
						}else {
							String original = (String)oStream[i]; //oStream의 한 줄을 뽑아
							String[] sArr = original.split(","); // ,를 기준으로 배열을 만들고
							// 만들어진 배열을 바탕으로 Grade 클래스에 들어갈 내용 생성
							int studentNo = -1, korean = -1, english = -1, math = -1;
							String studentName = "";
							if(sArr[0] !=null) studentNo = Integer.parseInt(sArr[0].trim());
							if(sArr[1] !=null) studentName = sArr[1];
							if(sArr[2] !=null) korean = Integer.parseInt(sArr[2].trim()); 
							if(sArr[3] !=null) english = Integer.parseInt(sArr[3].trim());
							if(sArr[4] !=null) math = Integer.parseInt(sArr[4].trim());
							Grade g = new Grade(studentNo, studentName, korean, english, math);
							// 만들어진 Grade 클래스를 gradeList에 삽입
							this.gradeList.add(g);
							this.average.add(getAverage(i-1));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return 3;
				} finally {//마지막으로 BufferedReader 닫기
					if (reader != null) try {
						reader.close();} catch(Exception ex) {ex.printStackTrace();}
				}
			}
		}
		return 0;
	}

	// 평균 구하기
	public double getAverage(int row) {
		Grade g = this.gradeList.get(row);
		String s = String.format("%.1f", (g.korean+g.english+g.math)/3.0);
		Double d = Double.parseDouble(s);
		return d;

	}

	// 파일 저장하기
	byte save(File file, String encoding){
		int gradeLen = gradeList.size();
		BufferedWriter writer=null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			writer.write("학생번호,학생이름,국어,영어,수학"+"\n");
			for(int i = 0; i<gradeLen; i++) {
				Grade g = gradeList.get(i);
				writer.write(g.toString()+"\n");
			}
			writer.flush();
		}catch(FileNotFoundException e) {
			return 1;
		}catch(UnsupportedEncodingException e) {
			return 2;
		}catch(SecurityException e) {
			return 4;
		}catch(IOException e) {
			//System.out.println("파일을 쓸 수 없습니다.");
			return 5;
		}finally {
			if(writer != null) try{ writer.close();} catch(Exception ex) {}
		}

		return 0;
	}


	//추상 클래스 구현부
	@Override
	public int getRowCount() {
		return gradeList.size();
	}

	@Override
	public int getColumnCount() {
		return header.length;
	}

	@Override
	public String getColumnName(int column) {
		return header[column];
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		Grade g = this.gradeList.get(row);
		switch(column) {
		case 0: g.studentNo = (int)aValue; fireTableCellUpdated(row, column); this.gradeList.set(row,g); break;
		case 1: g.studentName = (String)aValue; fireTableCellUpdated(row, column); this.gradeList.set(row,g); break;
		case 2: g.korean = (int)aValue; fireTableCellUpdated(row, column); this.gradeList.set(row,g); break;
		case 3: g.english = (int)aValue; fireTableCellUpdated(row, column); this.gradeList.set(row,g); break;
		case 4: g.math = (int)aValue; fireTableCellUpdated(row, column); this.gradeList.set(row,g); break;
		case 5: this.average.set(row, (Double)aValue); break;
		default: System.out.println("출력될 일이 없다");
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Grade g = this.gradeList.get(rowIndex);
		switch(columnIndex) {
		case 0: return g.studentNo;
		case 1: return g.studentName;
		case 2: return g.korean;
		case 3: return g.english;
		case 4: return g.math;
		case 5: return average.get(rowIndex);
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 5) return false;
		else return true;
	}

	@Override
	public Class<?> getColumnClass (int c) {
		return getValueAt(0, c).getClass();

	}

	public void addRow(int row, Grade rowData) {
			gradeList.add(row+1, rowData);
			int length = getRowCount();
			average.clear();
			for(int i = 0; i<length; i++) average.add(getAverage(i));
			fireTableRowsInserted(0, getRowCount()-1);

	}

	public void removeRow(int tmRow, int tbRow) {
		gradeList.remove(tmRow);
		average.remove(tmRow);
		fireTableRowsDeleted(tbRow, tbRow);
	}
}
