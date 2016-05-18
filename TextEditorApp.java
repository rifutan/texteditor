
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;


@SuppressWarnings("serial")
public class TextEditorApp extends JFrame implements ChangeListener, ItemListener {
	public static void main(String[] arg) {
		new TextEditorApp( );
	}

	JComboBox<String>  fontchoice;
	JSlider  slider;
	JLabel  size;
	Menu  menu1;
	Menu  menu2;
	MenuBar  bar;
	LineNumberedTextArea  textarea;
	JFileChooser fc;
	JTextField search;
	JTextField areplace;
	JTextField breplace;
	transient Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

	
	public TextEditorApp( ) {
		super( "TextEditor Applicaton");
		setSize( 1200, 800 );
		
		setLayout( new BorderLayout() );
		
		// property panel(pp)は、プロパティを設定するコンポーネントが置かれる
		JPanel pp = new JPanel(); // 画面の上部に配置するもの
	    pp.setBackground(Color.CYAN);
	    JPanel pp2 = new JPanel(); // 画面の下部に配置するもの
	    pp2.setBackground(Color.ORANGE);
	    Container contentPane = getContentPane();
	    contentPane.add(pp, BorderLayout.NORTH);
	    contentPane.add(pp2, BorderLayout.SOUTH);
		
		// フォント選択メニュー
		pp.add( new Label( "Font:") );
		fontchoice = new JComboBox<String>();
		fontsetup();
		fontchoice.addItemListener( this );
		pp.add( fontchoice );
		
		// フォントサイズ調整のスライダー
		pp.add( new Label( "Size:") );
		slider = new JSlider( 8, 80 );
		slider.setPaintTicks( true );
		slider.setMajorTickSpacing( 8 );
		slider.setPaintLabels( true );
		slider.setValue( 24 );
		slider.addChangeListener( this );
		size = new JLabel( " "+slider.getValue() );
		pp.add( size );
		pp.add( slider );
		add( "North", pp );

		// 中央に、行番号付きテキストエリアを置く
		textarea = new LineNumberedTextArea( (String)(fontchoice.getSelectedItem()), slider.getValue());
		add( "Center", textarea.pane );

		
		// Fileメニュー、Editメニューをメニューバーに追加する
		menu1 = new Menu( "File" );
		for ( String item: new String [] {"Open", "Save" } ) {
			MenuItem mi1 = new MenuItem( item );
			mi1.setActionCommand( item );
			mi1.addActionListener( new MyActionAdapter( ) );
			menu1.add( mi1 );
		}
		menu2 = new Menu( "Edit" );
		for ( String item: new String [] {"Select All", "Cut", "Copy", "Paste", "Clear" } ) {
			MenuItem mi2 = new MenuItem( item );
			mi2.setActionCommand( item );
			mi2.addActionListener( new MyActionAdapter( ) );
			menu2.add( mi2 );
		}
		bar = new MenuBar ( );
		bar.add( menu1 );
		bar.add( menu2 );
		setMenuBar( bar );
		
		// クローズボックスを押されたら、ウィンドウを閉じて終了する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible( true );

		// 文字列検索機能
		JTextField search = new JTextField("文字列を入力", 10);
		class search implements ActionListener{
	    	public void actionPerformed(ActionEvent e){
	    		String keyword = search.getText();
	    		String text = textarea.textarea.getText().replaceAll("\r\n", "\n");
	    		String word = text.substring(0, text.length());
	    		textarea.textarea.setBackground(Color.WHITE);
	    		Highlighter highlighter = textarea.textarea.getHighlighter();
	    		highlighter.removeAllHighlights();
	    		int i = 0;
	    		while (i<word.length() ){
	    			if (i == word.indexOf((keyword), i)){
	    				String matched = text.substring(i, i + keyword.length());
	    				System.out.println(matched);
	    				try{
	    			    	highlighter.addHighlight(i, i + keyword.length(), highlightPainter);
	    				} catch(BadLocationException ep) {
	    					ep.printStackTrace();
	    				}
	    			}
	    			i++;
	    		}
	    	}
	    }
	    JButton btn = new JButton("検索");
	    btn.addActionListener(new search());
		pp2.add( new Label( "Search:") );
	    pp2.add(search);
	    pp2.add(btn);
	    
	 	// 文字置き換え機能
 		JTextField areplace = new JTextField("置き換え後の文字", 8);
 		JTextField breplace = new JTextField("置き換え前の文字", 8);
	 	class breplace implements ActionListener{
	 	    public void actionPerformed(ActionEvent e){
	 	    	String keyword1 = breplace.getText();
	 	   		String keyword2 = areplace.getText();
	 	    	String text = textarea.textarea.getText().replaceAll(keyword1, keyword2);
	 	   		textarea.textarea.setText("");
	 	   		textarea.textarea.setText(text);
	 	   	}
	 	}
	 	JButton btn2 = new JButton("置き換え");
	 	btn2.addActionListener(new breplace());
	 	pp2.add( new Label( "Replace:") );
	    pp2.add(breplace);
	    pp2.add(areplace);
	 	pp2.add(btn2);
	}

	// フォントの一覧を選択メニューに登録する
	void fontsetup() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font allfonts [] = ge.getAllFonts();
		for ( Font  f: allfonts ) {
			fontchoice.addItem( f.getName() );
		}
	}
	
	// 選択メニューが選ばれた場合
	@Override
	public void itemStateChanged(ItemEvent e) {
		int  s = slider.getValue();
		textarea.setRowHeight( (String)(fontchoice.getSelectedItem()), s );
		repaint();
	}

	// スライダーが動かされた場合
	@Override
	public void stateChanged(ChangeEvent e) {
		int  s = slider.getValue();
		size.setText( s+"" );
		textarea.setRowHeight( (String)(fontchoice.getSelectedItem()), s );
		repaint();
	}
	
	// メニューの項目が呼ばれた場合
	class MyActionAdapter implements ActionListener {
		@Override
		public void actionPerformed( ActionEvent ae ) {
			if (ae.getActionCommand() == "Open"){
				JFileChooser fc = new JFileChooser();
				fc.showOpenDialog(null);
				File file = fc.getSelectedFile();				
				try{
					@SuppressWarnings("resource")
					BufferedReader br = new BufferedReader(new FileReader(file));				
					String s;
					while ((s = br.readLine()) != null){
						String st = new String(s.getBytes("EUC_JP"), "EUC_JP");
						textarea.textarea.append(st + '\n');
					}
				}
				catch(IOException e){
					return;
				}
			} else if (ae.getActionCommand() == "Save") {
				JFileChooser fc = new JFileChooser();
				fc.showSaveDialog(null);
				File f = fc.getSelectedFile();					
				try{
					PrintWriter pw = new PrintWriter(new FileWriter(f,false));
					String s = textarea.textarea.getText();
					String st[] = s.split("\n");
					int limit = st.length;
					for (int i = 0;i < limit;i++){
						pw.println(st[i]);
					}
					pw.close();
				}
				catch(IOException e){
					return;
				}
			} else if (ae.getActionCommand() == "Select All") {
				textarea.textarea.selectAll();
			} else if (ae.getActionCommand() == "Cut") {
				textarea.textarea.cut();
			} else if (ae.getActionCommand() == "Copy") {
				textarea.textarea.copy();
			} else if (ae.getActionCommand() == "Paste") {
				textarea.textarea.paste();
			} else if (ae.getActionCommand() == "Clear") {
				textarea.textarea.setText("");
			}		
		}
	}
}