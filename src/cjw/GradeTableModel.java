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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.stream.Stream;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

public class GradeTableModel extends AbstractTableModel{

	private static final long serialVersionUID = 127756560822363832L;

	static final String[] header = {"번호", "이름", "국어", "영어", "수학", "평균"};
	private Vector<Object[]> gradeList = new Vector<Object[]>();

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
							Object[] temp = new Object[2];
							temp[0] = g;
							temp[1] = getAverage(g);
							this.gradeList.add(temp);
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
		try {
			Grade g = (Grade) this.gradeList.get(row)[0];
			String s = String.format("%.1f", (g.korean+g.english+g.math)/3.0);
			Double d = Double.parseDouble(s);
			return d;
		}catch(Exception e) {
			return 0;
		}

	}
	public double getAverage(Grade g) {
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
				try {
					Grade g = (Grade) gradeList.get(i)[0];
					writer.write(g.toString()+"\n");
				}
				catch (Exception e) {

				}
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
		try {
			Object[] temp = this.gradeList.get(row);
			Grade g = (Grade)temp[0];
			switch(column) {
			case 0: 
				g.studentNo = (int)aValue;
				temp[0] = g;
				gradeList.set(row, temp);
				fireTableCellUpdated(row, column); 
				break;
			case 1:
				g.studentName = (String)aValue;
				temp[0] = g;
				gradeList.set(row, temp);
				fireTableCellUpdated(row, column);
				break;
			case 2:
				g.korean = (int)aValue;
				temp[0] = g;
				gradeList.set(row, temp);
				fireTableCellUpdated(row, column);
				break;
			case 3:
				g.english = (int)aValue;
				temp[0] = g;
				gradeList.set(row, temp);
				fireTableCellUpdated(row, column);
				break;
			case 4: 
				g.math = (int)aValue; 
				temp[0] = g;
				gradeList.set(row, temp);
				fireTableCellUpdated(row, column);
				break;
			case 5: 
				temp[1] = (Double)aValue;
				gradeList.set(row, temp);
				break;
			default: System.out.println("출력될 일이 없다");
			}
		}catch (Exception e) {

		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			System.out.printf("gradeList.get(%d)\n", rowIndex);
			Object[] temp = gradeList.get(rowIndex);
			Grade g = (Grade)temp[0];
			switch(columnIndex) {
			case 0: return g.studentNo;
			case 1: return g.studentName;
			case 2: return g.korean;
			case 3: return g.english;
			case 4: return g.math;
			case 5: return temp[1];
			}
		}catch(Exception e) {
			switch(columnIndex) {
			case 0: return 0;
			case 1: return "";
			case 2: return 0;
			case 3: return 0;
			case 4: return 0;
			case 5: return 1.0;
			}
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
		Object temp[] = new Object[2];
		temp[0] = rowData;
		temp[1] = getAverage(rowData);
		gradeList.add(row+1, temp);
		fireTableRowsInserted(0, getRowCount()-1);

	}

	public void removeRow(int[] tmRows, int[] tbRows) {
		Vector<Object[]> targets = new Vector<Object[]>();
		for(int i: tmRows) {
			Object[] temp= gradeList.get(i);
			targets.add(temp);
		}
		gradeList.removeAll(targets);
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
