

import  java.awt.*;

import  javax.swing.*;
import  javax.swing.event.*;
import  javax.swing.table.*;

public class LineNumberedTextArea {

	JScrollPane pane;
	JTextArea textarea;
	JTable rowheader;
	
	final int initialRowCount = 1;

	public LineNumberedTextArea( String fontname, int initialheight ) {
		textarea = new JTextArea( initialRowCount, 40 );
		textarea.addCaretListener( new TextAreaCaretListener() );
		
		rowheader = new JTable( initialRowCount, 1 );
		TableColumnModel tcm = rowheader.getColumnModel();
		TableColumn tc = tcm.getColumn( 0 );
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(SwingConstants.CENTER);
		tc.setCellRenderer(r);
		tc.setHeaderValue( "" );
		rowheader.setColumnModel( tcm );
		for ( int n=1; n <=rowheader.getRowCount(); n++ ) {
			rowheader.setValueAt( new Integer(n), n-1, 0);
		}
		setRowHeight(  fontname, initialheight );

		pane = new JScrollPane( textarea );
		pane.setRowHeaderView(rowheader);
		pane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowheader.getTableHeader());
		Dimension size = new Dimension(rowheader.getPreferredSize().width/2, textarea.getPreferredSize().height);
		pane.getRowHeader().setPreferredSize( size ); 
	}
	
	void  setRowHeight( String fontname, int size ) {
		Font  f = new Font( fontname, Font.PLAIN, size );
		textarea.setFont( f );
		FontMetrics  fm = textarea.getFontMetrics( f );
		rowheader.setRowHeight( fm.getHeight() );
	}
	
	class TextAreaCaretListener implements CaretListener {
		@Override
		public void caretUpdate(CaretEvent e) {
			int rows = textarea.getLineCount();
			int rrows = rowheader.getRowCount();
			//System.out.println( rows+":"+rrows );
			DefaultTableModel model = (DefaultTableModel)rowheader.getModel();
			if ( rows > rrows ) {
				// 足すときも、現在の行から
				// 行番号を付け替える
				int current = countRows( e.getDot() );
				int diff = rows - rrows;
				for ( int i=current; i < current+diff; i++ ) {
					model.insertRow( i, new Integer[] {new Integer(i+1)} );
				}
				rowheader.setModel( model );
				renumbering( current );
				rowheader.updateUI();
				pane.updateUI();
			} else if ( rows < rrows ){
				// e.getDot()から、現在の行を\nを数えることで判別
				// 現在の行から、余分な行数分だけ削除
				// 現在の行から、行番号を振り直す
				int current = countRows( e.getDot() );
				int diff = rrows - rows;
				//System.out.println( "cur:" + current + " diff:" + diff );
				for ( int i=current+diff-1; i >= current; i-- ) {
					model.removeRow( i );
				}			
				rowheader.setModel( model );
				renumbering( current );
				rowheader.updateUI();
				pane.updateUI();
			}
		}
	}
	
	int countRows( int position) {
		String  text = textarea.getText();
		String  lines [] = text.substring( 0, position ).split( "\n" );
		if ( lines == null ) { return 0; } else { return lines.length; }
	}
	
	void renumbering( int current ) {
		for ( int i=current; i < rowheader.getRowCount(); i++ ) {
			rowheader.setValueAt( new Integer(i+1), i, 0 );
		}
	}	
}
