package cjw;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelpackage cjw;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;


public class Main extends JFrame implements TableModelListener, FocusListener, ActionListener{
	private static final long serialVersionUID = 9180188918596466310L;


	public static void main(String[] args) {
		Main m = new Main();
		m.setDefaultCloseOperation(EXIT_ON_CLOSE); //종료 이벤트 처리
	}
	/* 메인 클래스에 사용할 오브젝트들 모음 */
	private File file;
	//GradeManager g = GradeManager.getInstance();
	private final int width = 550, height = 550; //창의 크기
	private JScrollPane scroll;
	private GradeTableModel tm = GradeTableModel.getInstance(); //테이블 모델
	private JTable table; //테이블 모델을 기반으로 만들 테이블
	//컴포넌트를 붙이기 위한 박스 생성
	private final Box leftBox = Box.createVerticalBox();
	private final Box rightBox = Box.createVerticalBox();
	private static final Box center = Box.createHorizontalBox();
	//중앙 패널에 붙이기 위한 측면 패널들
	private JPanel leftPanel = new JPanel(new BorderLayout());
	private JPanel rightPanel = new JPanel(new BorderLayout());
	//컴포넌트 생성
	private final JButton openBtn = new JButton("열기");
	private final JButton saveBtn = new JButton("저장");
	private final JButton saveAsBtn = new JButton("Save as..");
	private final JButton addBtn = new JButton("행 추가");
	private final JButton delBtn = new JButton("행 삭제");
	//파일 선택창
	private final FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Seperated Values (.csv)", "csv");
	JFileChooser fc = new JFileChooser(".");


	//메인 클래스 생성자
	private Main() {
		super("성적 관리");
		//파일 선택창에 필터 씌우기
		fc.setFileFilter(filter);

		//컴포넌트에 ActionListener 붙이기
		openBtn.addActionListener(this);
		saveBtn.addActionListener(this);
		saveAsBtn.addActionListener(this);
		addBtn.addActionListener(this);
		delBtn.addActionListener(this);

		//레이아웃 설정
		setLayout(new BorderLayout());

		//컴포넌트 부착 (컴포넌트 ->박스 -> 좌,우 패널-> 중앙패널)
		leftBox.add(Box.createVerticalStrut(0));
		leftBox.add(openBtn, 1);
		leftBox.add(saveBtn, 2);
		leftBox.add(saveAsBtn, 3);
		leftBox.add(addBtn, 4);
		leftBox.add(delBtn, 5);
		leftPanel.setBorder(new TitledBorder(new EtchedBorder(), "조작"));
		leftPanel.add(leftBox, BorderLayout.CENTER);
		add(leftPanel);
		center.add(leftBox);
		add(center, BorderLayout.CENTER);

		//창의 크기, 위치 지정
		Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(width, height);
		setLocation((res.width/2)-(width/2), 
				(res.height/2)-(height/2)); //창을 화면의 중앙으로

		//창을 보이게함
		setVisible(true);	
	}

	//메인 고유 메소드

	//파일 열기
	void openFile(Main m) {
		byte successCode = tm.open(file, "euc-kr");
		if(successCode == 0) {
			createTable();
		}else {
			printErrorCode(successCode);
		}
	}

	//테이블 생성
	void createTable() {
		//테이블로 출력할 콘텐츠 추출
		table = new JTable(tm);
		//테이블 설정
		table.setFillsViewportHeight(true); //테이블이 항상 창 높이의 100%를 차지하도록
		tm.addTableModelListener(this); //테이블 변경을 감지하는 인터페이스 장착
		table.setAutoCreateRowSorter(true); //테이블 정렬 기능 활성화
		//열 별 설정
		for(int i = 1; i < GradeTableModel.header.length; i++) {
			//정수를 받는 컬럼이라면 - IntegerEditor로 바꾸고 , FocusListener 붙이기
			TableColumn tc = table.getColumn(GradeTableModel.header[i]);
			if( tm.getColumnClass(i) == Integer.class) {
				tc.setCellEditor(new IntegerEditor(0,100));
			}else if(tm.getColumnClass(i) == String.class) {
				//문자열을 받는 컬럼이라면 - FocusListener 붙이기
				JTextField jt = new JTextField();
				jt.addFocusListener(this);
				tc.setCellEditor(new DefaultCellEditor(jt));
			}
		}
		//만든 테이블 부착
		//만약 기존 테이블이 있다면 그 테이블은 떼기
		Component[] c = rightBox.getComponents();
		for(int i = 0; i < c.length; i++) {
			rightBox.remove(i);
		}
		//만든 테이블은 JScrollPane에 부착 -> JScrollPane을 rightBox에 부착 -> rightPanel에 부착 ->  center에 부착
		scroll = new JScrollPane(table);
		rightBox.add(scroll);
		rightPanel.add(rightBox);
		center.add(rightPanel);
		setVisible(true);
	}
	//저장 메시지 오류 출력
	void printErrorCode(byte successCode) {
		switch(successCode) {
		case 0: 
			JOptionPane.showMessageDialog(center, "명령을 성공적으로 수행했습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
			break;
		case 1: 
			JOptionPane.showMessageDialog(center, " 파일을 찾을 수 없습니다.", "파일 열기 실패", JOptionPane.ERROR_MESSAGE);
			break;
		case 2:
			JOptionPane.showMessageDialog(center, " 잘못된 인코딩 형식입니다.", "파일 열기 실패", JOptionPane.ERROR_MESSAGE);
			break;
		case 3:
			JOptionPane.showMessageDialog(center, " 잘못된 파일 형식입니다.", "파일 열기 실패", JOptionPane.ERROR_MESSAGE);
			break;
		case 4:
			JOptionPane.showMessageDialog(center, " 파일을 여는데 필요한 권한이 부족합니다. \n 관리자 모드로 다시 실행해보세요.", "파일 열기 실패", JOptionPane.ERROR_MESSAGE);
			break;
		case 5:
			JOptionPane.showMessageDialog(center, "파일이 이미 사용중일 수 있습니다. ", "파일 저장 실패", JOptionPane.ERROR_MESSAGE);
			break;
		default: JOptionPane.showMessageDialog(center, " 정의되지 않은 오류입니다", "파일 IO 실패", JOptionPane.ERROR_MESSAGE);
		}
	}



	//인터페이스 구현부  -- 이벤트 처리


	int fRow = -1, col = -1;

	//JTable
	@Override
	//테이블 변경이 감지시
	public void tableChanged(TableModelEvent e) { 
		tm.removeTableModelListener(this);  //스택오버플로우 방지용

		fRow = e.getFirstRow();
		col = e.getColumn();
		if (col == 5) return;

		//평균 재계산

		if (col==2 || col==3 || col==4) {
			tm.setValueAt(tm.getAverage(fRow), fRow, 5);
		}
		tm.addTableModelListener(this);
	}


	//CellEditor
	@Override
	//DefautCellEditor 포커스시 실행되는 메소드
	public void focusGained(FocusEvent fe) {
		if (!(fe.getSource() instanceof JTextField)) {System.out.println("원하는 인스턴스가 아님"); return;}
		JTextField txt = (JTextField)fe.getComponent();
		//전체선택
		txt.selectAll();
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
	}

	//EventObject
	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		String actionCommand = e.getActionCommand();

		if(s.getClass() == JButton.class) {
			if(actionCommand == openBtn.getActionCommand()) { 
				//열기 버튼 눌렀을때 - fileChooser 부르자
				int returnVal = fc.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {//파일을 제대로 골랐을때
					file = fc.getSelectedFile();
					openFile(this);
				}
				return;
			}
			else if(actionCommand == saveBtn.getActionCommand()) { 
				//저장 버튼 눌렀을때
				if(file != null) {
					byte successCode = tm.save(file, "euc-kr");
					printErrorCode(successCode);
					return;
				}else {
					JOptionPane.showMessageDialog(center, "파일을 먼저 열어주세요.", "정보", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			else if(actionCommand == saveAsBtn.getActionCommand()) { 
				//다른 이름으로 저장 버튼 눌렀을때
				if(file != null) {
					int returnVal = fc.showSaveDialog(this);
					if(returnVal == JFileChooser.APPROVE_OPTION) {//파일을 제대로 골랐을때
						String savepath = fc.getSelectedFile().toString();
						String extension = null;
						try {extension = savepath.substring(savepath.lastIndexOf(".csv"));}catch (Exception ex){}
						if(extension != ".csv") {
							//확장자가 csv가 아닐때 csv로 만들어준다
							savepath = savepath.concat(".csv");
						}
						file = new File(savepath);
						byte successCode = tm.save(file, "euc-kr");
						printErrorCode(successCode);
						if(successCode == 0) openFile(this);
					}
					return;
				}else {
					JOptionPane.showMessageDialog(center, "파일을 먼저 열어주세요.", "정보", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}else if(actionCommand == addBtn.getActionCommand()) {
				if(file != null) {
					int tableRow = table.getSelectedRow();
					Grade g = new Grade(tableRow+2, "김아무개" + (tableRow+2), 100, 100, 100);
					tm.addRow(tableRow, g);
					table.clearSelection();
					int tmRow = table.convertRowIndexToView(tableRow+1);
					table.addRowSelectionInterval(tmRow, tmRow);
				}else {
					JOptionPane.showMessageDialog(center, "파일을 먼저 열어주세요.", "정보", JOptionPane.INFORMATION_MESSAGE);
				}
			}else if(actionCommand == delBtn.getActionCommand()) {
				if(file!=null) {
					int[] rows = table.getSelectedRows();
					table.clearSelection();
					int tmRow;
					for (int i: rows) {
						try {
							tmRow = table.convertRowIndexToModel(i);
							System.out.println("테이블 선택"+i +"테이블 모델 선텍"+tmRow);
							tm.removeRow(tmRow, i);
							
						} catch(Exception ex){
							continue;
						}
					}
				}else {
					JOptionPane.showMessageDialog(center, "파일을 먼저 열어주세요.", "정보", JOptionPane.INFORMATION_MESSAGE);
				}
			}else {
				System.out.println("뭐누른거야?");
			}
		}

	}



}
Listener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;


public class Main extends JFrame implements TableModelListener, FocusListener, ActionListener{
	private static final long serialVersionUID = 9180188918596466310L;


	public static void main(String[] args) {
		Main m = new Main();
		m.setDefaultCloseOperation(EXIT_ON_CLOSE); //종료 이벤트 처리
	}
	/* 메인 클래스에 사용할 오브젝트들 모음 */
	private File file;
	//GradeManager g = GradeManager.getInstance();
	private final int width = 550, height = 550; //창의 크기
	private JScrollPane scroll;
	private GradeTableModel tm = GradeTableModel.getInstance(); //테이블 모델
	private JTable table; //테이블 모델을 기반으로 만들 테이블
	//컴포넌트를 붙이기 위한 박스 생성
	private final Box leftBox = Box.createVerticalBox();
	private final Box rightBox = Box.createVerticalBox();
	private static final Box center = Box.createHorizontalBox();
	//중앙 패널에 붙이기 위한 측면 패널들
	private JPanel leftPanel = new JPanel(new BorderLayout());
	private JPanel rightPanel = new JPanel(new BorderLayout());
	//컴포넌트 생성
	private final JButton openBtn = new JButton("열기");
	private final JButton saveBtn = new JButton("저장");
	private final JButton saveAsBtn = new JButton("Save as..");
	private final JButton addBtn = new JButton("행 추가");
	private final JButton delBtn = new JButton("행 삭제");
	//파일 선택창
	private final FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Seperated Values (.csv)", "csv");
	JFileChooser fc = new JFileChooser(".");


	//메인 클래스 생성자
	private Main() {
		super("성적 관리");
		//파일 선택창에 필터 씌우기
		fc.setFileFilter(filter);

		//컴포넌트에 ActionListener 붙이기
		openBtn.addActionListener(this);
		saveBtn.addActionListener(this);
		saveAsBtn.addActionListener(this);
		addBtn.addActionListener(this);
		delBtn.addActionListener(this);

		//레이아웃 설정
		setLayout(new BorderLayout());

		//컴포넌트 부착 (컴포넌트 ->박스 -> 좌,우 패널-> 중앙패널)
		leftBox.add(Box.createVerticalStrut(0));
		leftBox.add(openBtn, 1);
		leftBox.add(saveBtn, 2);
		leftBox.add(saveAsBtn, 3);
		leftBox.add(addBtn, 4);
		leftBox.add(delBtn, 5);
		leftPanel.setBorder(new TitledBorder(new EtchedBorder(), "조작"));
		leftPanel.add(leftBox, BorderLayout.CENTER);
		add(leftPanel);
		center.add(leftBox);
		add(center, BorderLayout.CENTER);

		//창의 크기, 위치 지정
		Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(width, height);
		setLocation((res.width/2)-(width/2), 
				(res.height/2)-(height/2)); //창을 화면의 중앙으로

		//창을 보이게함
		setVisible(true);	
	}

	//메인 고유 메소드

	//파일 열기
	void openFile(Main m) {
		byte successCode = tm.open(file, "euc-kr");
		if(successCode == 0) {
			createTable();
		}else {
			printErrorCode(successCode);
		}
	}

	//테이블 생성
	void createTable() {
		//테이블로 출력할 콘텐츠 추출
		table = new JTable(tm);
		//테이블 설정
		table.setFillsViewportHeight(true); //테이블이 항상 창 높이의 100%를 차지하도록
		tm.addTableModelListener(this); //테이블 변경을 감지하는 인터페이스 장착
		table.setAutoCreateRowSorter(true); //테이블 정렬 기능 활성화
		//열 별 설정
		for(int i = 1; i < GradeTableModel.header.length; i++) {
			//정수를 받는 컬럼이라면 - IntegerEditor로 바꾸고 , FocusListener 붙이기
			TableColumn tc = table.getColumn(GradeTableModel.header[i]);
			if( tm.getColumnClass(i) == Integer.class) {
				tc.setCellEditor(new IntegerEditor(0,100));
			}else if(tm.getColumnClass(i) == String.class) {
				//문자열을 받는 컬럼이라면 - FocusListener 붙이기
				JTextField jt = new JTextField();
				jt.addFocusListener(this);
				tc.setCellEditor(new DefaultCellEditor(jt));
			}
		}
		//만든 테이블 부착
		//만약 기존 테이블이 있다면 그 테이블은 떼기
		Component[] c = rightBox.getComponents();
		for(int i = 0; i < c.length; i++) {
			rightBox.remove(i);
		}
		//만든 테이블은 JScrollPane에 부착 -> JScrollPane을 rightBox에 부착 -> rightPanel에 부착 ->  center에 부착
		scroll = new JScrollPane(table);
		rightBox.add(scroll);
		rightPanel.add(rightBox);
		center.add(rightPanel);
		setVisible(true);
	}
	//저장 메시지 오류 출력
	void printErrorCode(byte successCode) {
		switch(successCode) {
		case 0: 
			JOptionPane.showMessageDialog(center, "명령을 성공적으로 수행했습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
			break;
		case 1: 
			JOptionPane.showMessageDialog(center, " 파일을 찾을 수 없습니다.", "파일 열기 실패", JOptionPane.ERROR_MESSAGE);
			break;
		case 2:
			JOptionPane.showMessageDialog(center, " 잘못된 인코딩 형식입니다.", "파일 열기 실패", JOptionPane.ERROR_MESSAGE);
			break;
		case 3:
			JOptionPane.showMessageDialog(center, " 잘못된 파일 형식입니다.", "파일 열기 실패", JOptionPane.ERROR_MESSAGE);
			break;
		case 4:
			JOptionPane.showMessageDialog(center, " 파일을 여는데 필요한 권한이 부족합니다. \n 관리자 모드로 다시 실행해보세요.", "파일 열기 실패", JOptionPane.ERROR_MESSAGE);
			break;
		case 5:
			JOptionPane.showMessageDialog(center, "파일이 이미 사용중일 수 있습니다. ", "파일 저장 실패", JOptionPane.ERROR_MESSAGE);
			break;
		default: JOptionPane.showMessageDialog(center, " 정의되지 않은 오류입니다", "파일 IO 실패", JOptionPane.ERROR_MESSAGE);
		}
	}



	//인터페이스 구현부  -- 이벤트 처리


	int fRow = -1, col = -1;

	//JTable
	@Override
	//테이블 변경이 감지시
	public void tableChanged(TableModelEvent e) { 
		tm.removeTableModelListener(this);  //스택오버플로우 방지용

		fRow = e.getFirstRow();
		col = e.getColumn();
		if (col == 5) return;

		//평균 재계산

		if (col==2 || col==3 || col==4) {
			tm.setValueAt(tm.getAverage(fRow), fRow, 5);
		}
		tm.addTableModelListener(this);
	}


	//CellEditor
	@Override
	//DefautCellEditor 포커스시 실행되는 메소드
	public void focusGained(FocusEvent fe) {
		if (!(fe.getSource() instanceof JTextField)) {System.out.println("원하는 인스턴스가 아님"); return;}
		JTextField txt = (JTextField)fe.getComponent();
		//전체선택
		txt.selectAll();
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
	}

	//EventObject
	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		String actionCommand = e.getActionCommand();

		if(s.getClass() == JButton.class) {
			if(actionCommand == openBtn.getActionCommand()) { 
				//열기 버튼 눌렀을때 - fileChooser 부르자
				int returnVal = fc.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {//파일을 제대로 골랐을때
					file = fc.getSelectedFile();
					openFile(this);
				}
				return;
			}
			else if(actionCommand == saveBtn.getActionCommand()) { 
				//저장 버튼 눌렀을때
				if(file != null) {
					byte successCode = tm.save(file, "euc-kr");
					printErrorCode(successCode);
					return;
				}else {
					JOptionPane.showMessageDialog(center, "파일을 먼저 열어주세요.", "정보", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			else if(actionCommand == saveAsBtn.getActionCommand()) { 
				//다른 이름으로 저장 버튼 눌렀을때
				if(file != null) {
					int returnVal = fc.showSaveDialog(this);
					if(returnVal == JFileChooser.APPROVE_OPTION) {//파일을 제대로 골랐을때
						String savepath = fc.getSelectedFile().toString();
						String extension = null;
						try {extension = savepath.substring(savepath.lastIndexOf(".csv"));}catch (Exception ex){}
						if(extension != ".csv") {
							//확장자가 csv가 아닐때 csv로 만들어준다
							savepath = savepath.concat(".csv");
						}
						file = new File(savepath);
						byte successCode = tm.save(file, "euc-kr");
						printErrorCode(successCode);
					}
					return;
				}else {
					JOptionPane.showMessageDialog(center, "파일을 먼저 열어주세요.", "정보", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}else if(actionCommand == addBtn.getActionCommand()) {
				if(file != null) {
					int tableRow = table.getSelectedRow();
					Grade g = new Grade(tableRow+2, "김아무개" + (tableRow+2), 100, 100, 100);
					tm.addRow(tableRow, g);
					table.clearSelection();
					int tmRow = table.convertRowIndexToView(tableRow+1);
					table.addRowSelectionInterval(tmRow, tmRow);
				}else {
					JOptionPane.showMessageDialog(center, "파일을 먼저 열어주세요.", "정보", JOptionPane.INFORMATION_MESSAGE);
				}
			}else if(actionCommand == delBtn.getActionCommand()) {
				if(file!=null) {
					int[] rows = table.getSelectedRows();
					table.clearSelection();
					int tmRow;
					for (int i: rows) {
						try {
							tmRow = table.convertRowIndexToModel(i);
							System.out.println("테이블 선택"+i +"테이블 모델 선텍"+tmRow);
							tm.removeRow(tmRow, i);
							
						} catch(Exception ex){
							continue;
						}
					}
				}else {
					JOptionPane.showMessageDialog(center, "파일을 먼저 열어주세요.", "정보", JOptionPane.INFORMATION_MESSAGE);
				}
			}else {
				System.out.println("뭐누른거야?");
			}
		}

	}



}
