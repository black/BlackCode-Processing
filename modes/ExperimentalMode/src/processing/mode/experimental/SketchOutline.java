package processing.mode.experimental;

import static processing.mode.experimental.ExperimentalMode.log;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import processing.app.Base;

public class SketchOutline {
  protected JFrame frmOutlineView;

  protected ErrorCheckerService errorCheckerService;

  protected JScrollPane jsp;

  protected DefaultMutableTreeNode soNode, tempNode;

  protected final JTree soTree;

  protected JTextField searchField;

  protected DebugEditor editor;

  public SketchOutline(DefaultMutableTreeNode codeTree, ErrorCheckerService ecs) {
    errorCheckerService = ecs;
    editor = ecs.getEditor();
    frmOutlineView = new JFrame();
    frmOutlineView.setAlwaysOnTop(true);
    frmOutlineView.setUndecorated(true);
    Point tp = errorCheckerService.getEditor().ta.getLocationOnScreen();
//    frmOutlineView.setBounds(tp.x
//                                 + errorCheckerService.getEditor().ta
//                                     .getWidth() - 300, tp.y, 300,
//                             errorCheckerService.getEditor().ta.getHeight());

    //TODO: ^Absolute dimensions are bad bro

    int minWidth = (int) (editor.getMinimumSize().width * 0.7f), 
        maxWidth = (int) (editor.getMinimumSize().width * 0.9f);
    frmOutlineView.setLayout(new BoxLayout(frmOutlineView.getContentPane(),
                                           BoxLayout.Y_AXIS));
    JPanel panelTop = new JPanel(), panelBottom = new JPanel();
    panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
    panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.Y_AXIS));
    searchField = new JTextField();
    searchField.setMinimumSize(new Dimension(minWidth, 25));
    panelTop.add(searchField);

    jsp = new JScrollPane();
    soNode = new DefaultMutableTreeNode();
    generateSketchOutlineTree(soNode, codeTree);
    soNode = (DefaultMutableTreeNode) soNode.getChildAt(0);
    tempNode = soNode;
    soTree = new JTree(soNode);
    soTree.getSelectionModel()
        .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    soTree.setRootVisible(false);
    soTree.setCellRenderer(new CustomCellRenderer());
    for (int i = 0; i < soTree.getRowCount(); i++) {
      soTree.expandRow(i);
    }
    soTree.setSelectionRow(0);
    
    jsp.setViewportView(soTree);
    jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jsp.setMinimumSize(new Dimension(minWidth, editor.ta.getHeight() - 10));
    jsp.setMaximumSize(new Dimension(maxWidth, editor.ta.getHeight() - 10));    
    
    panelBottom.add(jsp);
    frmOutlineView.add(panelTop);
    frmOutlineView.add(panelBottom);
    frmOutlineView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frmOutlineView.pack();
    frmOutlineView.setBounds(tp.x
                                 + errorCheckerService.getEditor().ta
                                     .getWidth() - minWidth, tp.y, minWidth,
                             Math.min(editor.ta.getHeight(), frmOutlineView.getHeight()));
    frmOutlineView.setMinimumSize(new Dimension(minWidth, Math
        .min(errorCheckerService.getEditor().ta.getHeight(), frmOutlineView.getHeight())));    
    frmOutlineView.setLocation(tp.x
                                   + errorCheckerService.getEditor().ta
                                       .getWidth() - frmOutlineView.getWidth(),
                               frmOutlineView.getY()
                                   + (editor.ta.getHeight() - frmOutlineView
                                       .getHeight()) / 2);
    addListeners();

  }

  protected boolean internalSelection = false;

  protected void addListeners() {

    searchField.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent evt) {
        if (soTree.getRowCount() == 0)
          return;

        internalSelection = true;
        
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
         close();
        }
        else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
          if (soTree.getLastSelectedPathComponent() != null) {
            DefaultMutableTreeNode tnode = (DefaultMutableTreeNode) soTree
                .getLastSelectedPathComponent();
            if (tnode.getUserObject() instanceof ASTNodeWrapper) {
              ASTNodeWrapper awrap = (ASTNodeWrapper) tnode.getUserObject();
              errorCheckerService.highlightNode(awrap);
              close();
            }
          }
        } 
        else if (evt.getKeyCode() == KeyEvent.VK_UP) {
          if (soTree.getLastSelectedPathComponent() == null) {
            soTree.setSelectionRow(0);
            return;
          }
          
          int x = soTree.getLeadSelectionRow() - 1;
          int step = jsp.getVerticalScrollBar().getMaximum()
              / soTree.getRowCount();
          if (x == -1) {
            x = soTree.getRowCount() - 1;
            jsp.getVerticalScrollBar().setValue(jsp.getVerticalScrollBar().getMaximum());
          } else {
            jsp.getVerticalScrollBar().setValue((jsp.getVerticalScrollBar()
                                                    .getValue() - step));
          }
          soTree.setSelectionRow(x);
        } 
        else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
          if (soTree.getLastSelectedPathComponent() == null) {
            soTree.setSelectionRow(0);
            return;
          }
          int x = soTree.getLeadSelectionRow() + 1;

          int step = jsp.getVerticalScrollBar().getMaximum()
              / soTree.getRowCount();
          if (x == soTree.getRowCount()) {
            x = 0;
            jsp.getVerticalScrollBar().setValue(jsp.getVerticalScrollBar().getMinimum());
          } else {
            jsp.getVerticalScrollBar().setValue((jsp.getVerticalScrollBar()
                                                    .getValue() + step));
          }
          soTree.setSelectionRow(x);
        }
      }
    });
    
    searchField.getDocument().addDocumentListener(new DocumentListener() {

      public void insertUpdate(DocumentEvent e) {
        updateSelection();
      }

      public void removeUpdate(DocumentEvent e) {
        updateSelection();
      }

      public void changedUpdate(DocumentEvent e) {
        updateSelection();
      }
      
      private void updateSelection(){
        SwingWorker worker = new SwingWorker() {
          protected Object doInBackground() throws Exception {
            String text = searchField.getText().toLowerCase();
            tempNode = new DefaultMutableTreeNode();
            filterTree(text, tempNode, soNode);
            return null;
          }

          protected void done() {            
            soTree.setModel(new DefaultTreeModel(tempNode));
            ((DefaultTreeModel) soTree.getModel()).reload();
            for (int i = 0; i < soTree.getRowCount(); i++) {
              soTree.expandRow(i);
            }
            internalSelection = true;
            soTree.setSelectionRow(0);
          }
        };
        worker.execute();
      }
    });

    frmOutlineView.addWindowFocusListener(new WindowFocusListener() {
      public void windowLostFocus(WindowEvent e) {
        close();
      }

      public void windowGainedFocus(WindowEvent e) {
      }
    });

    soTree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {

        if (internalSelection) {
          internalSelection = (false);
          return;
        }
        // log(e);
        SwingWorker worker = new SwingWorker() {

          protected Object doInBackground() throws Exception {
            return null;
          }

          protected void done() {
            if (soTree.getLastSelectedPathComponent() == null) {
              return;
            }
            DefaultMutableTreeNode tnode = (DefaultMutableTreeNode) soTree
                .getLastSelectedPathComponent();
            if (tnode.getUserObject() instanceof ASTNodeWrapper) {
              ASTNodeWrapper awrap = (ASTNodeWrapper) tnode.getUserObject();
              // log(awrap);
              errorCheckerService.highlightNode(awrap);
            }
          }
        };
        worker.execute();
      }
    });
  }

  protected boolean filterTree(String prefix, DefaultMutableTreeNode tree,
                               DefaultMutableTreeNode mainTree) {
    if (mainTree.isLeaf()) {
      return (mainTree.getUserObject().toString().toLowerCase()
          .startsWith(prefix));
    }

    boolean found = false;
    for (int i = 0; i < mainTree.getChildCount(); i++) {
      DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(
                                                                ((DefaultMutableTreeNode) mainTree
                                                                    .getChildAt(i))
                                                                    .getUserObject());
      if (filterTree(prefix, tNode,
                     (DefaultMutableTreeNode) mainTree.getChildAt(i))) {
        found = true;
        tree.add(tNode);
      }
    }
    return found;
  }

  protected void generateSketchOutlineTree(DefaultMutableTreeNode node,
                                           DefaultMutableTreeNode codetree) {
    if (codetree == null)
      return;
    //log("Visi " + codetree + codetree.getUserObject().getClass().getSimpleName());
    if (!(codetree.getUserObject() instanceof ASTNodeWrapper))
      return;
    ASTNodeWrapper awnode = (ASTNodeWrapper) codetree.getUserObject(), aw2 = null;

    if (awnode.getNode() instanceof TypeDeclaration) {
      aw2 = new ASTNodeWrapper( ((TypeDeclaration) awnode.getNode()).getName(),
                               ((TypeDeclaration) awnode.getNode()).getName()
                                   .toString());
    } else if (awnode.getNode() instanceof MethodDeclaration) {
      aw2 = new ASTNodeWrapper(
                               ((MethodDeclaration) awnode.getNode()).getName(),
                               new CompletionCandidate(
                                                       ((MethodDeclaration) awnode
                                                           .getNode()))
                                   .toString());
    } else if (awnode.getNode() instanceof FieldDeclaration) {
      FieldDeclaration fd = (FieldDeclaration) awnode.getNode();
      for (VariableDeclarationFragment vdf : (List<VariableDeclarationFragment>) fd
          .fragments()) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
                                                                    new ASTNodeWrapper(
                                                                                       vdf.getName(),
                                                                                       new CompletionCandidate(
                                                                                                               vdf)
                                                                                           .toString()));
        node.add(newNode);
      }
      return;
    }
    if (aw2 == null)
      return;
    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(aw2);
    node.add(newNode);
    for (int i = 0; i < codetree.getChildCount(); i++) {
      generateSketchOutlineTree(newNode,
                                (DefaultMutableTreeNode) codetree.getChildAt(i));
    }
  }

  public void show() {
    frmOutlineView.setVisible(true);
  }
  
  public void close(){
    frmOutlineView.setVisible(false);
    frmOutlineView.dispose();
  }
  
  public boolean isVisible(){
    return frmOutlineView.isVisible();
  }
  
  protected class CustomCellRenderer extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel, boolean expanded, boolean leaf, int row,
        boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded,
          leaf, row, hasFocus);
      if (value instanceof DefaultMutableTreeNode)
        setIcon(getTreeIcon(value));

      return this;
    }

    public javax.swing.Icon getTreeIcon(Object o) {
      if (((DefaultMutableTreeNode) o).getUserObject() instanceof ASTNodeWrapper) {

        ASTNodeWrapper awrap = (ASTNodeWrapper) ((DefaultMutableTreeNode) o)
            .getUserObject();
        int type = awrap.getNode().getParent().getNodeType();
        if (type == ASTNode.METHOD_DECLARATION)
          return editor.dmode.methodIcon;
        if (type == ASTNode.TYPE_DECLARATION)
          return editor.dmode.classIcon;
        if (type == ASTNode.VARIABLE_DECLARATION_FRAGMENT)
          return editor.dmode.fieldIcon;
      }
      return null;
    }
  }
}
